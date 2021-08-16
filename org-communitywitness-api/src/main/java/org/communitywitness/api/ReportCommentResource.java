package org.communitywitness.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/reportComments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportCommentResource {
	/**
	 * Creates a new comment on a report with the data contained in the sent object.
	 * @param reportCommentRequest - an object containing the information about the comment
	 * @return the actual id of the newly created comment
	 * @throws WebApplicationException if the report or investigator the comment is associated with doesn't exist
	 */
	@POST
	public int createReportComment(ReportCommentRequest reportCommentRequest) throws WebApplicationException, SQLException {
		// check if the report and investigator exist
		try {
			new Report(reportCommentRequest.getReportId());
			new Investigator(reportCommentRequest.getInvestigatorId());
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		ReportComment newComment = new ReportComment(reportCommentRequest);
		return newComment.writeToDb();
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
