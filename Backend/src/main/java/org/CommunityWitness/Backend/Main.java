package org.CommunityWitness.Backend;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {
	// URL that the HTTP server listens on, TODO: decide if this should be run locally and proxied
	public static final String BASE_URI = "http://0.0.0.0:80/";
	
	/**
	 * Starts an embedded Grizzly HTTP server which serves the REST API.
	 * @return The Grizzly HTTP server
	 */
	public static HttpServer startServer() {
		// scan for resources (endpoints) in the package
		final ResourceConfig resources = new ResourceConfig().packages("org.CommunityWitness.Backend");
		
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resources);
		
	}
	
	/**
	 * Minimal main that starts up the embedded http server and waits on input.
	 * @param args - arguments from the command line, which are currently unused.
	 * @throws IOException in the unusual case of problems with stdin or stdout
	 */
	public static void main(String[] args) throws IOException {
		final HttpServer httpServer = startServer();
		
		System.out.println(String.format("Backend started at '%s', press Ctrl-C to quit on the command line or use the stop button in an IDE.", BASE_URI));
		// loop on input to keep running, TODO: make this more elegant
		while (true)
			System.in.read();
	}
}
