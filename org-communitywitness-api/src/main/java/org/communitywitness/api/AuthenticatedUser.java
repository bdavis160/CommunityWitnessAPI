package org.communitywitness.api;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

public class AuthenticatedUser implements SecurityContext {
	private String username;
	private String role;
	private Principal principal;
	
	public AuthenticatedUser(String username, String password) throws BadLoginException {
		setUsername(username);
		setPrincipal();
		
		// TODO: check against database
		
	}
	
	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (role.equalsIgnoreCase(this.role))
			return true;
		else
			return false;
	}

	@Override
	public boolean isSecure() {
		// Assume the connection is secure if it's going over https
		return Server.getBaseUri().toLowerCase().startsWith("https");
	}

	@Override
	public String getAuthenticationScheme() {
		return SecurityContext.BASIC_AUTH;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setPrincipal() {
		this.principal = new Principal() {
			public String getName() {
				return username;
			}
		};
	}

}
