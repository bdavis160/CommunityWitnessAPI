package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;
import java.util.ArrayList;

public class Witness extends org.communitywitness.common.Witness {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
    public Witness() { super(); }

    /**
     * Constructor that looks up a witness in the database then fills that data into the object.
     * @param id - the id of the witness to lookup in the database.
     */
    public Witness(int id) throws SQLException {
        // retrieve witnesses account info
        Connection conn = new SQLConnection().databaseConnection();
        String query = "SELECT Name, Rating, Location " +
                "FROM Witness " +
                "WHERE ID=?";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, id);
        ResultSet queryResults = queryStatement.executeQuery();

        if (queryResults.next()) {
            setId(id);
            setName(queryResults.getString(1));
            setRating(queryResults.getDouble(2));
            setLocation(queryResults.getString(3));
        } else {
            throw new RuntimeException("Witness with the supplied ID does not exist in database");
        }
        // retrieve ids of the witnesses filed cases
        loadReports(conn);

        queryResults.close();
        conn.close();
        

    }

    /**
     * A constructor that converts WitnessRequest into a Witness object
     * @param witnessRequest object containing the information to be written
     */
    public Witness(WitnessRequest witnessRequest) {
        setName(witnessRequest.getName());
        setLocation(witnessRequest.getLocation());
    }

    /**
     * Constructor that creates a new entry that will be added to the database.
     * @param name        - the name of the witness
     * @param rating   - rating of the witness
     * @param location    - location of occurrence
     */
    public Witness(String name, double rating, String location) {
        super(SpecialIds.UNSET_ID, name, rating, location,
                new ArrayList<>());
    }

    /**
     * Retrieves the list of the ids of reports filed by this witness,
     * then saves that to this object.
     */
    public void loadReports(Connection conn) throws SQLException {
        ArrayList<Integer> reportIds = new ArrayList<>();

        String query = "SELECT ID FROM Report WHERE WitnessID=?";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, getId());
        ResultSet queryResults = queryStatement.executeQuery();

        while (queryResults.next()) {
            reportIds.add(queryResults.getInt(1));
        }

        queryStatement.close();
        setReports(reportIds);
    }

    /**
     * Updates the information about a witness that witnesses are allowed to change about themselves,
     * using data from a source Witness object.
     * @param source - a Witness object containing the updated data
     */
    public void updateFrom(Witness source) throws SQLException {
        setName(source.getName());
        setLocation(source.getLocation());
        this.writeToDb();
    }

    /**
     * Writes the current Witness object out to the database
     * Updates the witness with the current ID if one exists
     * Returns the id of the object (newly generated if inserting a new witness)
     * @throws SQLException if unable to write
     */
    public int writeToDb() throws SQLException {
        Connection conn = new SQLConnection().databaseConnection();

        // if the report is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
        // once the report gets written, it is given an id by the db which will be pulled back into the object
        if (getId() == SpecialIds.UNSET_ID) {
            String query = "INSERT INTO witness (name, rating, location) " +
                            "VALUES (?,?,?)";

            PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            queryStatement.setString(1, getName());
            queryStatement.setDouble(2, getRating());
            queryStatement.setString(3, getLocation());

            int rows = queryStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Witness insertion failed");
            }

            try (ResultSet ids = queryStatement.getGeneratedKeys()) {
                if (ids.next()) {
                    setId(ids.getInt(1));
                } else {
                    throw new SQLException("ID retrieval failed");
                }
            }

            queryStatement.close();
            //otherwise, we know that this report already has a place in the database and just needs updated
        } else {
            String query = "UPDATE witness " +
                            "SET " +
                            "name=?, " +
                            "rating=?, " +
                            "location=? " +
                            "WHERE id=?";

            PreparedStatement queryStatement = conn.prepareStatement(query);
            queryStatement.setString(1, getName());
            queryStatement.setDouble(2, getRating());
            queryStatement.setString(3, getLocation());
            queryStatement.setInt(4, getId());

            queryStatement.executeUpdate();
            queryStatement.close();
        }

        return getId();
    }
}
