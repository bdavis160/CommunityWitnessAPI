package org.communitywitness.api;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
	// The actual embedded Grizzly HTTP server
	private static HttpServer httpServer;
	
	/**
	 * Creates the embedded Grizzly HTTP server with all the resources found in this package,
	 * then tries to start it.
	 * @return true if the server starts, false if it doesn't
	 */
	public static boolean startServer() {
		final ResourceConfig resources = new APIResourceConfig();
		
		if (Settings.getInstance().isTlsEnabled()) {
			SSLContextConfigurator sslSettings = new SSLContextConfigurator();
			sslSettings.setKeyStoreFile(Settings.getInstance().getTlsKeyStoreFile());
			sslSettings.setKeyStorePass(Settings.getInstance().getTlsKeyStorePassword());
			SSLContext tlsContext;
			SSLEngineConfigurator tlsEngineConfig;
			
			try {
				tlsContext = sslSettings.createSSLContext(true);
				tlsEngineConfig = new SSLEngineConfigurator(tlsContext, false, false, false);
			} catch (Exception exception) {
				// Grizzly's TLS/SSL stuff is poorly documented so I don't know all of the exceptions that might be thrown
				System.err.println(String.format("Error setting up TLS, please verify your keystore file '%s'.", 
						Settings.getInstance().getTlsKeyStoreFile()));
				return false;
			}
			
			httpServer = GrizzlyHttpServerFactory.createHttpServer(Settings.getInstance().getBaseUri(), resources, true, tlsEngineConfig, false);
		} else {
			httpServer = GrizzlyHttpServerFactory.createHttpServer(Settings.getInstance().getBaseUri(), resources, false);
		}
		
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
	 * @param args arguments from the command line, the first of which is used as settings filename
	 */
	public static void main(String[] args) {
		// Load users settings if they specified a settings file
		if (args.length > 0)
			Settings.loadSettings(args[0]);
		
		SQLConnection.connectToDatabase();
		
		if (!startServer())
			System.exit(-1);
		
		Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

		System.out.println(String.format("Backend started at '%s', to quit send a SIGTERM, SIGINT, or SIGHUP signal"
				+ "(by using a service manager, pressing Ctrl-C command line, the stop button in an IDE, or some other means).", 
				Settings.getInstance().getBaseUri()));
	}
}
