package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	/**
	 * Returns all the reports in the database.
	 * @return a list of reports
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR})
	@GET
	public List<Report> queryReports() throws SQLException {
		Connection conn = new SQLConnection().databaseConnection();
		String query = "SELECT id, resolved, description, time, location, witnessID FROM report";
		ResultSet queryResults = conn.prepareStatement(query).executeQuery();

		ArrayList<Report> results = new ArrayList<>();

		while (queryResults.next()) {
			Report report = new Report(queryResults.getBoolean(2),
					queryResults.getString(3),
					queryResults.getTimestamp(4).toLocalDateTime(),
					queryResults.getString(5),
					queryResults.getInt(6));
			report.setId(queryResults.getInt(1));

			report.loadComments(conn);
			report.loadEvidence(conn);
			results.add(report);
		}

		// close out sql stuff
		queryResults.close();
		conn.close();

		return results;
	}

	/**
	 * Creates a new report from data sent from a client.
	 * @param newReportRequestData - object containing data for new report
	 * @return the id of the newly created report.
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@POST
	public int createReport(NewReportRequest newReportRequestData, @Context AuthenticatedUser user) throws WebApplicationException, SQLException {
		if (user.getId() != newReportRequestData.getWitnessId())
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You can't file a report as another witness."));
		
		Report newReport = new Report(
				false,
				newReportRequestData.getDescription(),
				newReportRequestData.getTime(),
				newReportRequestData.getLocation(),
				newReportRequestData.getWitnessId());
		return newReport.writeToDb();
	}
	
	/**
	 * Returns a report from the database to a client.
	 * @param reportId - the id of the report to send, which will be encoded in the request URL. 
	 * For example doing a GET on "ourApiUrl.com/reports/123" would send the report with id 123.
	 * @return the report with the given id.
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@GET
	@Path("/{reportId}")
	public Report getReport(@PathParam("reportId") int reportId, @Context AuthenticatedUser user) throws WebApplicationException {
		Report requestedReport;
		
		try {
			requestedReport = new Report(reportId);
			
			if (user.isUserInRole(UserRoles.WITNESS) && user.getId() != requestedReport.getWitnessId())
				throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("You can only access reports you filed."));
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedReport;
	}
	
	/**
	 * Updates a reports status to what the client specifies.
	 * @param reportId - the id of the report to update
	 * @param status - the new status of the report
	 * @return An OK status on success, otherwise a NOT_FOUND status when the report isn't found
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@PUT
	@Path("/{reportId}/{status}")
	public Response updateReportStatus(@PathParam("reportId") int reportId, @PathParam("status") boolean status, @Context AuthenticatedUser user) {
		Report toUpdate;
		
		try {
			toUpdate = new Report(reportId);
			
			if (user.getId() != toUpdate.getWitnessId())
				return AuthorizationFilter.unauthorizedAccessResponse("You can only modify the status of your own reports.");
			
			toUpdate.setResolved(status);
			toUpdate.writeToDb();
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		return Response.ok().build();
	}
}
