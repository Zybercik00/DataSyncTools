package com.github.zybercik00.datasyncentitygenerator.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionToDatabase {

    Connection getConnection() throws SQLException;

    void loadProperties() throws IOException;
}
