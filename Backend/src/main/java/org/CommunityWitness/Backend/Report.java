package org.CommunityWitness.Backend;

import java.util.Date;

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
	public Report(int id) {
		this.id = id;
		// TODO: retrieve information from database to fill out object 
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

	public int getWitnessID() {
		return witnessID;
	}

	public void setWitnessID(int witnessID) {
		this.witnessID = witnessID;
	}

}
