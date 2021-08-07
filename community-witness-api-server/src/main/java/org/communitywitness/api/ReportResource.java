package org.communitywitness.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// TODO: implement user authentication for all of these calls
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	/**
	 * Returns a list of reports matching the restrictions in the query parameters, or just all the reports if no parameters are specified.
	 * @param location - the location of the report(s)
	 * @param time - the time range for when the report(s) were submitted
	 * @return a list of reports matching the query
	 */
	@GET
	public List<Report> queryReports(@QueryParam("location") String location, @QueryParam("time") String time) throws SQLException {
		// TODO: modify this to accept a range of times and perhaps a radius relative to location (depending on front end needs)
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT id, resolved, description, time, location, witnessID " +
						"FROM report " +
						"WHERE location='%s' " +
						"AND time='%s';", location, time);

		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		ArrayList<Report> results = new ArrayList<>();

		while (queryResults.next()) {
			Report report = new Report(queryResults.getInt(1));
			results.add(report);
		}

		return results;
	}

	/**
	 * Creates a new report from data sent from a client.
	 * @param description - a description of what took place
	 * @param time - the time that the report took place
	 * @param location - where the report took place
	 * @return the id of the newly created report.
	 */
	@POST
	public int createReport(@FormParam("description") String description, @FormParam("time") Date time, @FormParam("location") String location) throws SQLException {
		Report newReport = new Report();
		newReport.setDescription(description);
		newReport.setTimestamp(time);
		newReport.setLocation(location);
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
	@Path("/{reportId}")
	public Response updateReportStatus(@PathParam("reportId") int reportId, @FormParam("status") boolean status) {
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
