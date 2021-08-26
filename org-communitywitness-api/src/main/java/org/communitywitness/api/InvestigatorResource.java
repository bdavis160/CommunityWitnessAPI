package org.communitywitness.api;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

// TODO: implement user authentication for all of these calls
@Path("/investigators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestigatorResource {
	/**
	 * Creates a new investigator with data sent by the client.
	 * TODO: decide if this should be changed into a request somehow, 
	 * 		perhaps with a separate requests table in the database to store them in.
	 * @param newInvestigatorRequestData - object containing the new data to be inserted
	 * @return the id of the newly created investigator
	 */
	@PermitAll
	@POST
	public int createInvestigator(NewInvestigatorRequest newInvestigatorRequestData) throws WebApplicationException {
		int id;
		Investigator newInvestigator = new Investigator(
				newInvestigatorRequestData.getName(),
				newInvestigatorRequestData.getOrganization(),
				newInvestigatorRequestData.getOrganizationType(),
				newInvestigatorRequestData.getRating());
		
		try {
			id = newInvestigator.writeToDb();
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to write investigator to database.");
		}
		
		if (!AccountManagement.createInvestigatorAccount(newInvestigatorRequestData.getUsername(),
				newInvestigatorRequestData.getPassword(), id))
			throw new WebApplicationException("Failed to create investigator account.");
			
		return id;
	}
	
	/**
	 * Returns an investigators information from the database to the client
	 * @param investigatorId - the id of the investigator to send data about
	 * @return the data about the investigator
	 * @throws WebApplicationException if the investigator isn't found in the database
	 */
	@GET
	@Path("/{investigatorId}")
	public Investigator getInvestigator(@PathParam("investigatorId") int investigatorId) throws WebApplicationException {
		Investigator requestedInvestigator;
		
		try {
			requestedInvestigator = new Investigator(investigatorId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return requestedInvestigator;
	}
	
	/**
	 * Updates the changeable parts of an investigators data with data sent by the client.
	 * This takes a whole Investigator object so that making more forms changeable is simple.
	 * @param investigatorId - the id of the investigator whose data should be updated
	 * @param updateInvestigatorRequestData - an object containing the updated data
	 * @return An OK status on success, otherwise a NOT_FOUND status when no matching investigator is found.
	 */
	@POST
	@Path("/{investigatorId}")
	public Response updateInvestigator(@PathParam("investigatorId") int investigatorId, UpdateInvestigatorRequest updateInvestigatorRequestData) {
		try {
			Investigator requestedInvestigator = new Investigator(investigatorId);
			Investigator updatedData = new Investigator(
					updateInvestigatorRequestData.getName(),
					updateInvestigatorRequestData.getOrganization(),
					updateInvestigatorRequestData.getOrganizationType(),
					0);
			requestedInvestigator.updateFrom(updatedData);
			requestedInvestigator.writeToDb();
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		return Response.ok().build();
	}

	/**
	 * Connects this investigator to a report (case)
	 * Takes an investigator id and a case id
	 * @param investigatorId - the id of the investigator
	 * @param reportId - the id of the report they're interested in
	 * @return An OK status on success, otherwise a NOT_FOUND status when no matching investigator/report is found.
	 */
	@POST
	@Path("/{investigatorId}/take/{reportId}")
	public Response takeCase(@PathParam("investigatorId") int investigatorId, @PathParam("reportId") int reportId) throws SQLException {
		Investigator investigator;

		try {
			investigator = new Investigator(investigatorId);
		} catch (RuntimeException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		try {
			investigator.takeCase(reportId);
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().build();
	}
}
