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
        setId(id);

        // retrieve witnesses account info
        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String myQuery = String.format("SELECT Name, Rating, Location " +
                "FROM Witness " +
                "WHERE ID='%s';", id);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(myQuery);

        if (rs.next()) {
            setName(rs.getString(1));
            setRating(rs.getDouble(2));
            setLocation(rs.getString(3));
        } else {
            throw new RuntimeException("Witness with the supplied ID does not exist in database");
        }

        rs.close();
        st.close();
        
        // retrieve ids of the witnesses filed cases
        loadReports();
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
    public void loadReports() throws SQLException {
        ArrayList<Integer> reportIds = new ArrayList<>();

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT ID FROM Report WHERE WitnessID='%s'", getId());
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            reportIds.add(queryResults.getInt(1));
        }

        queryResults.close();
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
        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();

        // if the report is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
        // once the report gets written, it is given an id by the db which will be pulled back into the object
        if (getId() == SpecialIds.UNSET_ID) {
            String query = String.format("INSERT INTO witness (name, rating, location) " +
                            "VALUES ('%s', '%s', '%s');",
                    getName(),
                    getRating(),
                    getLocation()
            );

            PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
            Statement queryStatement = conn.createStatement();
            String query = String.format("UPDATE witness " +
                            "SET " +
                            "name='%s', " +
                            "rating='%s', " +
                            "location='%s' " +
                            "WHERE id='%s';",
                    getName(),
                    getRating(),
                    getLocation(),
                    getId()
            );

            queryStatement.executeUpdate(query);
            queryStatement.close();
        }

        return getId();
    }
}
