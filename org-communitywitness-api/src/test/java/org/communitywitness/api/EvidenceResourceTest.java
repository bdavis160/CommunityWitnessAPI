package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EvidenceResourceTest {
    EvidenceResource res = new EvidenceResource();

    @Test
    void createEvidence() {
    }

    @Test
    void getEvidence() {
        int id = 0;
        Evidence evidence = res.getEvidence(id);
        assertNotNull(evidence);
    }
}