package org.CommunityWitness.Backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Witness {
    private int id;
    private String name;
    private double rating;
    private String location;

    /**
     * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
     */
    public Witness() {
    }

    /**
     * Constructor that looks up a witness in the database then fills that data into the object.
     *
     * @param id - the id of the witness to lookup in the database.
     */
    public Witness(int id) throws SQLException {
        this.id = id;

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String myQuery = String.format("SELECT Name, Rating, Location " +
                "FROM Witness " +
                "WHERE ID='%s';", id);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(myQuery);

        if (rs.next()) {
            this.name = rs.getString(1);
            this.rating = rs.getDouble(2);
            this.location = rs.getString(3);
        } else {
            throw new RuntimeException("Witness with the supplied ID does not exist in database");
        }

        rs.close();
        st.close();
    }

    /**
     * Returns a list of the ids of reports filed by this witness
     *
     * @return a list of report id numbers
     */
    public List<Integer> getReports() throws SQLException {
        ArrayList<Integer> reportIds = new ArrayList<>();

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT ID FROM Report WHERE WitnessID='%s'", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            reportIds.add(queryResults.getInt(1));
        }

        queryResults.close();
        queryStatement.close();

        return reportIds;
    }

    /**
     * Updates the information about a witness that witnesses are allowed to change about themselves,
     * using data from a source Witness object.
     *
     * @param source - a Witness object containing the updated data
     */
    public void updateFrom(Witness source) {
        this.name = source.getName();
        this.location = source.getLocation();
    }

    // Basic getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
