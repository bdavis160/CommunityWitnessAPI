package org.communitywitness.api;

import java.sql.SQLException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/reportComments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportCommentResource {
	/**
	 * Creates a new comment on a report with the data contained in the sent object.
	 * @param newComment - an object containing the information about the comment
	 * @return the actual id of the newly created comment
	 * @throws WebApplicationException if the report or investigator the comment is associated with doesn't exist
	 */
	@POST
	public int createReportComment(ReportComment newComment) throws WebApplicationException {
		// check if the report and investigator exist
		try {
			Report requestedReport = new Report(newComment.getReportId());
			Investigator requestedInvestigator = new Investigator(newComment.getInvestigatorId());
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		// TODO: write new comment back to database and fill in its id
		return newComment.getId();
	}
	
	/**
	 * Returns the report comment with the given id.
	 * @param commentId - the id of the comment
	 * @return the comment data
	 * @throws WebApplicationException if no comment with the given id is found
	 */
	@GET
	@Path("/{reportCommentId}")
	public ReportComment getReportComment(@PathParam("reportCommentId") int commentId) throws WebApplicationException {
		ReportComment requestedComment;
		
		try {
			requestedComment = new ReportComment(commentId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedComment;
	}
}
