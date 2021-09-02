package org.communitywitness.api;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Properties;

import org.apache.commons.validator.routines.UrlValidator;

import com.kosprov.jargon2.api.Jargon2;
import com.kosprov.jargon2.api.Jargon2.Type;

public class Settings {
	// Singleton instance data
	private static Settings instance = null;
	private static boolean settingsLoaded = false;
	
	// The kinds of URI schemes that are allowed for the base URI and cross origin URIs
	private final String[] ALLOWED_URI_SCHEMES = {"http", "https"}; 
	
	// Runtime setting values
	private URI baseUri;
	private String tlsKeyStoreFile;
	private String tlsKeyStorePassword;
	private String allowedCrossOrigin;
	private Type passwordHashType;
	private int passwordHashMemoryCost;
	private int passwordHashTimeCost;
	private int passwordHashParallelism;
	private int passwordHashSaltLength;
	private int passwordHashLength;
	private int apiKeyLength;
	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;
	
	// Default setting values
	private final URI DEFAULT_BASE_URI = URI.create("http://127.0.0.1:8080");
	private final String DEFAULT_TLS_KEYSTORE_FILE = "none";
	private final String DEFAULT_TLS_KEYSTORE_PASSWORD = "";
	private final String DEFAULT_ALLOWED_CROSS_ORIGIN = "*";
	private final Jargon2.Type DEFAULT_PASSWORD_HASH_TYPE = Type.ARGON2i;
	private final int DEFAULT_PASSWORD_HASH_MEMORY_COST = 131072;
	private final int DEFAULT_PASSWORD_HASH_TIME_COST = 3;
	private final int DEFAULT_PASSWORD_HASH_PARALLELISM = 4;
	private final int DEFAULT_PASSWORD_HASH_SALT_LENGTH = 32;
	private final int DEFAULT_PASSWORD_HASH_LENGTH = 32;
	private final int DEFAULT_API_KEY_LENGTH = 24;
	private final String DEFAULT_DATABASE_URL = "jdbc:postgresql://commdbserver.ddns.net/cw_primary";
	private final String DEFAULT_DATABASE_USERNAME = "postgres";
	private final String DEFAULT_DATABASE_PASSWORD = "cwdefpass";
	
	/**
	 * Loads user specified settings from a file and applies the valid ones,
	 * while falling back to default values for invalid values.
	 * Note that this can only be called once, 
	 * so that once settings are loaded the Settings act like constants instead of global state.
	 * @param settingsFilename the path of the properties-style text file containing user settings
	 */
	public static void loadSettings(String settingsFilename) {
		// Settings should only ever be loaded once to avoid the problems that global state introduces (like concurrency issues)
		if (settingsLoaded)
			return;
		else
			settingsLoaded = true;
		
		if (instance == null)
			instance = new Settings();
		
		// Try to read settings from the file
		Properties userSettings = new Properties();
		try {
			FileInputStream settingsFile = new FileInputStream(settingsFilename);
			userSettings.load(settingsFile);
		} catch (Exception exception) {
			// If there's trouble reading or parsing the settings file just leave them as their defaults
			System.err.println(String.format("Problem occured while reading '%s', using default settings.", 
					settingsFilename));
			return;
		}
		
		// Set the settings read from the file, letting setters handle invalid inputs
		instance.setBaseUri(userSettings.getProperty("baseUri"));
		instance.setTlsKeyStorePassword("tlsKeyStorePassword"); // out of order so setTlsKeyStoreFile has it
		instance.setTlsKeyStoreFile("tlsKeyStoreFile");
		instance.setAllowedCrossOrigin(userSettings.getProperty("allowedCrossOrigin"));
		instance.setPasswordHashType(userSettings.getProperty("passwordHashType"));
		instance.setPasswordHashMemoryCost(userSettings.getProperty("passwordHashMemoryCost"));;
		instance.setPasswordHashTimeCost(userSettings.getProperty("passwordHashTimeCost"));;
		instance.setPasswordHashParallelism(userSettings.getProperty("passwordHashParallelism"));
		instance.setPasswordHashSaltLength(userSettings.getProperty("passwordHashSaltLength"));
		instance.setPasswordHashLength(userSettings.getProperty("passwordHashLength"));
		instance.setApiKeyLength(userSettings.getProperty("apiKeyLength"));
		instance.setDatabaseUrl(userSettings.getProperty("databaseUrl"));
		instance.setDatabaseUsername(userSettings.getProperty("databaseUsername"));
		instance.setDatabasePassword(userSettings.getProperty("databasePassword"));
	}
	
