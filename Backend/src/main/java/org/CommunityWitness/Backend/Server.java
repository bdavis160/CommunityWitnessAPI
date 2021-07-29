package org.CommunityWitness.Backend;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
	// URL that the HTTP server listens on, TODO: decide if this should be run locally and proxied
	public static final String BASE_URI = "http://0.0.0.0:8080/";

	// Multipliers that determine the base (core) and max number of worker threads relative to the systems processor count
	private static final int CORE_POOL_MULTIPLIER = 1;
	private static final int MAX_POOL_MULTIPLIER = 4;

	// The actual embedded Grizzly HTTP server
	private static HttpServer httpServer;

	/**
	 * Creates the embedded Grizzly HTTP server with all the resources in this package, but doesn't start it.
	 */
	public static void setupServer() {
		// Create the HTTP server with all the resources in this package
		final ResourceConfig resources = new ResourceConfig().packages("org.CommunityWitness.Backend");
		httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resources, false);

		// Set the size of the worker thread pool
		int numProcessors = Runtime.getRuntime().availableProcessors();
		ThreadPoolConfig workerPoolConfig = httpServer.getListener("grizzly").getTransport().getWorkerThreadPoolConfig();
		workerPoolConfig.setCorePoolSize(CORE_POOL_MULTIPLIER * numProcessors);
		workerPoolConfig.setMaxPoolSize(MAX_POOL_MULTIPLIER * numProcessors);
	}

	/**
	 * Try to start the HTTP server
	 * @return true on success, false on failure
	 */
	public static boolean startServer() {
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
	 * Stops the HTTP server
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
	 * Sets up and starts the HTTP server hosting the REST API
	 * @param args - arguments from the command line, which are currently unused
	 */
	public static void main(String[] args) {
		setupServer();
		if (!startServer())
			return;
		
		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

		System.out.println(String.format("Backend started at '%s', to quit send a SIGTERM, SIGINT, or SIGHUP signal"
				+ "(by using a service manager, pressing Ctrl-C command line, the stop button in an IDE, or some other means).", BASE_URI));
	}
}
