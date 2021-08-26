package org.communitywitness.api;

import java.sql.SQLException;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

// TODO: implement user authentication for all of these calls
@Path("/witnesses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WitnessResource {
	/**
	 * Creates a new witness with the data sent by the client.
	 * @param witnessRequest - the updated data for the witness
	 * @return the api key of the newly created witness
	 */
	@PermitAll
	@POST
	public String createWitness(WitnessRequest witnessRequest) throws WebApplicationException {
		Witness newWitness = new Witness(witnessRequest);
		
		try {
			int id = newWitness.writeToDb();
			String apiKey = AccountManagement.giveUserApiKey(id, UserRoles.WITNESS);
			
			if (apiKey != null)
				return apiKey;
			else
				throw new WebApplicationException("Failed to create API key.");
		} catch (SQLException exception) {
			throw new WebApplicationException("Failed to create database entry.");
		}
	}

	/**
	 * Returns a witness from the database to the client
	 * @param witnessId - the id of the witness to send
	 * @return the witness with the given id
	 * @throws WebApplicationException if the witness isn't in the database
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@GET
	@Path("/{witnessId}")
	public Witness getWitness(@PathParam("witnessId") int witnessId, @Context AuthenticatedUser user) throws WebApplicationException {
		Witness requestedWitness;

		if (user.getId() != witnessId)
			throw new WebApplicationException(AuthenticationFilter.unauthorizedAccessResponse("You can only view your own witness data."));
		
		try {
			requestedWitness = new Witness(witnessId);
		} catch (SQLException exception) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return requestedWitness;
	}

	/**
	 * Updates the changeable parts of a witness' data with data sent by the client.
	 * This takes a whole witness object so that making more forms changeable is possible with minimal modifications.
	 * @param witnessId - the id of the witness whose data should be updated
	 * @param witnessRequestData - the updated data for the witness
	 * @return A status of OK if the witness is found and update, otherwise a status of NOT_FOUND
	 */
	@RolesAllowed({UserRoles.WITNESS})
	@POST
	@Path("/{witnessId}")
	public Response updateWitness(@PathParam("witnessId") int witnessId, WitnessRequest witnessRequestData, @Context AuthenticatedUser user) {
		if (user.getId() != witnessId)
			return AuthenticationFilter.unauthorizedAccessResponse("You can only modify your own witness data.");
		
		try {
			Witness requestedWitness = new Witness(witnessId);
			Witness updates = new Witness(witnessRequestData);
			requestedWitness.updateFrom(updates);
		} catch (SQLException exception) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		return Response.ok().build();
	}
}