	/**
	 * Returns whether or not TLS is enabled for this server. 
	 * TLS is considered enabled if baseUri starts with https 
	 * and a valid tlsKeyStoreFile is specified.
	 * @return true if TLS is enabled, false otherwise
	 */
	public boolean isTlsEnabled() {
		return baseUri.toString().toLowerCase().startsWith("https") && 
				!tlsKeyStoreFile.isBlank() && !tlsKeyStoreFile.equalsIgnoreCase("none"); 
	}

	/**
	 * Returns the URI that the server listens for network connections on.
	 * @return this.baseUri
	 */
	public URI getBaseUri() {
		return baseUri;
	}
	
	/**
	 * Returns the name of the KeyStore file containing the TLS certificate and private key
	 * for this server.
	 * @return this.tlsKeyStoreFile
	 */
	public String getTlsKeyStoreFile() {
		return tlsKeyStoreFile;
	}
	
	/**
	 * Returns the password of the KeyStore file containing the TLS certificate and private key.
	 * @return this.tlsKeyStorePassword
	 */
	public String getTlsKeyStorePassword() {
		return tlsKeyStorePassword;
	}

	/**
	 * Returns the foreign origin(s) that is allowed to send cross-origin requests to the server,
	 * where * means any origin.
	 * @return this.allowedCrossOrigin
	 */
	public String getAllowedCrossOrigin() {
		return allowedCrossOrigin;
	}

	/**
	 * Returns the variant of the argon2 hashing algorithm that is being used to hash passwords.
	 * For details on the argon2 variants see argon2's documentation.
	 * @return this.passwordHashType
	 */
	public Type getPasswordHashType() {
		return passwordHashType;
	}

	/**
	 * Returns the memory cost in kibibytes that is being used to hash passwords. 
	 * @return this.passwordhashMemoryCost
	 */
	public int getPasswordHashMemoryCost() {
		return passwordHashMemoryCost;
	}

	/**
	 * Returns the time cost, or number of iterations, that is being used to hash passwords.
	 * @return this.passwordHashTimeCost
	 */
	public int getPasswordHashTimeCost() {
		return passwordHashTimeCost;
	}

	/**
	 * Returns the number of threads used for password hashing. 
	 * @return this.passwordHashParallelism
	 */
	public int getPasswordHashParallelism() {
		return passwordHashParallelism;
	}

	/**
	 * Returns the length in bytes of the salts used for password hashing.
	 * @return this.passwordHashSaltLength
	 */
	public int getPasswordHashSaltLength() {
		return passwordHashSaltLength;
	}

	/**
	 * Returns the length in bytes of the hash of the password on its own.
	 * @return this.passwordHashLength
	 */
	public int getPasswordHashLength() {
		return passwordHashLength;
	}

	/**
	 * Returns the length in bytes of API keys.
	 * @return this.apiKeyLength
	 */
	public int getApiKeyLength() {
		return apiKeyLength;
	}

	/**
	 * Returns the JDBC URL of the backing SQL database.
	 * @return this.databaseUrl
	 */
	public String getDatabaseUrl() {
		return databaseUrl;
	}

