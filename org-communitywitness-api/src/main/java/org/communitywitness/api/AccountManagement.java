package org.communitywitness.api;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashSet;
import java.util.Set;

import com.kosprov.jargon2.api.Jargon2;
import com.kosprov.jargon2.api.Jargon2.Hasher;
import com.kosprov.jargon2.api.Jargon2.Type;
import com.kosprov.jargon2.api.Jargon2.Verifier;

public class AccountManagement {
	// Text should be encoded in UTF-8 form
	private static final Charset TEXT_CHARSET = StandardCharsets.UTF_8;
	// The recommended argon2 type for password hashing
	private static final Jargon2.Type HASH_TYPE = Type.ARGON2i;
	/*
	 * The settings for the hashing algorithm, which where determined
	 * experimentally on the google cloud server with the aim of
	 * hashing a password taking around 0.5 seconds to ensure security.
	 * When running this on another system these values may need changing for security.
	 */
	private static final int HASH_MEMORY_COST = 131072;
	private static final int HASH_TIME_COST = 3;
	private static final int HASH_PARALLELISM = 4;
	private static final int HASH_SALT_LENGTH = 32;
	private static final int HASH_LENGTH = 32;
	// The number of bytes long an API key should be.
	// 24 bytes allows for 2^192 different keys, which is more than enough, 
	// and since 192 is divisible by 6 there is no need for padding in the Base64 form of the key.
	private static final int API_KEY_LENGTH = 24;

	/**
	 * Checks if a given password matches the hashed correct password.
	 * @param attemptedPassword the password to check for correctness
	 * @param correctPasswordHash the hashed and salted correct password, encoded in the form produced by {@link #hashPassword(String)}
	 * @return true if the attempted password matches the correct one, false if not
	 */
	public static boolean checkPassword(String attemptedPassword, String correctPasswordHash) {
		byte[] attemptedPasswordBytes = attemptedPassword.getBytes(TEXT_CHARSET);
		Verifier verifier = Jargon2.jargon2Verifier();
		
		return verifier.hash(correctPasswordHash).password(attemptedPasswordBytes).verifyEncoded();
	}
	
	/**
	 * Checks if the given username is available.
	 * @param username the username to check
	 * @return true if the username is available, false if it isn't
	 */
	public static boolean isUsernameAvailable(String username) {
		try {
			// Retrieve any potential matches from database
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			String query = "SELECT username FROM Accounts WHERE username=?";
			PreparedStatement queryStatement = dbConnection.prepareStatement(query);
			queryStatement.setString(1, username);
			ResultSet queryResults = queryStatement.executeQuery();
			
			Set<String> resultingNames = new HashSet<String>();
			while (queryResults.next()) {
				resultingNames.add(queryResults.getString(1));
			}
			
			if (resultingNames.contains(username))
				return false;
		} catch (SQLException exception) {
			// if there's trouble reading the database just assume the names taken
			return false;
		}
		
		return true;
	}
	
	/**
	 * Attempts to create an account for an investigator and save it to the database.
	 * @param username the username for the account
	 * @param password the password for the account
	 * @param investigatorId the id of the investigator this account is for
	 * @return true on success, false on failure which can occur if the username is taken, 
	 * a database failure occurs, or the given investigator id already has an account
	 */
	public static boolean createInvestigatorAccount(String username, String password, int investigatorId) {
		if (!isUsernameAvailable(username))
			return false;
		
		String hashedPassword = hashPassword(password);
		
		try {
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			
			// Check if this investigator id already has an account
			String existingAccountQuery = "SELECT InvestigatorId FROM Accounts WHERE InvestigatorId=?";
			PreparedStatement existingAccountStatement = dbConnection.prepareStatement(existingAccountQuery);
			existingAccountStatement.setInt(1, investigatorId);
			ResultSet existingAccountResult = existingAccountStatement.executeQuery();
			Set<Integer> existingIds = new HashSet<Integer>();
			while (existingAccountResult.next())
				existingIds.add(existingAccountResult.getInt(1));
			
			if (existingIds.contains(investigatorId))
				return false;
			
			// Write the account to the database
			String insertQuery = "INSERT INTO Accounts (Username, PasswordHash, InvestigatorId)" +
			"VALUES (?, ?, ?)";
			PreparedStatement insertStatement = dbConnection.prepareStatement(insertQuery);
			insertStatement.setString(1, username);
			insertStatement.setString(2, hashedPassword);
			insertStatement.setInt(3, investigatorId);
			
			if (insertStatement.executeUpdate() == 0)
				return false;
			else
				return true;
			
		} catch (SQLException exception) {
			return false;
		}
	}
	
