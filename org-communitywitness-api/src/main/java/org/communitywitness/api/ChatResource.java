package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {
	/**
	 * Returns all chat messages for the given investigator
	 * @param id the ID number of the investigator whose messages to retrieve
	 * @param user the authentication data of the requesting user
	 * @return a list of messages involving the given investigator
	 * @throws WebApplicationException on authorization error or database reading error
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR})
	@GET
	@Path("/investigator/{investigatorId}")
	public List<ChatMessage> investigatorMessages(@PathParam("investigatorId") int id, @Context AuthenticatedUser user) throws WebApplicationException {
		if (user.getId() != id)
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Investigators may only view their own messages."));

		// try to retrieve the messages
		try {
			Connection conn = new SQLConnection().databaseConnection();
			String query = "SELECT id, reportid, investigatorid, message, time FROM chat WHERE investigatorid=?";
			PreparedStatement preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, id);
			ResultSet queryResults = preparedStatement.executeQuery();

			ArrayList<ChatMessage> messages = new ArrayList<>();

			while (queryResults.next()) {
				ChatMessage message = new ChatMessage(
						queryResults.getInt(2),
						queryResults.getInt(3),
						queryResults.getString(4),
						queryResults.getTimestamp(5).toLocalDateTime());

				message.setId(queryResults.getInt(1));

				messages.add(message);
			}

			// close out sql stuff
			queryResults.close();
			conn.close();

			return messages;
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Returns all chat messages for the given witness
	 * @param id the ID number of the witness whose messages to retrieve
	 * @param user the authentication data of the requesting user
	 * @return a list of messages involving the given witness
	 * @throws WebApplicationException on authorization errors or database read failures
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@GET
	@Path("/witness/{witnessId}")
	public List<ChatMessage> witnessMessages(@PathParam("witnessId") int id, @Context AuthenticatedUser user) throws WebApplicationException {
		if (user.getId() != id)
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses may only view their own messages."));

		// try to retrieve the messages
		try {
			List<Integer> reportIdString = new Witness(id).getReports();

			Connection conn = new SQLConnection().databaseConnection();
			String query = String.format("SELECT id, reportid, investigatorid, message, time FROM chat WHERE reportid IN (%s)",
					reportIdString.stream()
					.map(v -> "?")
					.collect(Collectors.joining(", ")));
			PreparedStatement preparedStatement = conn.prepareStatement(query);

			for (int i=1; i<= reportIdString.size(); i++) {
				preparedStatement.setInt(i, reportIdString.get(i-1));
			}
			ResultSet queryResults = preparedStatement.executeQuery();

			ArrayList<ChatMessage> messages = new ArrayList<>();

			while (queryResults.next()) {
				ChatMessage message = new ChatMessage(
						queryResults.getInt(2),
						queryResults.getInt(3),
						queryResults.getString(4),
						queryResults.getTimestamp(5).toLocalDateTime());

				message.setId(queryResults.getInt(1));

				messages.add(message);
			}

			// close out sql stuff
			queryResults.close();
			conn.close();

			return messages;
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Writes a chat message to the database.
	 * @param reportId the ID of the report this message is addressing
	 * @param chatMessageRequest the data about and contents of the message
	 * @param user the authentication data of the requesting user
	 * @return the id of the newly created message
	 * @throws WebApplicationException on authorization failure, 
	 * if the message doesn't address a valid report, or database writing fails.
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@PUT
	@Path("/{reportId}")
	public int addMessage(@PathParam("reportId") int reportId, ChatMessageRequest chatMessageRequest, @Context AuthenticatedUser user) throws WebApplicationException {
		// Verify that the user is allowed to send this message
		if (user.isUserInRole(UserRoles.INVESTIGATOR) && user.getId() != chatMessageRequest.getInvestigatorId())
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Investigators may only send messages as themselves."));

		try {
			if (user.isUserInRole(UserRoles.WITNESS)) {
				Report relevantReport = new Report(reportId);
				if (user.getId() != relevantReport.getWitnessId())
					throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses may only send messages regarding their reports."));
			}
		} catch (SQLException exception) {
			throw new WebApplicationException("Messages must address existing reports.");
		}


		// try to write the message
		try {
			ChatMessage message = new ChatMessage(
					reportId,
					chatMessageRequest.getInvestigatorId(),
					chatMessageRequest.getMessage(),
					chatMessageRequest.getTime());
			return message.writeToDb();
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to write message to database.");
		}
	}
}


