package org.communitywitness.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnection {
    protected Connection databaseConnection() throws SQLException {
        String url = "jdbc:postgresql://commdbserver.ddns.net/cw_primary";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "cwdefpass");
        Connection conn = DriverManager.getConnection(url, props);
        return conn;
    }
}
