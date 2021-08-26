package org.communitywitness.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	private static final String CREDENTIAL_HEADER = "X-API-KEY";

	/**
	 * A filter before each request that tries to authenticate the user based on any credentials given.
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String apiKey = requestContext.getHeaderString(CREDENTIAL_HEADER);
		apiKey = apiKey.strip();

		// Try to authenticate the user
		try {
			AuthenticatedUser currentUser = new AuthenticatedUser(apiKey);
			requestContext.setSecurityContext(currentUser);
		} catch (BadLoginException exception) {
			requestContext.setSecurityContext(new GuestUser());
		}
	}

	/**
	 * Returns an HTTP 401 Unauthorized response containing information about how
	 * to authenticate and the reason the request was unauthorized.
	 * @param reason a description of why the request was unauthorized
	 * @return an HTTP 401 response with the given reason
	 */
	public static Response unauthorizedAccessResponse(String reason) {
		return Response
				.status(Response.Status.UNAUTHORIZED)
				.entity(reason)
				.build();
	}
}
