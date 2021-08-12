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
import jakarta.ws.rs.core.Response.Status;

// TODO: implement user authentication for all of these calls
@Path("/witnesses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WitnessResource {
	/**
	 * Creates a new witness with the data sent by the client.
	 * @param witnessRequest - the updated data for the witness
	 * @return the id of the newly created witness
	 */
	@POST
	public int createWitness(WitnessRequest witnessRequest) throws SQLException {
		Witness newWitness = new Witness(witnessRequest);
		return newWitness.writeToDb();
	}

	/**
	 * Returns a witness from the database to the client
	 * @param witnessId - the id of the witness to send
	 * @return the witness with the given id
	 * @throws WebApplicationException if the witness isn't in the database
	 */
	@GET
	@Path("/{witnessId}")
	public Witness getWitness(@PathParam("witnessId") int witnessId) throws WebApplicationException {
		Witness requestedWitness;

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
	@POST
	@Path("/{witnessId}")
	public Status updateWitness(@PathParam("witnessId") int witnessId, WitnessRequest witnessRequestData) {
		try {
			Witness requestedWitness = new Witness(witnessId);
			Witness updates = new Witness(witnessRequestData);
			requestedWitness.updateFrom(updates);
		} catch (SQLException exception) {
			return Response.Status.NOT_FOUND;
		}
		
		return Response.Status.OK;
	}
}
