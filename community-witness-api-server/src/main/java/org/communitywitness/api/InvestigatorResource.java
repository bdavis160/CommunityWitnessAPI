package org.communitywitness.api;

import java.sql.SQLException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// TODO: implement user authentication for all of these calls
@Path("/investigators")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestigatorResource {
	/**
	 * Creates a new investigator with data sent by the client.
	 * TODO: decide if this should be changed into a request somehow, 
	 * 		perhaps with a separate requests table in the database to store them in.
	 * @param name - the name of the investigator
	 * @param organization - the organization the investigator works for
	 * @param organizationType - the type of organization the investigator works for
	 * @return the id of the newly created investigator
	 */
	@POST
	public int createInvestigator(@FormParam("name") String name, @FormParam("organization") String organization,
			@FormParam("organizationType") String organizationType) {
		Investigator newInvestigator = new Investigator();
		newInvestigator.setName(name);
		newInvestigator.setOrganization(organization);
		newInvestigator.setOrganizationType(organizationType);
		// TODO: write investigator to database and get the id assigned by the db
		
		return newInvestigator.getId();
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
	 * @param updatedData - an Investigator object containing the updated data
	 * @return An OK status on success, otherwise a NOT_FOUND status when no matching investigator is found.
	 */
	@POST
	@Path("/{investigatorId}")
	public Response updateInvestigator(@PathParam("investigatorId") int investigatorId, Investigator updatedData) {
		try {
			Investigator requestedInvestigator = new Investigator(investigatorId);
			requestedInvestigator.updateFrom(updatedData);
			// TODO: write back update to database
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		return Response.ok().build();
	}
}