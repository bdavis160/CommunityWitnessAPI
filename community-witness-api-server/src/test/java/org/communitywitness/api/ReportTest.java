package org.communitywitness.api;

import org.communitywitness.api.Report;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportTest {
    private static Report report = new Report();

    @Test
    void singleIdConstructor() throws SQLException {
        Report report2 = new Report(0);
        assertNotNull(report2.getDescription());
        assertNotNull(report2.getTime());
        assertNotNull(report2.getLocation());
    }

    /* TODO: the following two tests should be removed if we move to continuous integration.
    Having tests that modify the production database is a bad idea. The next step would be mocking
    the database, but it's generally pointless to mock infrastructure solely for the purpose of tests.
    Leaving them here for now as we work on things, but really what they're testing is SQL syntax
    and functionality of imported methods. Once these things are understood, the need to continually
    test them becomes obsolete IMO.
     */

    @Test
    void testChangeAndWriteToDatabase() throws SQLException {
        int testId = 0;
        boolean testResolved = false;
        String testDescription = "foo";
        Date testTime = new Date();
        String testLocation = "bar";
        int testWitnessId = 0;

        //test write functionality if report already exists
        Report report3 = new Report(testId);

        //store the data so we can write it back
        boolean realResolved = report3.isResolved();
        String realDescription = report3.getDescription();
        Date realTime = report3.getTime();
        String realLocation = report3.getLocation();
        int realWitnessId = report3.getWitnessID();

        //make changes and write to db
        report3.setResolved(testResolved);
        report3.setDescription(testDescription);
        report3.setTime(testTime);
        report3.setLocation(testLocation);
        report3.setWitnessID(testWitnessId);
        report3.writeToDb();

        //pull the record and check that everything was updated
        Report report4 = new Report(testId);
        assertEquals(testResolved, report4.isResolved());
        assertEquals(testDescription, report4.getDescription());
        assertEquals(testTime.toString(), report4.getTime().toString());
        assertEquals(testLocation, report4.getLocation());
        assertEquals(testWitnessId, report4.getWitnessID());

        //roll back the changes
        //TODO: see above
        report3.setResolved(realResolved);
        report3.setDescription(realDescription);
        report3.setTime(realTime);
        report3.setLocation(realLocation);
        report3.setWitnessID(realWitnessId);
        report3.writeToDb();
    }

    @Test
    void testWriteNewReportToDatabase() throws SQLException {
        boolean testResolved = false;
        String testDescription = "foo";
        Date testTime = new Date();
        String testLocation = "bar";
        int testWitnessId = 0;

        Report report = new Report(testResolved, testDescription, testTime, testLocation, testWitnessId);
        report.writeToDb();

        // in the spirit of "nothing gets deleted", this test data is sticking around for the time being as well.
        // Don't really feel like writing a delete method if we aren't planning on using that in production code.
    }

    @Test
    void setResolved() {
        boolean testResolved = true;
        report.setResolved(testResolved);
        assertEquals(testResolved, report.isResolved());
    }

    @Test
    void setDescription() {
        String testDescription = "Here's a test";
        report.setDescription(testDescription);
        assertEquals(testDescription, report.getDescription());
    }

    @Test
    void setTime() {
        Date testTime = new Date();
        report.setTime(testTime);
        assertEquals(testTime, report.getTime());
    }

    @Test
    void setLocation() {
        String testLocation = "Here's a test";
        report.setLocation(testLocation);
        assertEquals(testLocation, report.getLocation());
    }

    @Test
    void setWitnessID() {
        int testWitnessId = 10;
        report.setWitnessID(testWitnessId);
        assertEquals(testWitnessId, report.getWitnessID());
    }
}