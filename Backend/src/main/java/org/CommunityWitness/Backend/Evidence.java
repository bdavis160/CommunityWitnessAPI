package org.CommunityWitness.Backend;

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
	 * 
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Evidence(int id) throws SQLException {
		this.id = id;
		
		// TODO: make databaseConnection() globally accessible so that the code below can be used
//		Connection conn = databaseConnection();
//		String query = String.format("SELECT Title, Type, Timestamp, Link, reportID " +
//				"FROM Evidence " +
//				"WHERE ID='%s';", id);
//		Statement queryStatement = conn.createStatement();
//		ResultSet queryResults = queryStatement.executeQuery(query);
//
//		while (queryResults.next()) {
//			this.title = queryResults.getString(1);
//			this.type = queryResults.getString(2);
//			this.timestamp = queryResults.getDate(3);
//			this.link = queryResults.getString(4);
//			this.reportId = queryResults.getInt(5);
//		}
//
//		queryResults.close();
//		queryStatement.close();
	}
	
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
}
