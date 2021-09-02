package org.communitywitness.api;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/usernames")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsernameResource {
	/**
	 * Checks if the given username is available for use.
	 * @param username the desired username
	 * @return true if the username is available, false otherwise
	 */
	@GET
	@Path("/{username}")
	public boolean checkUsernameAvailability(@PathParam("username") String username) {
		return AccountManagement.isUsernameAvailable(username);
	}
}
