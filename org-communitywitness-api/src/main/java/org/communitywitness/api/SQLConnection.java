package org.communitywitness.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    protected Connection databaseConnection() throws SQLException {
        return DriverManager.getConnection(Settings.getInstance().getDatabaseUrl(),
        		Settings.getInstance().getDatabaseUsername(), 
        		Settings.getInstance().getDatabasePassword());
    }
}
