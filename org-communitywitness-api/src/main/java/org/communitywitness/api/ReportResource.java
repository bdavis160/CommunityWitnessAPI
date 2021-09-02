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
	public List<Report> queryReports() throws WebApplicationException {

		try {
			Connection conn = SQLConnection.databaseConnection();
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
			SQLConnection.closeDbOperation(conn, null, queryResults);

			return results;
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Creates a new report from data sent from a client.
	 * @param newReportRequestData object containing data for new report
	 * @param user the authentication data of the requesting user
	 * @return the id of the newly created report
	 * @throws WebApplicationException on authorization failure or database writing failure
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@POST
	public int createReport(NewReportRequest newReportRequestData, @Context AuthenticatedUser user) throws WebApplicationException {
		if (user.getId() != newReportRequestData.getWitnessId())
			throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses may only file reports as themselves."));

		try {
			Report newReport = new Report(
					false,
					newReportRequestData.getDescription(),
					newReportRequestData.getTime(),
					newReportRequestData.getLocation(),
					newReportRequestData.getWitnessId());
			return newReport.writeToDb();
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to write report to database.");
		}
	}

	/**
	 * Returns a report from the database to a client.
	 * @param reportId the id of the report to send
	 * @param user the authentication data of the requesting user
	 * @return the report with the given id
	 * @throws WebApplicationException on authorization failure or database read failure
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@GET
	@Path("/{reportId}")
	public Report getReport(@PathParam("reportId") int reportId, @Context AuthenticatedUser user) throws WebApplicationException {
		Report requestedReport;

		try {
			requestedReport = new Report(reportId);

			if (user.isUserInRole(UserRoles.WITNESS) && user.getId() != requestedReport.getWitnessId())
				throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses can only view their own reports."));
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return requestedReport;
	}

	/**
	 * Updates a reports status to what the client specifies.
	 * @param reportId the id of the report to update
	 * @param status the new status of the report
	 * @param user the authentication data of the requesting user
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
				return AuthorizationFilter.unauthorizedAccessResponse("Witnesses may only modify the status of their own reports.");

			toUpdate.setResolved(status);
			toUpdate.writeToDb();
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.ok().build();
	}
}
