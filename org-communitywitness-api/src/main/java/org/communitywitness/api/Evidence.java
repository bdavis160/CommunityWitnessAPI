package org.communitywitness.api;

import java.sql.*;

public class Evidence extends org.communitywitness.common.Evidence {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public Evidence() { super(); };
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Evidence(int id) throws SQLException {
		setId(id);
		
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT Title, Type, Timestamp, Link, reportID " +
				"FROM Evidence " +
				"WHERE ID='%s';", id);
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		if (queryResults.next()) {
			setTitle(queryResults.getString(1));
			setType(queryResults.getString(2));
			setTimestamp(queryResults.getDate(3));
			setLink(queryResults.getString(4));
			setReportId(queryResults.getInt(5));
		} else {
			throw new RuntimeException("Evidence with the supplied ID does not exist in database");
		}

		queryResults.close();
		queryStatement.close();
	}

	/**
	 * Writes the current Evidence object out to the database
	 * Updates the Evidence with the current ID if one exists
	 * Returns the id of the object (newly generated if inserting a new evidence)
	 *
	 * @throws SQLException if unable to write
	 */

	public int writeToDb() throws SQLException {
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();

		// if the evidence is brand new (has not been written to the db yet), it will have an id of -1
		// once the evidence gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == -1) {
			String query = String.format("INSERT INTO evidence (title, type, timestamp, link, reportid) " +
							"VALUES ('%s', '%s', '%s', '%s', '%s');",
					getTitle(),
					getType(),
					getTimestamp(),
					getLink(),
					getReportId()
			);

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			int rows = queryStatement.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Evidence insertion failed");
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
			String query = String.format("UPDATE evidence " +
							"SET " +
							"title='%s', " +
							"type='%s', " +
							"timestamp='%s', " +
							"link='%s', " +
							"reportid='%s' " +
							"WHERE id='%s';",
					getTitle(),
					getType(),
					getTimestamp(),
					getLink(),
					getReportId(),
					getId()
			);

			queryStatement.executeUpdate(query);
			queryStatement.close();
		}
		return getId();
	}
}
