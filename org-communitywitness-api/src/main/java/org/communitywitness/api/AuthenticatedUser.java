package org.communitywitness.api;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.communitywitness.common.SpecialIds;

import jakarta.ws.rs.core.SecurityContext;

public class AuthenticatedUser implements SecurityContext {
	private static final String AUTHENTICATION_SCHEME = "API_KEY";
	private int id;
	private String role;
	private Principal principal;

	/**
	 * Authenticates a user based on their api key.
	 * @param apiKey the api key sent by the user
	 * @throws BadLoginException if the user couldn't be authenticated
	 */
	public AuthenticatedUser(String apiKey) throws BadLoginException {
		// Check the api key against the database
		try {
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			String query = "SELECT WitnessId, InvestigatorId FROM ApiKeys WHERE ApiKey=?";
			PreparedStatement queryStatement = dbConnection.prepareStatement(query);
			queryStatement.setString(1, apiKey);
			ResultSet queryResults = queryStatement.executeQuery();
			
			int witnessId;
			int investigatorId;
			if (queryResults.next()) {
				witnessId = queryResults.getInt(1);
				investigatorId = queryResults.getInt(2);
			} else {
				throw new SQLException();
			}
			
			// Determine the users role
			if (witnessId == SpecialIds.USER_NOT_IN_ROLE && !SpecialIds.isSpecialId(investigatorId)) {
				setRole(UserRoles.INVESTIGATOR);
				setId(investigatorId);
			} else if (investigatorId == SpecialIds.USER_NOT_IN_ROLE && !SpecialIds.isSpecialId(witnessId)) {
				setRole(UserRoles.WITNESS);
				setId(witnessId);
			} else {
				throw new BadLoginException("User has bad role data.");
			}
		} catch (SQLException exception) {
			throw new BadLoginException("Error retrieving data from database.");
		}
		
		setPrincipal();
	}
	
	/**
	 * Authenticates an investigator based on their username and password.
	 * @param username the username sent by the user
	 * @param password the password sent by the user
	 * @throws BadLoginException if the user couldn't be authenticated
	 */
	public AuthenticatedUser(String username, String password) throws BadLoginException {
		setRole(UserRoles.INVESTIGATOR);

		// Check the login details against the database
		try {
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			String query = "SELECT PasswordHash, InvestigatorId" +
					"FROM Accounts WHERE Username=?";
			PreparedStatement queryStatement = dbConnection.prepareStatement(query);
			queryStatement.setString(1, username);
			ResultSet queryResults = queryStatement.executeQuery();

			
			String passwordHash;
			if (queryResults.next()) {
				passwordHash = queryResults.getString(1);
				setId(queryResults.getInt(2));
			} else {
				throw new SQLException();
			}
			
			// Check that the given password matches the db entry
			if (!AccountManagement.checkPassword(passwordHash, password))
				throw new BadLoginException("Incorrect password.");
			
			// Check that the id is valid
			if (SpecialIds.isSpecialId(id))
				throw new BadLoginException("Invalid account id.");
		} catch (SQLException exception) {
			throw new BadLoginException("Error retrieving data from database.");
		}
		
		setPrincipal();
	}

	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (role.equalsIgnoreCase(this.role))
			return true;
		else
			return false;
	}

	@Override
	public boolean isSecure() {
		// Assume the connection is secure if it's going over https
		return Server.getBaseUri().toLowerCase().startsWith("https");
	}

	@Override
	public String getAuthenticationScheme() {
		return AUTHENTICATION_SCHEME;
	}

	/**
	 * Returns the role of this user, either witness or investigator.
	 * @return this.role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of this user.
	 * @param role the appropriate role constant from the UserRoles class
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Returns the id number of this user in either the witness or investigator table,
	 * according to their role.
	 * @return this.id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this user.
	 * @param id the id number of this user in either the witness or investigator table
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Creates a security principle that simply contains the users id as both their name and hash.
	 */
	public void setPrincipal() {
		this.principal = new Principal() {
			public String getName() {
				return String.valueOf(id);
			}
			
			// Setting the hash as the id makes it accessible directly as an int for downstream clients.
			public int hashCode() {
				return id;
			}
		};
	}
}
