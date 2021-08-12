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
		setId(id);
		
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT ID, ReportID, InvestigatorID, Contents " +
				"FROM ReportComments " +
				"WHERE ID='%s';", id);
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		if (queryResults.next()) {
			setId(queryResults.getInt(1));
			setReportId(queryResults.getInt(2));
			setInvestigatorId(queryResults.getInt(3));
			setContents(queryResults.getString(4));
		} else {
			throw new RuntimeException("Comment with the supplied ID does not exist in database");
		}

		queryResults.close();
		queryStatement.close();
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
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();

		// if the evidence is brand new (has not been written to the db yet), it will have an id of -1
		// once the evidence gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == SpecialIds.UNSET_ID) {
			String query = String.format("INSERT INTO reportcomments (reportid, investigatorid, contents) " +
							"VALUES ('%s', '%s', '%s');",
					getReportId(),
					getInvestigatorId(),
					getContents()
			);

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
			Statement queryStatement = conn.createStatement();
			String query = String.format("UPDATE reportcomments " +
							"SET " +
							"reportid='%s', " +
							"investigatorid='%s', " +
							"contents='%s' " +
							"WHERE id='%s';",
					getReportId(),
					getInvestigatorId(),
					getContents(),
					getId()
			);

			queryStatement.executeUpdate(query);
			queryStatement.close();
		}
		return getId();
	}
}