	/**
	 * Returns the username used to authenticate with the backing database.
	 * @return this.databaseUsername
	 */
	public String getDatabaseUsername() {
		return databaseUsername;
	}

	/**
	 * Returns the password used to authenticate with the backing database.
	 * @return this.databasePassword
	 */
	public String getDatabasePassword() {
		return databasePassword;
	}

	/**
	 * Returns the current singleton instance of Settings, 
	 * and constructs one with the default settings if there is no current instance.
	 * @return this.instance
	 */
	public static Settings getInstance() {
		if (instance == null)
			instance = new Settings();
		
		return instance;
	}
	
	/**
	 * Constructs the settings object using all default settings.
	 */
	private Settings() {
		baseUri = DEFAULT_BASE_URI;
		allowedCrossOrigin = DEFAULT_ALLOWED_CROSS_ORIGIN;
		passwordHashType = DEFAULT_PASSWORD_HASH_TYPE;
		passwordHashMemoryCost = DEFAULT_PASSWORD_HASH_MEMORY_COST;
		passwordHashTimeCost = DEFAULT_PASSWORD_HASH_TIME_COST;
		passwordHashParallelism = DEFAULT_PASSWORD_HASH_PARALLELISM;
		passwordHashSaltLength = DEFAULT_PASSWORD_HASH_SALT_LENGTH;
		passwordHashLength = DEFAULT_PASSWORD_HASH_LENGTH;
		apiKeyLength = DEFAULT_API_KEY_LENGTH;
		databaseUrl = DEFAULT_DATABASE_URL;
		databaseUsername = DEFAULT_DATABASE_USERNAME;
		databasePassword = DEFAULT_DATABASE_PASSWORD;
	}
	
	/**
	 * Tries to set the URI that the server should listen for connections on.
	 * Note that even if a URI passes validation the server may not actually be able to use it,
	 * for example while 'http://localhost' is a valid URI the server won't be able to use it 
	 * without the proper network permissions on Linux and thus will fail despite the URI being valid.
	 * @param baseUri the string form of the URI to try and use as the new base URI
	 */
	private void setBaseUri(String baseUri) {
		UrlValidator validator = new UrlValidator(ALLOWED_URI_SCHEMES);
		
		if (validator.isValid(baseUri)) {
			this.baseUri = URI.create(baseUri);
		} else {
			logInvalidSetting("URI", baseUri);
			this.baseUri = DEFAULT_BASE_URI;
		}
	}
	
	/**
	 * Tries to set the location of the KeyStore containing the TLS certificate and key for this server.
	 * @param tlsKeyStoreFile the file path of the TLS KeyStore for this server,
	 * or "none" or a blank string if TLS is not being used.
	 */
	private void setTlsKeyStoreFile(String tlsKeyStoreFile) {
		// Interpret empty strings or "none" as not using TLS
		if (tlsKeyStoreFile.equalsIgnoreCase("none") || tlsKeyStoreFile.isBlank())
			this.tlsKeyStoreFile = tlsKeyStoreFile;
		
		// Try to open the file as a KeyStore to check its validity
		try {
			File keyStoreFile = new File(tlsKeyStoreFile);
			KeyStore.getInstance(keyStoreFile, tlsKeyStorePassword.toCharArray());
			this.tlsKeyStoreFile = tlsKeyStoreFile;
		} catch (Exception exception) {
			logInvalidSetting("TLS KeyStore File", tlsKeyStoreFile);
			this.tlsKeyStoreFile = DEFAULT_TLS_KEYSTORE_FILE;
		}
	}
	
