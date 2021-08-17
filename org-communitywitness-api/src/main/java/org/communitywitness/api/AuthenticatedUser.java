package org.communitywitness.api;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.communitywitness.common.SpecialIds;

import jakarta.ws.rs.core.SecurityContext;

public class AuthenticatedUser implements SecurityContext {
	private String username;
	private String role;
	private int id;
	private Principal principal;

	public AuthenticatedUser(String username, String password) throws BadLoginException {
		setUsername(username);
		setPrincipal();

		// Check the login details against the database
		try {
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			String query = "SELECT Username, Password, Salt, WitnessId" +
					"InvestigatorId FROM Account WHERE Username=?";
			PreparedStatement queryStatement = dbConnection.prepareStatement(query);
			queryStatement.setString(1, username);
			ResultSet queryResults = queryStatement.executeQuery();

			
			String passwordInDb;
			String salt;
			int witnessId;
			int investigatorId;
			if (queryResults.next()) {
				passwordInDb = queryResults.getString(2);
				salt = queryResults.getString(3);
				witnessId = queryResults.getInt(4);
				investigatorId = queryResults.getInt(5);
			}
			
			// Check that the given password matches the db entry
			String hashedPassword = AccountManagement.hashPassword(password, salt);
			if (!hashedPassword.equals(passwordInDb))
				throw new BadLoginException("Incorrect password.");
			
			// Determine the users role
			if (witnessId == SpecialIds.UNSET_ID && investigatorId == SpecialIds.UNSET_ID) {
				throw new BadLoginException("User has no role.");
			} else if (witnessId == SpecialIds.UNSET_ID) {
				setRole(UserRoles.INVESTIGATOR);
				setId(investigatorId);
			} else if (investigatorId == SpecialIds.UNSET_ID) {
				setRole(UserRoles.WITNESS);
				setId(witnessId);
			} else {
				throw new BadLoginException("User has multiple roles.");
			}
		} catch (SQLException exception) {
			throw new BadLoginException("Error retrieving data from database.");
		}
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
		return SecurityContext.BASIC_AUTH;
	}

	/**
	 * Returns the username of this user, where witness usernames match their id,
	 * and investigator usernames are text set by the investigator.
	 * @return this.username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of this user.
	 * @param username either a witness id number or an investigators custom username
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * Creates a security principle that simply contains the users username.
	 */
	public void setPrincipal() {
		this.principal = new Principal() {
			public String getName() {
				return username;
			}
		};
	}
}
