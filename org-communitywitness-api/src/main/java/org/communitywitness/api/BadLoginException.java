package org.communitywitness.api;

public class BadLoginException extends Exception {
	// Random serial id to make eclipse happy
	private static final long serialVersionUID = 4159178255142195703L;

	/**
	 * Constructs an exception regarding bad login data.
	 * @param reason the specific reason that the login failed
	 */
	public BadLoginException(String reason) {
		super(reason);
	}
}
