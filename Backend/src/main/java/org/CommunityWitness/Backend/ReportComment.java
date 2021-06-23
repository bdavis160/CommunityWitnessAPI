package org.CommunityWitness.Backend;

import java.sql.*;

public class ReportComment {
	int id;
	int reportId;
	int investigatorId;
	String contents;
	
	/**
	 * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
	 */
	public ReportComment() {}
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * 
	 * @param id - the id of the witness to lookup in the database.
	 */
	public ReportComment(int id) throws SQLException {
		this.id = id;
		
		// TODO: make databaseConnection() globally accessible so that the code below can be used
//		Connection conn = databaseConnection();
//		String query = String.format("SELECT ID, ReportID, InvestigatorID, Contents " +
//				"FROM ReportComments " +
//				"WHERE ID='%s';", id);
//		Statement queryStatement = conn.createStatement();
//		ResultSet queryResults = queryStatement.executeQuery(query);
//
//		while (queryResults.next()) {
//			this.id = queryResults.getInt(1);
//			this.reportId = queryResults.getInt(2);
//			this.investigatorId = queryResults.getInt(3);
//			this.contents = queryResults.getString(4);
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
	
	public int getReportID() {
		return reportId;
	}
	
	public void setReportID(int reportID) {
		this.reportId = reportID;
	}
	
	public int getInvestigatorID() {
		return investigatorId;
	}
	
	public void setInvestigatorID(int investigatorID) {
		this.investigatorId = investigatorID;
	}
	
	public String getContents() {
		return contents;
	}
	
	public void setContents(String contents) {
		this.contents = contents;
	}
}
