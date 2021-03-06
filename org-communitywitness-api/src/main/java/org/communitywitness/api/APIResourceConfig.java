package org.communitywitness.api;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

public class APIResourceConfig extends ResourceConfig {
	
	/**
	 * Constructs a ResourceConfig for the API
	 */
	public APIResourceConfig() {
		// Scan for resources in this package
		packages("org.communitywitness.api");
		
		// Register the AuthenticatedUser injection factory
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(AuthenticatedUserInjectionFactory.class)
				.to(AuthenticatedUser.class)
				.in(RequestScoped.class);
			}
		});
	}
}
