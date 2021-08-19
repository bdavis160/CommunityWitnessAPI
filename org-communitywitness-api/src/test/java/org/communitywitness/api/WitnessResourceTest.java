package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WitnessResourceTest {
    WitnessResource res = new WitnessResource();

    @Test
    void createWitness() throws SQLException {
        WitnessRequest witnessRequest = new WitnessRequest();
        witnessRequest.setName("name from createWitness unit test");
        witnessRequest.setLocation("location from createWitness unit test");
        String apiKey = res.createWitness(witnessRequest);
        assertNotNull(apiKey);
    }

    @Test
    void getWitness() {
        int id = 0;
        Witness witness = res.getWitness(id);
        assertNotNull(witness);
    }

    @Test
    void updateWitness() throws SQLException {
        int id = 1;
        Witness witness = res.getWitness(id);

        // Store these so we can write them back
        String realName = witness.getName();
        String realLocation = witness.getLocation();

        // Values we'll use to update
        String updateName = "from updateWitness unit test";
        String updateLocation = "test updateWitness location";

        WitnessRequest update = new WitnessRequest();
        update.setName(updateName);
        update.setLocation(updateLocation);
        res.updateWitness(id, update);

        // Pull it back out of the db to assert values
        Witness changed = new Witness(id);
        assertEquals(updateName, changed.getName());
        assertEquals(updateLocation, changed.getLocation());

        // Change it back
        update.setName(witness.getName());
        update.setLocation(witness.getLocation());
        res.updateWitness(id, update);
        Witness changedBack = new Witness(id);
        assertEquals(realName, changedBack.getName());
        assertEquals(realLocation, changedBack.getLocation());
    }
}