package org.communitywitness.api;

import org.communitywitness.api.Evidence;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceTest {
    private static Evidence evidence = new Evidence();

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
        Date testTime = new Date();
        evidence.setTimestamp(testTime);
        assertEquals(testTime, evidence.getTimestamp());
    }

    @Test
    void setLink() {
        String testLink = "Foo";
        evidence.setLink(testLink);
        assertEquals(testLink, evidence.getLink());
    }

    @Test
    void setReportId() {
        int testReportId = 1;
        evidence.setReportId(testReportId);
        assertEquals(testReportId, evidence.getReportId());
    }
}