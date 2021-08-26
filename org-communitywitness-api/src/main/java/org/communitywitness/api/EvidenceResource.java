package org.communitywitness.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.sql.SQLException;

@Path("/evidence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvidenceResource {
	/**
	 * Creates new evidence with the data contained in the sent object.
	 * TODO: figure out how to consume files associated with the evidence
	 * @param newEvidenceRequestData - an object containing the information about the evidence
	 * @return the actual id of the newly created evidence
	 * @throws WebApplicationException if the report the evidence is associated with doesn't exist
	 */
	@POST
	public int createEvidence(NewEvidenceRequest newEvidenceRequestData) throws WebApplicationException, SQLException, IOException {
		// check if the report exists
		try {
			new Report(newEvidenceRequestData.getReportId());
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		Evidence newEvidence = new Evidence(newEvidenceRequestData);
		return newEvidence.writeToDb();
	}
	
	/**
	 * Returns the evidence with the given id.
	 * @param evidenceId - the id of the evidence
	 * @return the evidence data
	 * @throws WebApplicationException if no evidence with the given id is found
	 */
	@GET
	@Path("/{evidenceId}")
	public Evidence getEvidence(@PathParam("evidenceId") int evidenceId) throws WebApplicationException {
		Evidence requestedEvidence;
		
		try {
			requestedEvidence = new Evidence(evidenceId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedEvidence;
	}
}
