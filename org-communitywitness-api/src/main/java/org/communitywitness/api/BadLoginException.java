package org.communitywitness.api;

public class BadLoginException extends Exception {

	public BadLoginException(String reason) {
		super(reason);
	}
}
