package com.github.zybercik00.datasyncentitygenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws SQLException {
        List<String> tables = new ArrayList<>();
        // TODO [8] Connection from springboot
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "admin", "password")) {
            //
            try (Statement statement = connection.createStatement()) {
                // TODO [9] Install using liquibase
                statement.execute("""
                        create table EXTRACTION
                        (
                            EXTRACTION_ID    BIGINT not null primary key,
                            PREPARED_ON        TIMESTAMP,
                            RECEIVED_IN_BERN   TIMESTAMP,
                            SAMPLE_TEST_RESULT NUMERIC,
                            WEIGHT_AFTER       NUMERIC,
                            WEIGHT_BEFORE      NUMERIC,
                            MATERIAL           BIGINT
                        )
                        """);
            }
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tableResultSet = metaData.getTables(null, "PUBLIC", null, null)) {
                while (tableResultSet.next()) {
                    String tableName = tableResultSet.getString("TABLE_NAME");
                    tables.add(tableName);
                }
            }
            // TODO [1] fetch columns (names and types)
            // TODO [5] fetch primary keys
            // TODO [4] fetch unique indexes
            // TODO [3] fetch foreign keys
            // TODO [6] fetch sequences
        }

        for (String table : tables) {
            // TODO [7] Use string templates
            String entityName = table; // TODO [2] Capitalize, FOO_BAR replace with FooBar
            System.out.printf("public class %s {}%n", entityName);
        }
    }

}
