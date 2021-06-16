package org.CommunityWitness.Backend;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportTest {
    private static Report report;

    static {
        try {
            report = new Report(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
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