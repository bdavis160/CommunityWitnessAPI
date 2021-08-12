package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WitnessResourceTest {
    WitnessResource res = new WitnessResource();

    @Test
    void createWitness() throws SQLException {
        int myId = res.createWitness("from createWitness unit test", "test createWitness location");
        assertNotEquals(-1, myId);
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
        double updateRating = 0.999;   // not actually changed but used for creating test object
        String updateLocation = "test updateWitness location";

        Witness update = new Witness(updateName, updateRating, updateLocation);
        res.updateWitness(id, update);

        // Pull it back out of the db to assert values
        Witness changed = new Witness(id);
        assertEquals(updateName, changed.getName());
        assertEquals(updateLocation, changed.getLocation());

        // Change it back
        res.updateWitness(id, witness);
        Witness changedBack = new Witness(id);
        assertEquals(realName, changedBack.getName());
        assertEquals(realLocation, changedBack.getLocation());
    }
}