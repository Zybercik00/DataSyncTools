package com.github.zybercik00.datasyncentitygenerator.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Log4j2
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
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.properties");
        if (inputStream != null) {
            dbProperties.load(inputStream);
        } else {
            throw new IllegalArgumentException("file not found!");
        }
    }
}
