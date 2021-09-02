package org.communitywitness.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class SQLConnection {
	private static HikariDataSource connectionPool = new HikariDataSource();

	/**
	 * Sets up the database connection pool.
	 */
	public static void connectToDatabase() {
		connectionPool.setJdbcUrl(Settings.getInstance().getDatabaseUrl());
		connectionPool.setUsername(Settings.getInstance().getDatabaseUsername());
		connectionPool.setPassword(Settings.getInstance().getDatabasePassword());
	}

	/**
	 * Returns a connection to the database.
	 * @return a connection to the database
	 * @throws SQLException on database error
	 */
	public static Connection databaseConnection() throws SQLException {
		return connectionPool.getConnection();
	}

	/**
	 * A helper function that closes all the parts of a database operation in the correct order.
	 * @param dbConnection the connection to the database or null
	 * @param statement the statement ran against the database or null
	 * @param results the results returned by the database or null for update statements
	 * @throws SQLException if an error occurs while closing these
	 */
	public static void closeDbOperation(Connection dbConnection, PreparedStatement statement, 
			ResultSet results) throws SQLException {
		if (results != null)
			results.close();
		if (statement != null)
			statement.close();
		if (dbConnection != null)
			dbConnection.close();
	}
}