	/**
	 * Creates an api key for a user and associates that key with the user in the database.
	 * @param id the id number of the user
	 * @param role the kind of user the api key is for, from the constants in UserRoles
	 * @return the new api key on success, or null on failure 
	 * which can be due to either the user already having a key, an invalid role being given, or no such user existing.
	 */
	public static String giveUserApiKey(int id, String role) {
		if (!UserRoles.isRole(role))
			return null;
		
		try {
			// Make sure the user exists, if they don't these constructors throw exceptions
			if (role.equalsIgnoreCase(UserRoles.INVESTIGATOR))
				new Investigator(id);
			else if (role.equalsIgnoreCase(UserRoles.WITNESS))
				new Witness(id);
			

			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			
			// Check if this user already has an api key
			String existingKeyQuery = "SELECT %s FROM ApiKeys WHERE %s=?";
			if (role.equalsIgnoreCase(UserRoles.INVESTIGATOR))
				existingKeyQuery = String.format(existingKeyQuery,"InvestigatorId", "InvestigatorId");
			else if (role.equalsIgnoreCase(UserRoles.WITNESS))
				existingKeyQuery = String.format(existingKeyQuery, "WitnessId", "WitnessId");
			
			PreparedStatement existingKeyStatement = dbConnection.prepareStatement(existingKeyQuery);
			existingKeyStatement.setInt(1, id);
			ResultSet existingKeyResults = existingKeyStatement.executeQuery();
			Set<Integer> existingIds = new HashSet<Integer>();
			while (existingKeyResults.next())
				existingIds.add(existingKeyResults.getInt(1));
			
			if (existingIds.contains(id))
				return null;
			
			// Generate an api key and add it to the database
			String apiKey = generateApiKey();
			String insertQuery = "INSERT INTO ApiKeys (ApiKey, WitnessId, InvestigatorId) VALUES (?, ?, ?)";
			PreparedStatement insertStatement = dbConnection.prepareStatement(insertQuery);
			insertStatement.setString(1, apiKey);
			if (role.equalsIgnoreCase(UserRoles.INVESTIGATOR)) {
				insertStatement.setNull(2, Types.INTEGER);
				insertStatement.setInt(3, id);
			} else if (role.equalsIgnoreCase(UserRoles.WITNESS)) {
				insertStatement.setInt(2, id);
				insertStatement.setNull(3, Types.INTEGER);
			}
			
			if (insertStatement.executeUpdate() == 0)
				return null;
			else
				return apiKey;
		} catch (SQLException exception) {
			return null;
		}
	}
	
	/**
	 * Hashes and salts the given password for safe storage in the database.
	 * @param password a plaintext password to hash and salt
	 * @return the hashed and salted password encoded as text in jargon2's format
	 */
	private static String hashPassword(String password) {
		byte[] passwordBytes = password.getBytes(TEXT_CHARSET);
		
		Hasher hasher = Jargon2.jargon2Hasher()
				.type(HASH_TYPE)
				.memoryCost(HASH_MEMORY_COST)
				.timeCost(HASH_TIME_COST)
				.parallelism(HASH_PARALLELISM)
				.saltLength(HASH_SALT_LENGTH)
				.hashLength(HASH_LENGTH);
		
		return hasher.password(passwordBytes).encodedHash();
	}
	
	/**
	 * Randomly generates an api key and returns it in the form of a string.
	 * @return the randomly generated key as a string
	 */
	private static String generateApiKey() {
		SecureRandom randomGenerator = new SecureRandom();
		byte[] keyBytes = new byte[API_KEY_LENGTH];
		
		randomGenerator.nextBytes(keyBytes);
		
		// Encode the random bytes in base64 so they can be transmitted as text
		Encoder base64Encoder = Base64.getEncoder();
		return base64Encoder.encodeToString(keyBytes);
	}
}
