package org.communitywitness.api;

import org.glassfish.hk2.api.Factory;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

public class AuthenticatedUserInjectionFactory implements Factory<AuthenticatedUser> {
	private final ContainerRequestContext requestContext;
	
	/**
	 * Constructs the factory for the current request.
	 * @param requestContext the context of the current request
	 */
	@Inject
	public AuthenticatedUserInjectionFactory(ContainerRequestContext requestContext) {
		this.requestContext = requestContext;
	}
	
	@Override
	public AuthenticatedUser provide() {
		return (AuthenticatedUser) requestContext.getProperty(AuthenticatedUser.REQUEST_CONTEXT_PROPERTY);
	}

	@Override
	public void dispose(AuthenticatedUser instance) {}
}
