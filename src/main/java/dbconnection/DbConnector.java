package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnector {

    Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:datasyncdb;INIT=CREATE SCHEMA IF NOT EXISTS datasyncdb",
            "admin",
            "password");

    public DbConnector() throws SQLException {
    }
}
