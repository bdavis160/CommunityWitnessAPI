package org.communitywitness.api;

import java.sql.*;

public class ReportComment extends org.communitywitness.common.ReportComment {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public ReportComment() { super(); }
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
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
}
