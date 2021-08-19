package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountManagementTest {
	@Test
	void verifyPassword() {
		String aPassword = "password123";
		String hashedPassword = AccountManagement.hashPassword(aPassword);
		assertTrue(AccountManagement.checkPassword(aPassword, hashedPassword));
	}
}
