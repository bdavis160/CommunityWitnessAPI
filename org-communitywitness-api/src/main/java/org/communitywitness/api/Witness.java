package org.communitywitness.api;

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
    public void updateFrom(Witness source) {
        setName(source.getName());
        setLocation(source.getLocation());
    }
}
