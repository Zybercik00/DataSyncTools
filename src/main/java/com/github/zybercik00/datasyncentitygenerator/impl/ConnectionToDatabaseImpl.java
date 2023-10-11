package com.github.zybercik00.datasyncentitygenerator.impl;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class ConnectionToDatabaseImpl implements ConnectionToDatabase {

    private  final String jdbcUrl = "db.url";
    private  final String jdbcUser = "db.username";
    private  final String jdbcPassword = "db.password";
    private final Properties dbProperties = new Properties();


    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                dbProperties.getProperty(jdbcUrl),
                dbProperties.getProperty(jdbcUser),
                dbProperties.getProperty(jdbcPassword));
    }

    @Override
    public void loadProperties() throws IOException {
        // TODO load from classpath not from File
        dbProperties.load(new FileInputStream("data-sync-entity-generator/src/main/resources/database.properties"));
    }
}
