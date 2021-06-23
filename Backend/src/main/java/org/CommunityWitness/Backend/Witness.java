package org.CommunityWitness.Backend;

import java.sql.*;

public class Witness {
	int id;
	String name;
	double rating;
	String location;
	
	/**
	 * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
	 */
	public Witness() {}
	
	/**
	 * Constructor that looks up a witness in the database then fills that data into the object.
	 * 
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Witness(int id) throws SQLException {
		this.id = id;
		
		// TODO: make databaseConnection() globally accessible so that the code below can be used
//		Connection conn = databaseConnection();
//		String myQuery = String.format("SELECT Name, Rating, Location " +
//				"FROM Witness " +
//				"WHERE ID='%s';", id);
//		Statement st = conn.createStatement();
//		ResultSet rs = st.executeQuery(myQuery);
//
//		while (rs.next()) {
//			this.name = rs.getString(1);
//			this.rating = rs.getDouble(2);
//			this.location = rs.getString(3);
//		}
//
//		rs.close();
//		st.close();
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
	
	public double getRating() {
		return rating;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
