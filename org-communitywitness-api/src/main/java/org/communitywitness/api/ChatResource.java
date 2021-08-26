package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

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
     * @returns a list of messages
     */
	@RolesAllowed({UserRoles.INVESTIGATOR})
    @GET
    @Path("/investigator/{investigatorId}")
    public List<ChatMessage> investigatorMessages(@PathParam("investigatorId") int id, @Context AuthenticatedUser user) throws WebApplicationException, SQLException {
		if (user.getId() != id)
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You cannot view other investigators messages."));
		
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
    }

    /**
     * Returns all chat messages for the given witness
     * @returns a list of messages
     */
	@RolesAllowed({UserRoles.WITNESS})
    @GET
    @Path("/witness/{witnessId}")
    public List<ChatMessage> witnessMessages(@PathParam("witnessId") int id, @Context AuthenticatedUser user) throws SQLException {
		if (user.getId() != id)
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You cannot view other witnesses messages."));
		
        List<Integer> reportIdString = new Witness(id).getReports();

        Connection conn = new SQLConnection().databaseConnection();
        String query = String.format("SELECT id, reportid, investigatorid, message, time FROM chat WHERE reportid IN (%s)",
                reportIdString.stream()
                .map(v -> "?")
                .collect(Collectors.joining(", ")));
        PreparedStatement preparedStatement = conn.prepareStatement(query);

//        preparedStatement.setString(1, reportIdString);
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
    }

	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
    @PUT
    @Path("/{reportId}")
    public int addMessage(@PathParam("reportId") int reportId, ChatMessageRequest chatMessageRequest, @Context AuthenticatedUser user) throws WebApplicationException, SQLException {
		// Verify that the user is allowed to send this message
		if (user.isUserInRole(UserRoles.INVESTIGATOR) && user.getId() != chatMessageRequest.getInvestigatorId())
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You cannot send messages as another investigator."));
		
		if (user.isUserInRole(UserRoles.WITNESS)) {
			Report relevantReport = new Report(reportId);
			if (user.getId() != relevantReport.getWitnessId())
				throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You can only send messages regarding your reports."));
		}
		
        ChatMessage message = new ChatMessage(
                reportId,
                chatMessageRequest.getInvestigatorId(),
                chatMessageRequest.getMessage(),
                chatMessageRequest.getTime());
        return message.writeToDb();
    }
}


