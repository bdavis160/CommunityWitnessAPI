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
    AuthenticatedUser witness = new AuthenticatedUser("9XgUrSeCL9orXybssKknDW8vDJE2JkMS");
    AuthenticatedUser investigator = new AuthenticatedUser("dYOsflWZANe6aD2Piil94041GCgi8Qsu");

    EvidenceResourceTest() throws BadLoginException {
    }

    @Test
    void createEvidence() throws IOException, SQLException {
        String location = "test2.png";
        Path path = Paths.get(location);
        String data = Base64.getEncoder().encodeToString(Files.readAllBytes(path));

        NewEvidenceRequest req = new NewEvidenceRequest();
        req.setTitle("From unit test");
        req.setType("image");
        req.setTimestamp(LocalDateTime.now());
        req.setReportId(183);
        req.setData(data);

        int id = res.createEvidence(req, witness);
        assertNotEquals(-1, id);
    }

    @Test
    void getEvidence() {
        int id = 283;
        Evidence evidence = res.getEvidence(id, investigator);
        assertNotNull(evidence);
    }
}