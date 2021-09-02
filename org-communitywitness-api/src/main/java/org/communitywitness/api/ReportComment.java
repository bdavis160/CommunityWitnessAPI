package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;

public class ReportComment extends org.communitywitness.common.ReportComment {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public ReportComment() { super(); }
	
	/**
	 * Constructor that looks up a report comment in the database then fills that data into the object.
	 * @param id - the id of the witness to lookup in the database.
	 */
	public ReportComment(int id) throws SQLException {
		Connection conn = SQLConnection.databaseConnection();
		String query = "SELECT ID, ReportID, InvestigatorID, Contents " +
				"FROM ReportComments " +
				"WHERE ID=?";
		PreparedStatement queryStatement = conn.prepareStatement(query);
		queryStatement.setInt(1, id);
		ResultSet queryResults = queryStatement.executeQuery();

		if (queryResults.next()) {
			setId(id);
			setId(queryResults.getInt(1));
			setReportId(queryResults.getInt(2));
			setInvestigatorId(queryResults.getInt(3));
			setContents(queryResults.getString(4));
		} else {
			throw new RuntimeException("Comment with the supplied ID does not exist in database");
		}

		SQLConnection.closeDbOperation(conn, queryStatement, queryResults);
	}

	/**
	 * Constructor that converts a ReportCommentRequest to a ReportComment object
	 * @param reportCommentRequest - the id of the witness to lookup in the database.
	 */
	public ReportComment(ReportCommentRequest reportCommentRequest) {
		setReportId(reportCommentRequest.getReportId());
		setInvestigatorId(reportCommentRequest.getInvestigatorId());
		setContents(reportCommentRequest.getContents());
	}

	/**
	 * Writes the current ReportComment object out to the database
	 * Updates the ReportComment with the current ID if one exists
	 * Returns the id of the object (newly generated if inserting a new reportComment)
	 *
	 * @throws SQLException if unable to write
	 */
	public int writeToDb() throws SQLException {
		Connection conn = SQLConnection.databaseConnection();

		// if the evidence is brand new (has not been written to the db yet), it will have an id of -1
		// once the evidence gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == SpecialIds.UNSET_ID) {
			String query = "INSERT INTO reportcomments (reportid, investigatorid, contents) " +
							"VALUES (?,?,?)";
			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			queryStatement.setInt(1, getReportId());
			queryStatement.setInt(2, getInvestigatorId());
			queryStatement.setString(3, getContents());

			int rows = queryStatement.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Report comment insertion failed");
			}

			try (ResultSet ids = queryStatement.getGeneratedKeys()) {
				if (ids.next()) {
					setId(ids.getInt(1));
				} else {
					throw new SQLException("ID retrieval failed");
				}
			}

			queryStatement.close();
			//otherwise, we know that this evidence already has a place in the database and just needs updated
		} else {
			String query = "UPDATE reportcomments " +
							"SET " +
							"reportid=?, " +
							"investigatorid=?, " +
							"contents=? " +
							"WHERE id=?";

			PreparedStatement queryStatement = conn.prepareStatement(query);
			queryStatement.setInt(1, getReportId());
			queryStatement.setInt(2, getInvestigatorId());
			queryStatement.setString(3, getContents());
			queryStatement.setInt(4, getId());

			queryStatement.executeUpdate();
			queryStatement.close();
		}
		
		conn.close();
		return getId();
	}
}
