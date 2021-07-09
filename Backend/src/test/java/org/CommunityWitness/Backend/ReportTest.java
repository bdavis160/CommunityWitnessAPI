package org.CommunityWitness.Backend;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportTest {
    private static Report report = new Report();

    @Test
    void singleIdConstructor() throws SQLException {
        Report report2 = new Report(0);
        /* commenting out, used for debugging
        System.out.println(report2.id);
        System.out.println(report2.resolved);
        System.out.println(report2.description);
        System.out.println(report2.time);
        System.out.println(report2.location);
        System.out.println(report2.witnessId);
        */
    }

    @Test
    void testWriteToDatabase() throws SQLException {
        int testIdInRange = 0;
        int testIdOutOfRange = 1000;
        boolean testResolved = false;
        String testDescription = "bar";
        Date testTime = new Date();
        String testLocation = "Goodbye";
        int testWitnessId = 5;

        //test write functionality if report already exists
        Report report3 = new Report(testIdInRange);

        //store the data so we can write it back
        // TODO: Make this not dumb. We should mock the db and modify that instead of messing with the real one
        boolean realResolved = report3.resolved;
        String realDescription = report3.description;
        Date realTime = report3.time;
        String realLocation = report3.location;
        int realWitnessId = report3.witnessId;

        //make changes and write to db
        report3.setResolved(testResolved);
        report3.setDescription(testDescription);
        report3.setTime(testTime);
        report3.setLocation(testLocation);
        report3.setWitnessID(testWitnessId);
        report3.writeToDb();

        //pull the record and check that everything was updated
        Report report4 = new Report(testIdInRange);
        assertEquals(testResolved, report4.resolved);
        assertEquals(testDescription, report4.description);
        assertEquals(testTime.toString(), report4.time.toString());
        assertEquals(testLocation, report4.location);
        assertEquals(testWitnessId, report4.witnessId);

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
    void setId() {
        int testId = 10;
        report.setId(testId);
        assertEquals(testId, report.getId());
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