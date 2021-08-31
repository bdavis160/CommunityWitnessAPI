package org.communitywitness.api;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * A SecurityContext for users who aren't logged in.
 */
public class GuestUser implements SecurityContext {
	@Override
	public Principal getUserPrincipal() {
		// According to the javadoc for ContainerRequestContext.setSecurityContext
		// this must return null for unauthenticated users
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// Guest users have no roles
		return false;
	}

	@Override
	public boolean isSecure() {
		// Guest users have not been securely authenticated
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		// Guest users have no authentication scheme
		return null;
	}

}
