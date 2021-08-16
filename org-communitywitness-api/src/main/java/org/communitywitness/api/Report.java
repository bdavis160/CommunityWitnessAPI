package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Report extends org.communitywitness.common.Report {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
    public Report() { super(); }

    /**
     * Constructor that looks up a report in the database then fills that data into the object.
     * @param id - the id of the report to lookup in the database.
     */
    public Report(int id) throws SQLException {
        setId(id);

        // retrieve the primary info about the report
        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();
        String query = String.format("SELECT resolved, description, time, location, witnessID " +
                "FROM report " +
                "WHERE id='%s';", id);
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        if (queryResults.next()) {
            setResolved(queryResults.getBoolean(1));
            setDescription(queryResults.getString(2));
            setTimestamp(queryResults.getTimestamp(3).toLocalDateTime());
            setLocation(queryResults.getString(4));
            setWitnessId(queryResults.getInt(5));
        } else {
            throw new RuntimeException("Report with the supplied ID does not exist in database");
        }

        queryResults.close();
        queryStatement.close();
        
        // retrieve the ids of the comments and evidence relating to this report
        loadComments(conn);
        loadEvidence(conn);
    }

    /**
     * Constructor that creates a new entry that will be added to the database.
     * @param resolved    - whether or not the case is resolved
     * @param description - user-entered description of the case
     * @param time        - time of occurrence
     * @param location    - location of occurrence
     * @param witnessId   - id of reporting witness
     */
    public Report(boolean resolved, String description, LocalDateTime time, String location, int witnessId) {
    	super(SpecialIds.UNSET_ID, resolved, description, time, location, witnessId, 
    			new ArrayList<Integer>(), new ArrayList<Integer>());
    }

    /**
     * Retrieves the list of the ids of the comments on this report,
     * then saves that in this object.
     * @throws SQLException if no data is found
     */
    public void loadComments(Connection conn) throws SQLException {
        ArrayList<Integer> commentIds = new ArrayList<>();

        String query = String.format("SELECT ID FROM ReportComments WHERE ReportID='%s';", getId());
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            commentIds.add(queryResults.getInt(1));
        }

        setComments(commentIds);
    }

    /**
     * Retrieves the list of the ids of the evidence associated with this report,
     * then saves that in this object.
     * @throws SQLException if no data is found
     */
    public void loadEvidence(Connection conn) throws SQLException {
        ArrayList<Integer> evidenceIds = new ArrayList<>();

        String query = String.format("SELECT ID FROM Evidence WHERE ReportID='%s';", getId());
        Statement queryStatement = conn.createStatement();
        ResultSet queryResults = queryStatement.executeQuery(query);

        while (queryResults.next()) {
            evidenceIds.add(queryResults.getInt(1));
        }

        setEvidence(evidenceIds);
    }

    /**
     * Writes the current Report object out to the database
     * Updates the report with the current ID if one exists
     * Returns the id of the object (newly generated if inserting a new report)
     * @throws SQLException if unable to write
     */
    public int writeToDb() throws SQLException {
        SQLConnection myConnection = new SQLConnection();
        Connection conn = myConnection.databaseConnection();

        // if the report is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
        // once the report gets written, it is given an id by the db which will be pulled back into the object
        if (getId() == SpecialIds.UNSET_ID) {
            String query = String.format("INSERT INTO report (resolved, description, time, location, witnessid) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s');",
                    getResolved(),
                    getDescription(),
                    getTimestamp(),
                    getLocation(),
                    getWitnessId()
            );

            PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int rows = queryStatement.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Report insertion failed");
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
            String query = String.format("UPDATE report " +
                            "SET " +
                            "resolved='%s', " +
                            "description='%s', " +
                            "time='%s', " +
                            "location='%s', " +
                            "witnessid='%s' " +
                            "WHERE id='%s';",
                    getResolved(),
                    getDescription(),
                    getTimestamp(),
                    getLocation(),
                    getWitnessId(),
                    getId()
            );

            queryStatement.executeUpdate(query);
            queryStatement.close();
        }

        return getId();
    }
}
