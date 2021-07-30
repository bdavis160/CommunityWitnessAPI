package org.CommunityWitness.Backend;

public class ShutdownHandler extends Thread {
	/**
	 * Stops the HTTP server before the program exits
	 */
	@Override
	public void run() {
		Server.stopServer();
	}
}
