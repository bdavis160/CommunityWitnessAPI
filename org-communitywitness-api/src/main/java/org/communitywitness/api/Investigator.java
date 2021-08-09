package org.communitywitness.api;

import java.sql.*;
import java.util.ArrayList;

public class Investigator extends org.communitywitness.common.Investigator {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public Investigator() { super(); }
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Investigator(int id) throws SQLException {
		setId(id);
		
		// retrieve investigators account info
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT Name, Organization, OrganizationType, Rating " +
				"FROM Investigator " +
				"WHERE ID='%s';", id);
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		if (queryResults.next()) {
			setName(queryResults.getString(1));
			setOrganization(queryResults.getString(2));
			setOrganizationType(queryResults.getString(3));
			setRating(queryResults.getDouble(4));
		} else {
			throw new RuntimeException("Investigator with the supplied ID does not exist in database");
		}


		queryResults.close();
		queryStatement.close();
		
		// retrieve ids of the reports the investigator is investigating
		loadReports();
	}
	
	/**
	 * Retrieves the list of ids of reports this investigator is investigating,
	 * then saves that to this object.
	 * @throws SQLException if no data is found
	 */
	public void loadReports() throws SQLException {
		ArrayList<Integer> reportIds = new ArrayList<>();
		
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT ReportID FROM ReportInvestigations WHERE InvestigatorID='%s'", getId());
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);
		
		while (queryResults.next()) {
			reportIds.add(queryResults.getInt(1));
		}
		
		queryResults.close();
		queryStatement.close();
		
		setReports(reportIds);
	}
	
	/**
	 * Updates the information about an investigator that investigators are allowed to change using
	 * data from an Investigator object.
	 * @param source - an Investigator object containing the updated data
	 */
	public void updateFrom(Investigator source) {
		setName(source.getName());
		setOrganization(source.getOrganization());
		setOrganizationType(source.getOrganizationType());
	}
}
