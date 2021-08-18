package org.communitywitness.api;

public class UserRoles {
	/**
	 * The security role associated with witnesses.
	 */
	public static final String WITNESS = "Witness";
	
	/**
	 * The security role associated with investigators.
	 */
	public static final String INVESTIGATOR = "Investigator";
	
	/**
	 * Checks if the given string is a role.
	 * @param role the string to check
	 * @return true if the given string is a role, false otherwise
	 */
	public static boolean isRole(String role) {
		return role == WITNESS || role == INVESTIGATOR;
	}
}
