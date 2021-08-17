package org.communitywitness.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;


public class AuthenticationFilter implements ContainerRequestFilter {
	private static final String CREDENTIAL_HEADER = "X-API-KEY";

	@Context 
	private ResourceInfo targetResource;


	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Method targetMethod = targetResource.getResourceMethod();

		// If there's no method actually being called then no authentication is needed
		if (targetMethod == null)
			return;

		// PermitAll lets all requests through
		if (targetMethod.isAnnotationPresent(PermitAll.class)) 
			return;

		// DenyAll lets no requests through
		if (targetMethod.isAnnotationPresent(DenyAll.class)) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
			return;
		}

		// RolesAllowed lets in requests with proper credentials and roles
		if (targetMethod.isAnnotationPresent(RolesAllowed.class)) {
			Set<String> rolesAllowed = new HashSet<String>(Arrays.asList(
					targetMethod.getAnnotation(RolesAllowed.class).value()));
			String apiKey = requestContext.getHeaderString(CREDENTIAL_HEADER);

			try {
				AuthenticatedUser currentUser = new AuthenticatedUser(apiKey);
				requestContext.setSecurityContext(currentUser);

				if (rolesAllowed.contains(currentUser.getRole())) 
					return;
				else 
					throw new BadLoginException("User is not in appropriate role.");
				
			} catch (BadLoginException exception) {
				requestContext.abortWith(unauthorizedAccessResponse(exception.getMessage()));
			}
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
