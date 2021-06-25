package org.CommunityWitness.Backend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

// TODO: implement user authentication for all of these calls
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	/**
	 * Returns a list of reports matching the restrictions in the query parameters, or just all the reports if no parameters are specified.
	 * 
	 * @param location - the location of the report(s)
	 * @param time - the time range for when the report(s) were submitted
	 * @return a list of reports matching the query
	 */
	@GET
	public List<Report> queryReports(@QueryParam("location") String location, @QueryParam("time") String time) {
		// TODO: format query params for use in sql query, query for matching reports, put them in the results list
		ArrayList<Report> results = new ArrayList<Report>();
		return results;
	}

	/**
	 * Creates a new report from data sent from a client.
	 * 
	 * @param description - a description of what took place
	 * @param time - the time that the report took place
	 * @param location - where the report took place
	 * @return the id of the newly created report.
	 */
	@POST
	public int createReport(String description, Date time, String location) {
		Report newReport = new Report();
		newReport.setDescription(description);
		newReport.setTime(time);
		newReport.setLocation(location);
		// TODO: save newReport to database here and fill in it's id from that
		return newReport.id;
	}
	
	/**
	 * Returns a report from the database to a client.
	 * 
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
	 * 
	 * @param reportId - the id of the report to update
	 * @param status - the new status of the report
	 */
	@POST
	@Path("/{reportId}/status")
	public Status updateReportStatus(@PathParam("reportId") int reportId, boolean status) {
		Report toUpdate;
		
		try {
			toUpdate = new Report(reportId);
			toUpdate.setResolved(status);
			// TODO: write back to database, although maybe that should be implemented in the Report class
		} catch (SQLException exception) {
			return Response.Status.NOT_FOUND;
		}
		
		return Response.Status.OK;
	}
	
	/**
	 * Returns a list of all the comments on the report with the given id.
	 * 
	 * @param reportId - the id of the report to retrieve comments on
	 * @return a list of comments
	 * @throws SQLException
	 */
	@GET
	@Path("/{reportId}/comments")
	public List<ReportComment> getReportComments(@PathParam("reportId") int reportId) throws SQLException {
		Report report = new Report(reportId);
		
		List<ReportComment> comments = report.getComments();
		
		return comments;
	}
	
	/**
	 * Adds an investigators comment to a report.
	 * 
	 * @param reportId - the id of the report to comment on
	 * @param comment - the text of the comment sent by the client
	 */
	@PUT
	@Path("/{reportId}/comments")
	public Status addCommentToReport(@PathParam("reportId") int reportId, String comment) {
		//Report toCommentOn = new Report(reportId);
		// TODO: write comment about report to database, if that fails return a failure status
		return Response.Status.OK;
	}
}