	/**
	 * Tries to set the password of the TLS KeyStore for this server.
	 * @param tlsKeyStorePassword the password for the TLS KeyStore
	 */
	private void setTlsKeyStorePassword(String tlsKeyStorePassword) {
		if (tlsKeyStorePassword != null)
			this.tlsKeyStorePassword = tlsKeyStorePassword;
		else
			this.tlsKeyStorePassword = DEFAULT_TLS_KEYSTORE_PASSWORD;
	}
	
	
	/**
	 * Sets the foreign origin(s) that is allowed to send cross-origin requests to the server,
	 * where * means any origin.
	 * @param allowedCrossOrigin the uri of the allowed foreign origin or * for any origin
	 */
	private void setAllowedCrossOrigin(String allowedCrossOrigin) {
		UrlValidator validator = new UrlValidator(ALLOWED_URI_SCHEMES);
		
		if (allowedCrossOrigin.equals("*") || validator.isValid(allowedCrossOrigin)) {
			this.allowedCrossOrigin = allowedCrossOrigin;
		} else {
			logInvalidSetting("Cross-Origin", allowedCrossOrigin);
			this.allowedCrossOrigin = allowedCrossOrigin;
		}
	}
	
	/**
	 * Sets the variant of the argon2 hashing algorithm used for hashing passwords.
	 * See the argon2 documentation online to learn about the differences.
	 * @param passwordHashType a string containing "argon2i", "argon2d", or "argon2id", the available argon types
	 */
	private void setPasswordHashType(String passwordHashType) {
		if (passwordHashType.equalsIgnoreCase("argon2i")) {
			this.passwordHashType = Type.ARGON2i;
		} else if (passwordHashType.equalsIgnoreCase("argon2d")) {
			this.passwordHashType = Type.ARGON2d;
		} else if (passwordHashType.equalsIgnoreCase("argon2id")) {
			this.passwordHashType = Type.ARGON2id;
		} else {
			logInvalidSetting("Password Hash Type", passwordHashType);
			this.passwordHashType = DEFAULT_PASSWORD_HASH_TYPE;
		}
	}
	
	/**
	 * Sets the memory cost in kibibytes of hashing passwords.
	 * This, along with the time cost, should be tuned such that hashing a password takes around 500 ms for security.
	 * To do this, try experimenting with the argon2 utility on the command line on your server system.
	 * @param passwordHashMemoryCost the number of kibibytes for the memory cost, as a string
	 */
	private void setPasswordHashMemoryCost(String passwordHashMemoryCost) {
		try {
			this.passwordHashMemoryCost = Integer.valueOf(passwordHashMemoryCost);
		} catch (NumberFormatException exception) {
			logInvalidSetting("Password Hash Memory Cost", passwordHashMemoryCost);
			this.passwordHashMemoryCost = DEFAULT_PASSWORD_HASH_MEMORY_COST;
		}
	}
	
	/**
	 * Sets the time cost, or number of iterations, used to hash passwords.
	 * This, along with the memory cost, should be tuned such that hashing a password takes around 500 ms for security,
	 * although this value is less important than the memory cost.
	 * To tune these values, try experimenting with the argon2 utility on the command line on your server system.
	 * @param passwordHashTimeCost the number of hash iterations as a string
	 */
	private void setPasswordHashTimeCost(String passwordHashTimeCost) {
		try {
			this.passwordHashTimeCost = Integer.valueOf(passwordHashTimeCost);
		} catch (NumberFormatException exception) {
			logInvalidSetting("Password Hash Time Cost", passwordHashTimeCost);
			this.passwordHashTimeCost = DEFAULT_PASSWORD_HASH_TIME_COST;
		}
	}
	
	/**
	 * Sets the number of threads used to hash passwords.
	 * This should either be set to 2x the threads on your system, or based on your resource constraints.
	 * @param passwordHashParallelism the number of threads for password hashing as a string
	 */
	private void setPasswordHashParallelism(String passwordHashParallelism) {
		try {
			this.passwordHashParallelism = Integer.valueOf(passwordHashParallelism);
		} catch (NumberFormatException exception) {
			logInvalidSetting("Password Hash Parallelism", passwordHashParallelism);
			this.passwordHashParallelism = DEFAULT_PASSWORD_HASH_PARALLELISM;
		}
	}
	
