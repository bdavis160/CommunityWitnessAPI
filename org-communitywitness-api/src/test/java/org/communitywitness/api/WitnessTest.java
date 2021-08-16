package org.communitywitness.api;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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
            new Witness(-1);
        });
    }

    @Test
    void getReportsForWitnessWithReportsSucceeds() throws SQLException {
        Witness witness = new Witness(1);
        assertFalse(witness.getReports().isEmpty());
    }

    @Test
    void updateFromCorrectlyUpdatesWitnessInfo() throws SQLException {
        Witness source = new Witness();
        Witness destination = new Witness();

        source.setName("sourceName");
        source.setLocation("sourceLocation");
        destination.setName("destinationName");
        destination.setLocation("destinationLocation");

        destination.updateFrom(source);
        Witness check = new Witness(destination.getId());

        assertEquals(source.getName(), check.getName());
        assertEquals(source.getLocation(), check.getLocation());

        check.setName("destinationName");
        check.setLocation("destinationLocation");

        check.writeToDb();
    }

    @Test
    void writeNewWitnessToDatabase() throws SQLException {
        String name = "from new witness unit test";
        double rating = 0.999;
        String location = "bar";

        Witness witness = new Witness(name, rating, location);
        witness.writeToDb();
    }

    @Test
    void modifyExistingWitness() throws SQLException {
        int id = 0;
        String name = "foobar";
        double rating = 0.999;
        String location = "main street";

        //pull our test witness
        Witness witness = new Witness(id);

        //store the data so we can write it back
        String realName = witness.getName();
        double realRating = witness.getRating();
        String realLocation = witness.getLocation();

        //make changes and write to db
        witness.setName(name);
        witness.setRating(rating);
        witness.setLocation(location);
        witness.writeToDb();

        //pull the record and check that everything was updated
        Witness modifiedWitness = new Witness(id);
        assertEquals(name, modifiedWitness.getName());
        assertEquals(rating, modifiedWitness.getRating());
        assertEquals(location , modifiedWitness.getLocation());

        //roll back the changes
        modifiedWitness.setName(realName);
        modifiedWitness.setRating(realRating);
        modifiedWitness.setLocation(realLocation);
        modifiedWitness.writeToDb();
    }
}
