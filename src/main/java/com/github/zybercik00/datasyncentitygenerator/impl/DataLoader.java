package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JdbcTable;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public interface DataLoader {

    List<JdbcTable> loadTables() throws SQLException;
}
