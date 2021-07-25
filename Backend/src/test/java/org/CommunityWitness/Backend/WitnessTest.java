package org.CommunityWitness.Backend;

import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WitnessTest {

    @Test
    void validIdConstructorSucceeds() throws SQLException {
        Witness witness = new Witness(0);
        assertNotNull(witness.getName());
        assertNotNull(witness.getLocation());
    }

    @Test
    void invalidIdConstructorThrowsCorrectException() {
        assertThrows(RuntimeException.class, () -> {
            Witness witness = new Witness(-1);
        });
    }

    @Test
    void getReportsForWitnessWithReportsSucceeds() throws SQLException {
        Witness witness = new Witness();
        witness.setId(0);
        List<Integer> witnessReports = new ArrayList<Integer>() {
            {
                add(162);
                add(61);
            }};
        assertEquals(witness.getReports(), witnessReports);
    }

    @Test
    void updateFromCorrectlyUpdatesWitnessInfo() {
        Witness source = new Witness();
        Witness destination = new Witness();

        source.setName("sourceName");
        source.setLocation("sourceLocation");
        destination.setName("destinationName");
        destination.setLocation("destinationLocation");

        destination.updateFrom(source);

        assertEquals(source.getName(), destination.getName());
        assertEquals(source.getLocation(), destination.getLocation());
    }
}