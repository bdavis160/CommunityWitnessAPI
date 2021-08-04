package org.communitywitness.api;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: determine if comments and evidence should also be pulled into the report class, 
// or at least have methods in the class to grab them
public class Report {
    private int id = -1;
    private boolean resolved;
    private String description;
    private Date time;
    private String location;
    private int witnessId;

    /**
     * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
     */
    public Report() {
    }

    /**
     * Constructor that looks up a report in the database then fills that data into the object.
     *
     * @param id - the id of the report to lookup in the database.
     */
    public Report(int id) throws SQLException {
        this.id = id;

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT resolved, description, time, location, witnessID " +
                "FROM report " +
                "WHERE id='%s';", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        if (queryResults.next()) {
            this.resolved = queryResults.getBoolean(1);
            this.description = queryResults.getString(2);
            this.time = new Date(queryResults.getTimestamp(3).getTime());
            this.location = queryResults.getString(4);
            this.witnessId = queryResults.getInt(5);
        } else {
            throw new RuntimeException("Report with the supplied ID does not exist in database");
        }

        queryResults.close();
        queryStatement.close();
    }

    /**
     * Constructor that creates a new entry that will be added to the database.
     *
     * @param resolved    - whether or not the case is resolved
     * @param description - user-entered description of the case
     * @param time        - time of occurrence
     * @param location    - location of occurrence
     * @param witnessId   - id of reporting witness
     */
    public Report(boolean resolved, String description, Date time, String location, int witnessId) {
        this.resolved = resolved;
        this.description = description;
        this.time = time;
        this.location = location;
        this.witnessId = witnessId;
    }

    /**
     * Returns a list of the ids of the comments on this report from the database.
     *
     * @return a list containing the ids of the comments on this report
     * @throws SQLException if no data is found
     */
    public List<Integer> getComments() throws SQLException {
        ArrayList<Integer> commentIds = new ArrayList<>();

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT ID FROM ReportComments WHERE ReportID='%s';", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            commentIds.add(queryResults.getInt(1));
        }

        return commentIds;
    }

    /**
     * Returns a list of the ids of the evidence associated with this report.
     *
     * @return a list of evidence id numbers
     * @throws SQLException if no data is found
     */
    public List<Integer> getEvidence() throws SQLException {
        ArrayList<Integer> evidenceIds = new ArrayList<>();

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT ID FROM Evidence WHERE ReportID='%s';", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            evidenceIds.add(queryResults.getInt(1));
        }

        return evidenceIds;
    }

    /**
     * Writes the current Report object out to the database
     * Updates the report with the current ID if one exists
     * Returns the id of the object (newly generated if inserting a new report)
     *
     * @throws SQLException if unable to write
     */

    public int writeToDb() throws SQLException {
        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();

        // if the report is brand new (has not been written to the db yet), it will have an id of -1
        // once the report gets written, it is given an id by the db which will be pulled back into the object
        if (this.id == -1) {
            String query = String.format("INSERT INTO report (resolved, description, time, location, witnessid) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s');",
                    this.resolved,
                    this.description,
                    this.time,
                    this.location,
                    this.witnessId
            );

            PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int rows = queryStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Report insertion failed");
            }

            try (ResultSet ids = queryStatement.getGeneratedKeys()) {
                if (ids.next()) {
                    this.id = ids.getInt(1);
                } else {
                    throw new SQLException("ID retrieval failed");
                }
            }

            queryStatement.close();
            //otherwise, we know that this report already has a place in the database and just needs updated
        } else {
            Statement queryStatement = conn.createStatement();
            String query = String.format("UPDATE report " +
                            "SET " +
                            "resolved='%s', " +
                            "description='%s', " +
                            "time='%s', " +
                            "location='%s', " +
                            "witnessid='%s' " +
                            "WHERE id='%s';",
                    this.resolved,
                    this.description,
                    this.time,
                    this.location,
                    this.witnessId,
                    this.id
            );

            queryStatement.executeUpdate(query);
            queryStatement.close();
        }
        return this.id;
    }

    // Basic getters and setters
    public int getId() {
        return id;
    }

    // this is for jersey
    public void setId(int id) {
        this.id = id;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWitnessID() {
        return witnessId;
    }

    public void setWitnessID(int witnessID) {
        this.witnessId = witnessID;
    }

}
