package org.communitywitness.api;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
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
		
		if (apiKey == null) {
			requestContext.setSecurityContext(new GuestUser());
			return;
		} else {
			apiKey = apiKey.strip();
		}

		// Try to authenticate the user
		try {
			AuthenticatedUser currentUser = new AuthenticatedUser(apiKey);
			requestContext.setSecurityContext(currentUser);
			requestContext.setProperty(AuthenticatedUser.REQUEST_CONTEXT_PROPERTY, currentUser);
			return;
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
