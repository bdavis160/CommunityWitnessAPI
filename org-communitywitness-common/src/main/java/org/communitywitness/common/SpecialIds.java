package org.communitywitness.common;

/**
 * A class containing constants for ID numbers with special meanings.
 */
public class SpecialIds {
	/**
	 * The ID number for objects that either aren't in the database yet or are still being constructed.
	 */
	public static final int UNSET_ID = -1;
	
	/**
	 * The ID number used to anonymize an object.
	 */
	public static final int ANONYMIZED_ID = -2;
	
	/**
	 * The ID number used by the authentication system to indicate that user isn't in the role
	 * that the field this number is in is associated with.
	 * For example, if an entry in the ApiKeys table has a WitnessId of USER_NOT_IN_ROLE
	 * then they must be an investigator.
	 * Note that this is set to 0 as that is what is returned from null ints in the database.
	 */
	public static final int USER_NOT_IN_ROLE = 0;
	
	/**
	 * A function which checks if an ID number is any kind of special ID.
	 * @param id the ID number to check
	 * @return true if id is a special id, false if not
	 */
	public static boolean isSpecialId(int id) {
		return id == UNSET_ID || id == ANONYMIZED_ID || id == USER_NOT_IN_ROLE;
	}
}
