package org.CommunityWitness.Backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: determine if comments and evidence should also be pulled into the report class, 
// or at least have methods in the class to grab them
public class Report {
    int id;
    boolean resolved;
    String description;
    Date time;
    String location;
    int witnessId;

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
            this.time = queryResults.getTime(3);
            this.location = queryResults.getString(4);
            this.witnessId = queryResults.getInt(5);
        } else {
            throw new RuntimeException("Report with the supplied ID does not exist in database");
        }

        queryResults.close();
        queryStatement.close();
    }

    /**
     * Retrieves all of the comments on this report from the database and puts them in a list.
     *
     * @return a list containing all of the comments on this report
     */
    public List<ReportComment> getComments() throws SQLException {
        ArrayList<ReportComment> comments = new ArrayList<>();

        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT ID FROM ReportComments WHERE ReportID='%s';", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            int currentCommentId = queryResults.getInt(1);
            ReportComment currentComment = new ReportComment(currentCommentId);
            comments.add(currentComment);
        }

        return comments;
    }

    // Basic getters and setters
    public int getId() {
        return id;
    }

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
