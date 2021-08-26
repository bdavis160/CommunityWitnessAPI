package org.communitywitness.api;

import java.io.IOException;
import java.lang.reflect.Method;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {
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
			String[] rolesAllowed = targetMethod.getAnnotation(RolesAllowed.class).value();
			SecurityContext currentUser = requestContext.getSecurityContext();
			
			if (!isAuthenticated(currentUser)) {
				requestContext.abortWith(unauthorizedAccessResponse("User not authenticated."));
				return;
			}
			
			for (String role : rolesAllowed) {
				if (currentUser.isUserInRole(role))
					return;
			}
			
			requestContext.abortWith(unauthorizedAccessResponse("User not in role."));
			return;
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

	/**
	 * Checks if a user is authenticated by checking if they have a user principal.
	 * @param user the user to check
	 * @return true if the user is authenticated, false if not
	 */
	private static boolean isAuthenticated(SecurityContext user) {
		return user.getUserPrincipal() != null;
	}

}
