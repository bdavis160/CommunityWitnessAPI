package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class InvestigatorTest {
    @Test
    void testChangeAndWriteToDatabase() throws SQLException {
        int id = 1;
        String name = "testname";

        //test write functionality if report already exists
        Investigator investigator = new Investigator(id);

        //store the data so we can write it back
        String realName = investigator.getName();

        investigator.setName(name);
        investigator.writeToDb();

        investigator = new Investigator(id);
        assertEquals(name, investigator.getName());

        investigator.setName(realName);
        investigator.writeToDb();
    }

    @Test
    void testWriteNewInvestigatorToDatabase() throws SQLException {
        Investigator investigator = new Investigator("from unit test", "foo", "bar", 2.0);
        int id = investigator.writeToDb();

        Investigator written = new Investigator(id);
        assertEquals(id, written.getId());
    }

    @Test
    void testLoadReports() throws SQLException {
        Investigator investigator = new Investigator(2);
        investigator.loadReports();
        assertFalse(investigator.getReports().isEmpty());
    }
}