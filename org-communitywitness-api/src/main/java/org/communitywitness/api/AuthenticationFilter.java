package org.communitywitness.api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;


public class AuthenticationFilter implements ContainerRequestFilter {
	private static final String AUTHENTICATION_SCHEME_PRELUDE = "Basic ";
	private static final String AUTHENTICATION_REALM = "realm=\"CommunityWitness\"";
	private static final String CREDENTIAL_SEPERATOR = ":";
	private static final int NUM_CREDENTIAL_FIELDS = 2;

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
			List<String> credentials = decodeAuthorizationHeader(
					requestContext.getHeaderString(HttpHeaders.AUTHORIZATION));

			try {
				AuthenticatedUser currentUser = new AuthenticatedUser(credentials.get(0), credentials.get(1));

				if (rolesAllowed.contains(currentUser.getRole())) 
					return;
				else 
					throw new BadLoginException("User is not in appropriate role.");
			} catch (IndexOutOfBoundsException exception) {
				requestContext.abortWith(unauthorizedAccessResponse("Incorrectly formatted credentials."));
			} catch (BadLoginException exception) {
				requestContext.abortWith(unauthorizedAccessResponse(exception.getMessage()));
			}
		}
	}

	/**
	 * Returns an HTTP 401 Unauthorized response containing information about how
	 * to authenticate and the reason the request was unauthorized.
	 * @param reason
	 * @return
	 */
	public Response unauthorizedAccessResponse(String reason) {
		return Response
				.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME_PRELUDE + AUTHENTICATION_REALM)
				.entity(reason)
				.build();
	}

	/**
	 * Decodes the credentials stored in the HTTP Authorization header.
	 * @param authorizationHeader the full text contents of the authorization header
	 * @return the credentials from the header in a list 
	 * where the first entry is the username and the second the password
	 */
	private List<String> decodeAuthorizationHeader(String authorizationHeader) {
		// The authorization header must exist, have contents, and have the right scheme 
		if (authorizationHeader == null || authorizationHeader.isEmpty() ||
				authorizationHeader.isBlank() || !authorizationHeader.startsWith(AUTHENTICATION_SCHEME_PRELUDE))
			return Collections.emptyList();

		// In HTTP basic authentication the username and password are encoded in base64, so decode them
		String encodedCredentials = authorizationHeader.substring(AUTHENTICATION_SCHEME_PRELUDE.length());
		byte[] decodedCredentialBytes = Base64.getDecoder().decode(encodedCredentials.getBytes(StandardCharsets.UTF_8));
		String[] decodedCredentials = new String(decodedCredentialBytes, StandardCharsets.UTF_8).split(CREDENTIAL_SEPERATOR);

		if (decodedCredentials.length != NUM_CREDENTIAL_FIELDS)
			return Collections.emptyList();

		return new ArrayList<String>(Arrays.asList(decodedCredentials));
	}

}
