package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
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
	@RolesAllowed({UserRoles.INVESTIGATOR})
	@POST
	public int createReportComment(ReportCommentRequest reportCommentRequest, @Context AuthenticatedUser user) throws WebApplicationException, SQLException {
		if (user.getId() != reportCommentRequest.getInvestigatorId())
			AuthenticationFilter.unauthorizedAccessResponse("You cannot file a comment as a different investigator");
		
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
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@GET
	@Path("/{reportCommentId}")
	public ReportComment getReportComment(@PathParam("reportCommentId") int commentId, @Context AuthenticatedUser user) throws WebApplicationException {
		ReportComment requestedComment;
		
		try {
			requestedComment = new ReportComment(commentId);
			
			// Witnesses should only view comments on their own reports
			if (user.isUserInRole(UserRoles.WITNESS)) {
				Report relevantReport = new Report(requestedComment.getReportId());
				if (user.getId() != relevantReport.getWitnessId())
					throw new WebApplicationException(AuthenticationFilter.unauthorizedAccessResponse("You can only view comments on your reports."));
			}
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedComment;
	}
}
