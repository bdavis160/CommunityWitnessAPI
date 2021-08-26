package org.communitywitness.api;

public class BadLoginException extends Exception {
	// Random serial id to make eclipse happy
	private static final long serialVersionUID = 4159178255142195703L;

	public BadLoginException(String reason) {
		super(reason);
	}
}
