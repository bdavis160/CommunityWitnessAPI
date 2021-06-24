package org.CommunityWitness.Backend;

import java.sql.*;

public class Investigator {
	int id;
	String name;
	String organization;
	String organizationType;
	double rating;
	
	/**
	 * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
	 */
	public Investigator() {}
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * 
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Investigator(int id) throws SQLException {
		this.id = id;
		
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();
		String query = String.format("SELECT Name, Organization, OrganizationType, Rating " +
				"FROM Investigator " +
				"WHERE ID='%s';", id);
		Statement queryStatement = conn.createStatement();
		ResultSet queryResults = queryStatement.executeQuery(query);

		while (queryResults.next()) {
			this.name = queryResults.getString(1);
			this.organization = queryResults.getString(2);
			this.organizationType = queryResults.getString(3);
			this.rating = queryResults.getDouble(4);
		}

		queryResults.close();
		queryStatement.close();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getOrganizationType() {
		return organizationType;
	}
	
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}
	
	public double getRating() {
		return rating;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
}
