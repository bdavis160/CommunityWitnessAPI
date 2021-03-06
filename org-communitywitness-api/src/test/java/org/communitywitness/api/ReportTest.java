package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
    private static Report report = new Report();

    @Test
    void singleIdConstructor() throws SQLException {
        Report report2 = new Report(0);
        assertNotNull(report2.getDescription());
        assertNotNull(report2.getTimestamp());
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
        String testDescription = "description from testChangeAndWriteToDatabase unit test";
        // Needed to truncate this to microseconds to get the assertion to work
        // Database chops off the last three digits on write
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

        String testLocation = "location from testChangeAndWriteToDatabase unit test";
        int testWitnessId = 0;

        Report report3 = new Report(testId);

        //store the data so we can write it back
        boolean realResolved = report3.getResolved();
        String realDescription = report3.getDescription();
        LocalDateTime realTime = report3.getTimestamp();
        String realLocation = report3.getLocation();
        int realWitnessId = report3.getWitnessId();

        //make changes and write to db
        report3.setResolved(testResolved);
        report3.setDescription(testDescription);
        report3.setTimestamp(testTime);
        report3.setLocation(testLocation);
        report3.setWitnessId(testWitnessId);
        report3.writeToDb();

        //pull the record and check that everything was updated
        Report report4 = new Report(testId);
        assertEquals(testResolved, report4.getResolved());
        assertEquals(testDescription, report4.getDescription());
        assertEquals(testTime, report4.getTimestamp());
        assertEquals(testLocation, report4.getLocation());
        assertEquals(testWitnessId, report4.getWitnessId());

        //roll back the changes
        //TODO: see above
        report3.setResolved(realResolved);
        report3.setDescription(realDescription);
        report3.setTimestamp(realTime);
        report3.setLocation(realLocation);
        report3.setWitnessId(realWitnessId);
        report3.writeToDb();
    }

    @Test
    void testWriteNewReportToDatabase() throws SQLException {
        boolean testResolved = false;
        String testDescription = "description from testWriteNewReportToDatabase unit test";
        LocalDateTime testTime = LocalDateTime.now();
        String testLocation = "location from testWriteNewReportToDatabase unit test";
        int testWitnessId = 0;

        Report report = new Report(testResolved, testDescription, testTime, testLocation, testWitnessId);
        int id = report.writeToDb();
        assertNotEquals(-1, id);

        // in the spirit of "nothing gets deleted", this test data is sticking around for the time being as well.
        // Don't really feel like writing a delete method if we aren't planning on using that in production code.
    }

    @Test
    void setResolved() {
        boolean testResolved = true;
        report.setResolved(testResolved);
        assertEquals(testResolved, report.getResolved());
    }

    @Test
    void setDescription() {
        String testDescription = "Here's a test";
        report.setDescription(testDescription);
        assertEquals(testDescription, report.getDescription());
    }

    @Test
    void setTime() {
        LocalDateTime testTime = LocalDateTime.now();
        report.setTimestamp(testTime);
        assertEquals(testTime, report.getTimestamp());
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
        report.setWitnessId(testWitnessId);
        assertEquals(testWitnessId, report.getWitnessId());
    }
}