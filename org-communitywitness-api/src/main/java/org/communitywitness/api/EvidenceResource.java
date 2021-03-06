package org.communitywitness.api;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/evidence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvidenceResource {
	/**
	 * Creates new evidence with the data contained in the sent object.
	 * @param newEvidenceRequestData an object containing the information about the evidence
	 * @param user the authentication data of the requesting user
	 * @return the actual id of the newly created evidence
	 * @throws WebApplicationException if the report the evidence is associated with doesn't exist
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@POST
	public int createEvidence(NewEvidenceRequest newEvidenceRequestData, @Context AuthenticatedUser user) throws WebApplicationException {
		// check if the report exists and belongs to the right witness
		try {
			Report relevantReport = new Report(newEvidenceRequestData.getReportId());
			
			if (user.getId() != relevantReport.getWitnessId())
				throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses may only post evidence for their own reports."));
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		// try to write evidence to database
		try {
			Evidence newEvidence = new Evidence(newEvidenceRequestData);
			return newEvidence.writeToDb();
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to write evidence to database.");
		}
	}
	
	/**
	 * Returns the evidence with the given id.
	 * @param evidenceId the id of the evidence
	 * @param user the authentication data of the requesting user
	 * @return the evidence data
	 * @throws WebApplicationException if no evidence with the given id is found
	 */
	@RolesAllowed({UserRoles.INVESTIGATOR, UserRoles.WITNESS})
	@GET
	@Path("/{evidenceId}")
	public Evidence getEvidence(@PathParam("evidenceId") int evidenceId, @Context AuthenticatedUser user) throws WebApplicationException {
		Evidence requestedEvidence;
		
		try {
			requestedEvidence = new Evidence(evidenceId);
			
			if (user.isUserInRole(UserRoles.WITNESS)) {
				Report relevantReport = new Report(requestedEvidence.getReportId());
				
				if (user.getId() != relevantReport.getWitnessId())
					throw new WebApplicationException(AuthorizationFilter.unauthorizedAccessResponse("Witnesses can only view evidence from their own reports."));
			}
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedEvidence;
	}
}
