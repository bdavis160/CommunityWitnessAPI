package org.communitywitness.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

@PermitAll
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {
	
	/**
	 * Logs in an investigator by checking the credentials they provided against
	 * those in the database and sending them their api key if they have correct credentials.
	 * @param credentials the investigators username and password
	 * @return the investigators api key
	 * @throws WebApplicationException if a database failure occurs or incorrect credentials are given
	 */
	@POST
	public String login(LoginDetails credentials) throws WebApplicationException {
		try {
			SQLConnection myConnection = new SQLConnection();
			Connection dbConnection = myConnection.databaseConnection();
			
			// Make sure the given credentials match those in the database
			String accountQuery = "SELECT PasswordHash, InvestigatorId FROM Accounts WHERE Username=?";
			PreparedStatement accountStatement = dbConnection.prepareStatement(accountQuery);
			accountStatement.setString(1, credentials.getUsername());
			ResultSet accountResults = accountStatement.executeQuery();
			
			String passwordHash;
			int investigatorId;
			if (accountResults.next()) {
				passwordHash = accountResults.getString(1);
				investigatorId = accountResults.getInt(2);
			} else {
				throw new SQLException();
			}
			
			if (!AccountManagement.checkPassword(credentials.getPassword(), passwordHash))
				throw new WebApplicationException("Incorrect password.");
			
			// Password was correct, retrieve API key
			String apiKeyQuery = "SELECT ApiKey FROM ApiKeys WHERE InvestigatorId=?";
			PreparedStatement apiKeyStatement = dbConnection.prepareStatement(apiKeyQuery);
			apiKeyStatement.setInt(1, investigatorId);
			ResultSet apiKeyResults = apiKeyStatement.executeQuery();
			
			if (apiKeyResults.next())
				return apiKeyResults.getString(1);
			else
				throw new SQLException();
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to retrieve data from database.");
		}
	}
}
