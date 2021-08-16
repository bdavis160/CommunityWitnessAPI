package org.communitywitness.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// TODO: implement user authentication for all of these calls
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	/**
	 * Returns all the reports in the database.
	 * @return a list of reports
	 */
	@GET
	public List<Report> queryReports() throws SQLException {
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = "SELECT id, resolved, description, time, location, witnessID FROM report;";

		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		ArrayList<Report> results = new ArrayList<>();

		while (queryResults.next()) {
			Report report = new Report(queryResults.getBoolean(2),
					queryResults.getString(3),
					queryResults.getTimestamp(4).toLocalDateTime(),
					queryResults.getString(5),
					queryResults.getInt(6));
			report.setId(queryResults.getInt(1));
			report.loadComments();
			report.loadEvidence();
			results.add(report);
		}

		return results;
	}

	/**
	 * Creates a new report from data sent from a client.
	 * @param newReportRequestData - object containing data for new report
	 * @return the id of the newly created report.
	 */
	@POST
		public int createReport(NewReportRequest newReportRequestData) throws SQLException {
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
	@GET
	@Path("/{reportId}")
	public Report getReport(@PathParam("reportId") int reportId) throws WebApplicationException {
		Report requestedReport;
		
		try {
			requestedReport = new Report(reportId);
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
	@PUT
	@Path("/{reportId}/{status}")
	public Response updateReportStatus(@PathParam("reportId") int reportId, @PathParam("status") boolean status) {
		Report toUpdate;
		
		try {
			toUpdate = new Report(reportId);
			toUpdate.setResolved(status);
			toUpdate.writeToDb();
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		return Response.ok().build();
	}
}
