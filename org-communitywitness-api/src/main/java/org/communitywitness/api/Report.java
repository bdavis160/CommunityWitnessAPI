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
        // retrieve the primary info about the report
        Connection conn = SQLConnection.databaseConnection();
        String query = "SELECT resolved, description, time, location, witnessID " +
                "FROM report " +
                "WHERE id=?";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, id);
        ResultSet queryResults = queryStatement.executeQuery();

        if (queryResults.next()) {
            setId(id);
            setResolved(queryResults.getBoolean(1));
            setDescription(queryResults.getString(2));
            setTimestamp(queryResults.getTimestamp(3).toLocalDateTime());
            setLocation(queryResults.getString(4));
            setWitnessId(queryResults.getInt(5));
        } else {
        	SQLConnection.closeDbOperation(conn, queryStatement, queryResults);
            throw new RuntimeException("Report with the supplied ID does not exist in database");
        }

        // retrieve the ids of the comments and evidence relating to this report
        loadComments(conn);
        loadEvidence(conn);

        // close out sql stuff
        SQLConnection.closeDbOperation(conn, queryStatement, queryResults);
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

        String query = "SELECT ID FROM ReportComments WHERE ReportID=?";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, getId());
        ResultSet queryResults = queryStatement.executeQuery();

        while (queryResults.next()) {
            commentIds.add(queryResults.getInt(1));
        }
        queryStatement.close();
        setComments(commentIds);
    }

    /**
     * Retrieves the list of the ids of the evidence associated with this report,
     * then saves that in this object.
     * @throws SQLException if no data is found
     */
    public void loadEvidence(Connection conn) throws SQLException {
        ArrayList<Integer> evidenceIds = new ArrayList<>();

        String query = "SELECT ID FROM Evidence WHERE ReportID=?";
        PreparedStatement queryStatement = conn.prepareStatement(query);
        queryStatement.setInt(1, getId());
        ResultSet queryResults = queryStatement.executeQuery();

        while (queryResults.next()) {
            evidenceIds.add(queryResults.getInt(1));
        }
        setEvidence(evidenceIds);

        // close out sql stuff (not connection, calling routine may need it)
        queryStatement.close();
    }

    /**
     * Writes the current Report object out to the database
     * Updates the report with the current ID if one exists
     * Returns the id of the object (newly generated if inserting a new report)
     * @throws SQLException if unable to write
     */
    public int writeToDb() throws SQLException {
        Connection conn = SQLConnection.databaseConnection();

        // if the report is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
        // once the report gets written, it is given an id by the db which will be pulled back into the object
        if (getId() == SpecialIds.UNSET_ID) {
            String query = "INSERT INTO report (resolved, description, time, location, witnessid) " +
                            "VALUES (?,?,?,?,?)";

            PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            queryStatement.setBoolean(1, getResolved());
            queryStatement.setString(2, getDescription());
            queryStatement.setTimestamp(3, java.sql.Timestamp.valueOf(getTimestamp()));
            queryStatement.setString(4, getLocation());
            queryStatement.setInt(5, getWitnessId());

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
            String query = "UPDATE report " +
                            "SET " +
                            "resolved=?, " +
                            "description=?, " +
                            "time=?, " +
                            "location=?, " +
                            "witnessid=? " +
                            "WHERE id=?";

            PreparedStatement queryStatement = conn.prepareStatement(query);
            queryStatement.setBoolean(1, getResolved());
            queryStatement.setString(2, getDescription());
            queryStatement.setTimestamp(3, java.sql.Timestamp.valueOf(getTimestamp()));
            queryStatement.setString(4, getLocation());
            queryStatement.setInt(5, getWitnessId());
            queryStatement.setInt(6, getId());

            queryStatement.executeUpdate();
            queryStatement.close();
        }

        conn.close();
        return getId();
    }
}
