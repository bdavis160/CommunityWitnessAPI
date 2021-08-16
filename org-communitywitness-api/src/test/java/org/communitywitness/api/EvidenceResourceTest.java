package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EvidenceResourceTest {
    EvidenceResource res = new EvidenceResource();

    @Test
    void createEvidence() throws IOException, SQLException {
        String location = "test.png";
        Path path = Paths.get(location);
        String data = Base64.getEncoder().encodeToString(Files.readAllBytes(path));

        NewEvidenceRequest req = new NewEvidenceRequest();
        req.setTitle("From unit test");
        req.setType("image");
        req.setTimestamp(LocalDateTime.now());
        req.setReportId(2);
        req.setData(data);

        int id = res.createEvidence(req);
        assertNotEquals(-1, id);
    }

    @Test
    void getEvidence() {
        int id = 0;
        Evidence evidence = res.getEvidence(id);
        assertNotNull(evidence);
    }
}