package org.CommunityWitness.Backend;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

// TODO: determine if comments and evidence should also be pulled into the report class, 
// or at least have methods in the class to grab them
public class Report {
	int id;
	boolean resolved;
	String description;
	Date time;
	String location;
	int witnessID;
	
	/**
	 * 0-parameter constructor so that Jersey can generate objects for converting to and from JSON
	 */
	public Report() {}
	
	/**
	 * Constructor that looks up a report in the database then fills that data into the object.
	 * @param id - the id of the report to lookup in the database.
	 */
	public Report(int id) throws SQLException {
		this.id = id;
		Connection conn = DriverManager.getConnection("jdbc:postgresql://commdbserver.ddns.net/cw_primary", "postgres", "cwdefpass");
		//Connection conn = databaseConnection();
		String myQuery = String.format("SELECT resolved, description, time, location, witnessID " +
				"FROM report " +
				"WHERE id='%s';", id);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(myQuery);
		while (rs.next()) {
			//System.out.println(rs.getString(1));
			this.resolved = rs.getBoolean(1);
			this.description = rs.getString(2);
			this.time = rs.getTime(3);
			this.location = rs.getString(4);
			this.witnessID = rs.getInt(5);
		}
		rs.close();
		st.close();
	}

	private Connection databaseConnection() throws SQLException {
		String url = "jdbc:postgresql://commdbserver.ddns.net/cw_primary";
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "cwdefpass");
		props.setProperty("ssl", "true");
		Connection conn = DriverManager.getConnection(url, props);
		return conn;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getWitnessID() { return witnessID; }

	public void setWitnessID(int witnessID) {
		this.witnessID = witnessID;
	}

}
