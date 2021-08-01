package org.communitywitness.api;

import java.sql.*;
import java.util.Date;

public class Evidence {
	int id;
	String title;
	String type;
	Date timestamp;
	String link;
	int reportId;
	
	/**
	 * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
	 */
	public Evidence() {}
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Evidence(int id) throws SQLException {
		this.id = id;
		
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT Title, Type, Timestamp, Link, reportID " +
				"FROM Evidence " +
				"WHERE ID='%s';", id);
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		if (queryResults.next()) {
			this.title = queryResults.getString(1);
			this.type = queryResults.getString(2);
			this.timestamp = queryResults.getDate(3);
			this.link = queryResults.getString(4);
			this.reportId = queryResults.getInt(5);
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
		if (this.id == -1) {
			String query = String.format("INSERT INTO evidence (title, type, timestamp, link, reportid) " +
							"VALUES ('%s', '%s', '%s', '%s', '%s');",
					this.title,
					this.type,
					this.timestamp,
					this.link,
					this.reportId
			);

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			int rows = queryStatement.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Evidence insertion failed");
			}

			try (ResultSet ids = queryStatement.getGeneratedKeys()) {
				if (ids.next()) {
					this.id = ids.getInt(1);
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
					this.title,
					this.type,
					this.timestamp,
					this.link,
					this.reportId,
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

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
}
