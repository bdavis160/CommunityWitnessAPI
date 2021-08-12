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
		SQLConnection myConnection = new SQLConnection();
		Connection conn = myConnection.databaseConnection();

		// if the investigator is brand new (has not been written to the db yet), it will have an id of UNSET_ID (-1)
		// once the investigator gets written, it is given an id by the db which will be pulled back into the object
		if (getId() == SpecialIds.UNSET_ID) {
			String query = String.format("INSERT INTO investigator (name, organization, organizationtype, rating) " +
							"VALUES ('%s', '%s', '%s', '%s');",
					getName(),
					getOrganization(),
					getOrganizationType(),
					getRating()
			);

			PreparedStatement queryStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
			Statement queryStatement = conn.createStatement();
			String query = String.format("UPDATE investigator " +
							"SET " +
							"name='%s', " +
							"organization='%s', " +
							"organizationtype='%s', " +
							"rating='%s' " +
							"WHERE id='%s';",
					getName(),
					getOrganization(),
					getOrganizationType(),
					getRating(),
					getId()
			);

			queryStatement.executeUpdate(query);
			queryStatement.close();
		}

		return getId();
	}
}