	/**
	 * Sets the length in bytes of the salts used for password hashing.
	 * This should be set based on your storage and security requirements.
	 * @param passwordHashSaltLength the length in bytes of salts as a string
	 */
	private void setPasswordHashSaltLength(String passwordHashSaltLength) {
		try {
			this.passwordHashSaltLength = Integer.valueOf(passwordHashSaltLength);
		} catch (NumberFormatException exception) {
			logInvalidSetting("Password Salt Length", passwordHashSaltLength);
			this.passwordHashSaltLength = DEFAULT_PASSWORD_HASH_SALT_LENGTH;
		}
	}
	
	/**
	 * Sets the length in bytes of the hash of the password on its own.
	 * This should be set based on your storage and security requirements.
	 * @param passwordHashLength the length in bytes of the password hash as a string
	 */
	private void setPasswordHashLength(String passwordHashLength) {
		try {
			this.passwordHashLength = Integer.valueOf(passwordHashLength);
		} catch (NumberFormatException exception) {
			logInvalidSetting("Password Hash Length", passwordHashLength);
			this.passwordHashLength = DEFAULT_PASSWORD_HASH_LENGTH;
		}
	}
	
	/**
	 * Sets the length in bytes of API keys.
	 * This should be set based on your storage requirements and the number of users you expect.
	 * @param apiKeyLength the length in bytes for API keys as a string
	 */
	private void setApiKeyLength(String apiKeyLength) {
		try {
			this.apiKeyLength = Integer.valueOf(apiKeyLength);
		} catch (NumberFormatException exception) {
			logInvalidSetting("API Key Length", apiKeyLength);
			this.apiKeyLength = DEFAULT_API_KEY_LENGTH;
		}
	}
	
	/**
	 * Sets the location of your database.
	 * This should be set to match your database and your database should have the Community Witness schema.
	 * It's recommended that you use PostgreSQL for your database, as that is what this was tested on.
	 * @param databaseUrl the URL of your database in JDBC format, e.g. "jdbc:postgresql://localhost/community_witness"
	 */
	private void setDatabaseUrl(String databaseUrl) {
		if (databaseUrl != null && databaseUrl.toLowerCase().startsWith("jdbc:")) {
			this.databaseUrl = databaseUrl;
		} else {
			logInvalidSetting("Database URL", databaseUrl);
			this.databaseUrl = DEFAULT_DATABASE_URL;
		}
	}
	
	/**
	 * Sets the username used to login to the database.
	 * @param databaseUsername the username used to login, read, and write to the backing database
	 */
	private void setDatabaseUsername(String databaseUsername) {
		if (databaseUsername != null && !databaseUsername.isBlank()) {
			this.databaseUsername = databaseUsername;
		} else {
			logInvalidSetting("Database Username", databaseUsername);
			this.databaseUsername = DEFAULT_DATABASE_USERNAME;
		}
	}
	
	/**
	 * Sets the password used to login to the database.
	 * @param databasePassword the password used to login, read, and write to the backing database
	 */
	private void setDatabasePassword(String databasePassword) {
		if (databasePassword != null && !databasePassword.isBlank()) {
			this.databasePassword = databasePassword;
		} else {
			logInvalidSetting("Database Password", databasePassword);
			this.databasePassword = DEFAULT_DATABASE_PASSWORD;
		}
	}
	
	/**
	 * Logs to stderr that an invalid setting was given and the setting will fall back to default.
	 * @param settingDescriptor a descriptor of the kind of setting, or the settings name
	 * @param givenValue the value of the invalid setting as a string
	 */
	private void logInvalidSetting(String settingDescriptor, String givenValue) {
		// Assume that null values where just not specified, so don't bother the user about it
		if (givenValue != null)
			System.err.println(String.format("%s '%s' is invalid, falling back to default.", 
					settingDescriptor, givenValue));
	}
	
	
}
