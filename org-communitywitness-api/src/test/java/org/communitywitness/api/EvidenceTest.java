package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EvidenceTest {
    private static Evidence evidence = new Evidence();

    @Test
    void testChangeAndWriteToDatabase() throws SQLException {
        int testId = 283;
        String testTitle = "description from testChangeAndWriteToDatabase unit test";
        String testType = "type from testChangeAndWriteToDatabase unit test";
        // Needed to truncate this to microseconds to get the assertion to work
        // Database chops off the last three digits on write
        LocalDateTime testTime = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        int testReportId = 0;

        Evidence evidence = new Evidence(testId);

        //store the data so we can write it back
        String realTitle = evidence.getTitle();
        String realType = evidence.getType();
        LocalDateTime realTime = evidence.getTimestamp();
        int realReportId = evidence.getReportId();

        //make changes and write to db
        evidence.setTitle(testTitle);
        evidence.setType(testType);
        evidence.setTimestamp(testTime);
        evidence.setReportId(testReportId);
        evidence.writeToDb();

        //pull the record and check that everything was updated
        evidence = new Evidence(testId);
        assertEquals(testTitle, evidence.getTitle());
        assertEquals(testType, evidence.getType());
        assertEquals(testTime, evidence.getTimestamp());
        assertEquals(testReportId, evidence.getReportId());

        //roll back the changes
        evidence.setTitle(realTitle);
        evidence.setType(realType);
        evidence.setTimestamp(realTime);
        evidence.setReportId(realReportId);
        evidence.writeToDb();
    }

    @Test
    void testWriteNewEvidenceToDatabase() throws SQLException {
        String testTitle = "description from testWriteNewEvidenceToDatabase unit test";
        String testType = "type from testWriteNewEvidenceToDatabase unit test";
        LocalDateTime testTime = LocalDateTime.now();
        int testReportId = 0;

        NewEvidenceRequest evidenceRequest = new NewEvidenceRequest();
        evidenceRequest.setTitle(testTitle);
        evidenceRequest.setType(testType);
        evidenceRequest.setTimestamp(testTime);
        evidenceRequest.setReportId(testReportId);
        Evidence evidence = new Evidence(evidenceRequest);
        int id = evidence.writeToDb();

        assertNotEquals(-1, id);
        // in the spirit of "nothing gets deleted", this test data is sticking around for the time being as well.
        // Don't really feel like writing a delete method if we aren't planning on using that in production code.
    }

    @Test
    void setTitle() {
        String testTitle = "Foo";
        evidence.setTitle(testTitle);
        assertEquals(testTitle, evidence.getTitle());
    }

    @Test
    void setType() {
        String testType = "Foo";
        evidence.setType(testType);
        assertEquals(testType, evidence.getType());
    }

    @Test
    void setTimestamp() {
        LocalDateTime testTime = LocalDateTime.now();
        evidence.setTimestamp(testTime);
        assertEquals(testTime, evidence.getTimestamp());
    }

    @Test
    void setReportId() {
        int testReportId = 1;
        evidence.setReportId(testReportId);
        assertEquals(testReportId, evidence.getReportId());
    }
}