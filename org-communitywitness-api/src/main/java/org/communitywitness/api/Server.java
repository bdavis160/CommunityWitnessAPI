package org.communitywitness.api;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.apache.commons.validator.routines.UrlValidator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
	// The kinds of URI's ("schemes") that are accepted
	public static final String[] ALLOWED_URI_SCHEMES = {"http"};
	
	// URI that the HTTP server listens on
	private static URI baseUri = URI.create("http://127.0.0.1:8080");
	// The actual embedded Grizzly HTTP server
	private static HttpServer httpServer;


	/**
	 * Sets the base URI to a new value if it passes validation.
	 * Note that even if a URI passes validation the server may not actually be able to use it,
	 * for example while 'http://localhost' is a valid URI the server won't be able to use it 
	 * without the proper network permissions on Linux and thus will fail despite the URI being valid.
	 * @param newUri - the string form of the URI to try and use as the new base URI
	 */
	private static void setBaseUri(String newUri) {
		UrlValidator validator = new UrlValidator(ALLOWED_URI_SCHEMES);
		
		if (validator.isValid(newUri))
			baseUri = URI.create(newUri);
		else
			System.err.println(String.format("Failed to set base URI to '%s', falling back to default", newUri));
	}
	
	/**
	 * Returns the URI that the HTTP server is listening on in string form.
	 * @return the URI that the server is listening on as a string
	 */
	public static String getBaseUri() {
		return baseUri.toString();
	}
	
	/**
	 * Creates the embedded Grizzly HTTP server with all the resources found in this package,
	 * then tries to start it.
	 * @return true if the server starts, false if it doesn't
	 */
	public static boolean startServer() {
		final ResourceConfig resources = new APIResourceConfig();
		httpServer = GrizzlyHttpServerFactory.createHttpServer(baseUri, resources, false);
		
		try {
			httpServer.start();
		} catch (IOException exception) {
			System.err.println("Error starting embedded HTTP server.");
			exception.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Stops the HTTP server.
	 */
	public static void stopServer() {
		// Try to shutdown gracefully, otherwise shutdown immediately
		try {
			httpServer = httpServer.shutdown().get();
		} catch (InterruptedException | ExecutionException e) {
			httpServer.shutdownNow();
		}
	}

	/**
	 * Sets up and starts the HTTP server hosting the REST API.
	 * @param args - arguments from the command line, the first of which is used as the base URI
	 */
	public static void main(String[] args) {
		// Set baseUri to the first argument if it exists
		if (args.length > 0)
			setBaseUri(args[0]);
		
		if (!startServer())
			System.exit(-1);
		
		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

		System.out.println(String.format("Backend started at '%s', to quit send a SIGTERM, SIGINT, or SIGHUP signal"
				+ "(by using a service manager, pressing Ctrl-C command line, the stop button in an IDE, or some other means).", baseUri));
	}
}
