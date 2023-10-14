package com.github.zybercik00.datasyncentitygenerator.impl;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataInit {
    void loadSchema(Connection connection) throws SQLException;
}
