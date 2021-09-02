package org.communitywitness.api;

import org.communitywitness.common.SpecialIds;

import java.sql.*;
import java.util.ArrayList;

public class Investigator extends org.communitywitness.common.Investigator {
	/**
	 * Wrapper for base classes 0-parameter constructor.
	 * This is needed to allow Jersey to marshal data in and out of JSON.
	 */
	public Investigator() { super(); }
	
	/**
	 * Constructor that looks up an investigator in the database then fills that data into the object.
	 * @param id - the id of the witness to lookup in the database.
	 */
	public Investigator(int id) throws SQLException {
		// retrieve investigators account info
		Connection conn = SQLConnection.databaseConnection();
		String query = "SELECT Name, Organization, OrganizationType, Rating " +
				"FROM Investigator " +
				"WHERE ID=?";
		PreparedStatement queryStatement = conn.prepareStatement(query);
		queryStatement.setInt(1, id);
		ResultSet queryResults = queryStatement.executeQuery();

		if (queryResults.next()) {
			setId(id);
			setName(queryResults.getString(1));
			setOrganization(queryResults.getString(2));
			setOrganizationType(queryResults.getString(3));
			setRating(queryResults.getDouble(4));
		} else {
			throw new SQLException("Investigator with the supplied ID does not exist in database");
		}
		// retrieve ids of the reports the investigator is investigating
		loadReports();

		SQLConnection.closeDbOperation(conn, queryStatement, queryResults);
	}

	/**
	 * Constructor that creates a new entry that will be added to the database.
	 * @param name		  		- investigator name
	 * @param organization 		- investigator's organization
	 * @param organizationType  - type of organization
	 * @param rating    		- investigator rating
	 */
	public Investigator(String name, String organization, String organizationType, double rating) {
		super(SpecialIds.UNSET_ID, name, organization, organizationType, rating,
				new ArrayList<Integer>());
	}
	
	/**
	 * Retrieves the list of ids of reports this investigator is investigating,
	 * then saves that to this object.
	 * @throws SQLException if no data is found
	 */
	public void loadReports() throws SQLException {
		ArrayList<Integer> reportIds = new ArrayList<>();

		Connection conn = SQLConnection.databaseConnection();
		String query = "SELECT ReportID FROM ReportInvestigations WHERE InvestigatorID=?";
		PreparedStatement queryStatement = conn.prepareStatement(query);
		queryStatement.setInt(1, getId());
		ResultSet queryResults = queryStatement.executeQuery();
		
		while (queryResults.next()) {
			reportIds.add(queryResults.getInt(1));
		}
		SQLConnection.closeDbOperation(conn, queryStatement, queryResults);
		setReports(reportIds);
	}
	
	/**
	 * Updates the information about an investigator that investigators are allowed to change using
	 * data from an Investigator object.
	 * @param source - an Investigator object containing the updated data
	 */
	public void updateFrom(Investigator source) throws SQLException {
		setName(source.getName());
		setOrganization(source.getOrganization());
		setOrganizationType(source.getOrganizationType());
	}

	/**
	 * Writes the current Investigator object out to the database
	 * Updates the investigator with the current ID if one exists
	 * Returns the id of the object (newly generated if inserting a new investigator)
	 * @throws SQLException if unable to write
	 */
	public int writeToDb() throws SQLException {
		Connection conn = SQLConnection.databaseConnection();

		// if the investigator is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
		// once the investigator gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == SpecialIds.UNSET_ID) {
			String query = "INSERT INTO investigator (name, organization, organizationtype, rating) " +
							"VALUES (?,?,?,?)";

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			queryStatement.setString(1, getName());
			queryStatement.setString(2, getOrganization());
			queryStatement.setString(3, getOrganizationType());
			queryStatement.setDouble(4, getRating());

			int rows = queryStatement.executeUpdate();
			if (rows == 0) {
				throw new SQLException("Investigator insertion failed");
			}

			try (ResultSet ids = queryStatement.getGeneratedKeys()) {
				if (ids.next()) {
					setId(ids.getInt(1));
				} else {
					throw new SQLException("ID retrieval failed");
				}
			}

			queryStatement.close();
			//otherwise, we know that this investigator already has a place in the database and just needs updated
		} else {
			String query = "UPDATE investigator " +
							"SET " +
							"name=?, " +
							"organization=?, " +
							"organizationtype=?, " +
							"rating=? " +
							"WHERE id=?";

			PreparedStatement queryStatement = conn.prepareStatement(query);
			queryStatement.setString(1, getName());
			queryStatement.setString(2, getOrganization());
			queryStatement.setString(3, getOrganizationType());
			queryStatement.setDouble(4, getRating());
			queryStatement.setInt(5, getId());

			queryStatement.executeUpdate();
			queryStatement.close();
		}

		conn.close();
		return getId();
	}

	/**
	 * Marks this investigator as interested in the given case (by reportId)
	 * @throws SQLException if unable to write (most likely due to foreign key constraint
	 */
	public void takeCase(int reportId) throws SQLException {
		Connection conn = SQLConnection.databaseConnection();

		String query = "INSERT INTO reportinvestigations (reportid, investigatorid) " +
				"VALUES (?,?)";

		PreparedStatement queryStatement = conn.prepareStatement(query);
		queryStatement.setInt(1, reportId);
		queryStatement.setInt(2, getId());

		queryStatement.executeUpdate();
		SQLConnection.closeDbOperation(conn, queryStatement, null);
	}
}
