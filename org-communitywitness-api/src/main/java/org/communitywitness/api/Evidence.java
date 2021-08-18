package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;

public class Evidence extends org.communitywitness.common.Evidence {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public Evidence() { super(); }
	
	/**
	 * Constructor that looks up a piece of evidence in the database then fills that data into the object.
	 * @param id - the id of the evidence to lookup in the database.
	 */
	public Evidence(int id) throws SQLException {
		Connection conn = new SQLConnection().databaseConnection();
		String query = "SELECT Title, Type, Timestamp, Link, reportID " +
				"FROM Evidence " +
				"WHERE ID=?";
		PreparedStatement queryStatement = conn.prepareStatement(query);
		queryStatement.setInt(1, id);
		ResultSet queryResults = queryStatement.executeQuery();

		if (queryResults.next()) {
			setId(id);
			setTitle(queryResults.getString(1));
			setType(queryResults.getString(2));
			setTimestamp(queryResults.getTimestamp(3).toLocalDateTime());
			setLink(queryResults.getString(4));
			setReportId(queryResults.getInt(5));
		} else {
			throw new RuntimeException("Evidence with the supplied ID does not exist in database");
		}

		// close out sql stuff
		queryStatement.close();
		conn.close();
	}

	/**
	 * A constructor that converts NewEvidenceRequest into an Evidence object
	 * @param newEvidenceRequest object containing the information to be written
	 */
	public Evidence(NewEvidenceRequest newEvidenceRequest) {
		setTitle(newEvidenceRequest.getTitle());
		setType(newEvidenceRequest.getType());
		setTimestamp(newEvidenceRequest.getTimestamp());
		setLink(newEvidenceRequest.getLink());
		setReportId(newEvidenceRequest.getReportId());
	}

	/**
	 * Writes the current Evidence object out to the database
	 * Updates the Evidence with the current ID if one exists
	 * Returns the id of the object (newly generated if inserting a new evidence)
	 *
	 * @throws SQLException if unable to write
	 */

	public int writeToDb() throws SQLException {
		Connection conn = new SQLConnection().databaseConnection();

		// if the evidence is brand new (has not been written to the db yet), it will have an id of -1
		// once the evidence gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == SpecialIds.UNSET_ID) {
			String query = "INSERT INTO evidence (title, type, timestamp, link, reportid) " +
							"VALUES (?,?,?,?,?);";

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			queryStatement.setString(1, getTitle());
			queryStatement.setString(2, getType());
			queryStatement.setTimestamp(3, java.sql.Timestamp.valueOf(getTimestamp()));
			queryStatement.setString(4, getLink());
			queryStatement.setInt(5, getReportId());

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
			String query = "UPDATE evidence " +
							"SET " +
							"title=?, " +
							"type=?, " +
							"timestamp=?, " +
							"link=?, " +
							"reportid=? " +
							"WHERE id=?";

			PreparedStatement queryStatement = conn.prepareStatement(query);
			queryStatement.setString(1, getTitle());
			queryStatement.setString(2, getType());
			queryStatement.setTimestamp(3, java.sql.Timestamp.valueOf(getTimestamp()));
			queryStatement.setString(4, getLink());
			queryStatement.setInt(5, getReportId());
			queryStatement.setInt(6, getId());

			queryStatement.executeUpdate();
			queryStatement.close();
		}
		return getId();
	}
}
