package org.CommunityWitness.Backend;

import java.sql.SQLException;
import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/evidence")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EvidenceResource {
	/**
	 * Creates new evidence with the data contained in the sent object.
	 * TODO: figure out how to consume files associated with the evidence
	 * @param newEvidence - an object containing the information about the evidence
	 * @return the actual id of the newly created evidence
	 * @throws WebApplicationException if the report the evidence is associated with doesn't exist
	 */
	@POST
	public int createEvidence(Evidence newEvidence) throws WebApplicationException {
		// check if the report exists
		try {
			Report requestedReport = new Report(newEvidence.getReportId());
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		// TODO: write new evidence back to database and fill in its id
		return newEvidence.getId();
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
	
	/**
	 * A resource that doesn't rely on the database for testing if it's running properly on local machines.
	 * @return an evidence object with simple contents
	 */
	@GET
	@Path("/test")
	public Evidence testEvidence() {
		Evidence dummyData = new Evidence();
		dummyData.setId(0);
		dummyData.setLink("link");
		dummyData.setReportId(0);
		dummyData.setTimestamp(new Date());
		dummyData.setTitle("title");
		dummyData.setType("type");
		
		return dummyData;
	}
}
