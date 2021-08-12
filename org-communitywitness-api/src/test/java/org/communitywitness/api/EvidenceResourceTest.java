package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceResourceTest {
    EvidenceResource res = new EvidenceResource();

    @Test
    void createEvidence() throws SQLException {
        NewEvidenceRequest newEvidenceRequest = new NewEvidenceRequest();
        newEvidenceRequest.setTitle("title from unit test");
        newEvidenceRequest.setType("type from unit test");
        newEvidenceRequest.setTimestamp(LocalDateTime.now());

        int myId = res.createEvidence(newEvidenceRequest);
        assertNotEquals(-1, myId);
    }

    @Test
    void getEvidence() {
        int id = 0;
        Evidence evidence = res.getEvidence(id);
        assertNotNull(evidence);
    }
}