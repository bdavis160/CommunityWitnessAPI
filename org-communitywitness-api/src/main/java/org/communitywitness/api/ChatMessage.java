package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;
import java.time.LocalDateTime;

public class ChatMessage extends org.communitywitness.common.ChatMessage {
    /**
     * Wrapper for base classes 0-parameter constructor.
     * This is needed to allow Jersey to marshal data in and out of JSON.
     */
    public ChatMessage() { super(); }

    /**
     * Constructor that creates a new entry that will be added to the database.
     * @param reportId
     * @param investigatorId
     * @param message
     * @param time
     */
    public ChatMessage(int reportId, int investigatorId, String message, LocalDateTime time) {
        super(SpecialIds.UNSET_ID, reportId, investigatorId, message, time);
    }

    public int writeToDb() throws SQLException {
        Connection conn = SQLConnection.databaseConnection();

        String query = "INSERT INTO chat (reportid, investigatorid, message, time) " +
                "VALUES (?,?,?,?)";

        PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        queryStatement.setInt(1, getReportId());
        queryStatement.setInt(2, getInvestigatorId());
        queryStatement.setString(3, getMessage());
        queryStatement.setTimestamp(4, java.sql.Timestamp.valueOf(getTimestamp()));

        int rows = queryStatement.executeUpdate();
        if (rows == 0) {
        	SQLConnection.closeDbOperation(conn, queryStatement, null);
            throw new SQLException("Message insertion failed");
        }

        try (ResultSet ids = queryStatement.getGeneratedKeys()) {
            if (ids.next()) {
                setId(ids.getInt(1));
            } else {
                throw new SQLException("ID retrieval failed");
            }
        }

        queryStatement.close();
        return getId();
    }
}
