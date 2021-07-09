package org.CommunityWitness.Backend;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
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
	 * @param description - a description of what took place
	 * @param time - the time that the report took place
	 * @param location - where the report took place
	 * @return the id of the newly created report.
	 */
	@POST
	public int createReport(@FormParam("description") String description, @FormParam("time") Date time, @FormParam("location") String location) {
		Report newReport = new Report();
		newReport.setDescription(description);
		newReport.setTime(time);
		newReport.setLocation(location);
		// TODO: save newReport to database here and fill in it's id from that
		return newReport.id;
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
	 * Returns a list of all the ids of the evidence associated with the report with the given id.
	 * @param reportId - the id of the report to retrieve evidence of
	 * @return a list of ids of evidence associated with the report
	 * @throws WebApplicationException if the report isn't found
	 */
	@GET
	@Path("/{reportId}/evidence")
	public List<Integer> getEvidence(@PathParam("reportId") int reportId) throws WebApplicationException {
		List<Integer> evidenceIds;
		
		try {
			Report requestedReport = new Report(reportId);
			evidenceIds = requestedReport.getEvidence();
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return evidenceIds;
	}
	
	/**
	 * Creates new evidence associated with the given report id from the data sent.
	 * TODO: figure out how to send any files associated with the evidence
	 * @param reportId - the id of the report the evidence is associated with
	 * @param title - the title of the evidence
	 * @param type - the type of the evidence (video, picture, audio, etc)
	 * @param timestamp - the time the evidence occurred
	 * @return the id of the newly created evidence
	 * @throws WebApplicationException if the report isn't found
	 */
	@POST
	@Path("/{reportId}/evidence")
	public int appendEvidence(@PathParam("reportId") int reportId, @FormParam("title") String title, 
			@FormParam("type") String type, @FormParam("timestamp") Date timestamp) throws WebApplicationException {
		// check if the report exists
		try {
			Report requestedReport = new Report(reportId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		Evidence newEvidence = new Evidence();
		
		newEvidence.setTitle(title);
		newEvidence.setType(type);
		newEvidence.setTimestamp(timestamp);
		newEvidence.setReportId(reportId);
		
		// TODO: write new evidence back to database and fill in its id
		
		return newEvidence.getId();
	}
	
	/**
	 * Updates a reports status to what the client specifies.
	 * @param reportId - the id of the report to update
	 * @param status - the new status of the report
	 * @return An OK status on success, otherwise a NOT_FOUND status when the report isn't found
	 */
	@POST
	@Path("/{reportId}/status")
	public Status updateReportStatus(@PathParam("reportId") int reportId, @FormParam("status") boolean status) {
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
	 * Returns a list of all the ids of the comments on the report with the given id.
	 * @param reportId - the id of the report to retrieve comments on
	 * @return a list of comment ids
	 * @throws SQLException
	 */
	@GET
	@Path("/{reportId}/comments")
	public List<Integer> getReportComments(@PathParam("reportId") int reportId) throws WebApplicationException {
		List<Integer> commentIds;
		
		try {
			Report requestedReport = new Report(reportId);
			commentIds = requestedReport.getComments();
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return commentIds;
	}
	
	/**
	 * Adds an investigators comment to a report.
	 * @param reportId - the id of the report to comment on
	 * @param comment - the text of the comment sent by the client
	 */
	@PUT
	@Path("/{reportId}/comments")
	public Status addCommentToReport(@PathParam("reportId") int reportId, @FormParam("comment") String comment) {
		//Report toCommentOn = new Report(reportId);
		// TODO: write comment about report to database, if that fails return a failure status
		return Response.Status.OK;
	}

}
