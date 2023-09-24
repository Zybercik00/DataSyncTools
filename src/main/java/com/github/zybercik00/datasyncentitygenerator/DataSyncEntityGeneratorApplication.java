package com.github.zybercik00.datasyncentitygenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.WordUtils;


public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws SQLException {
        List<String> tables = new ArrayList<>();
        List<String> columns = new ArrayList<>();
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
            try (ResultSet tableResultSet = metaData.getColumns(null, "PUBLIC", null, null)) {
                while (tableResultSet.next()) {
                    String columnName = tableResultSet.getString("COLUMN_NAME");
                    System.out.println(columnName);
                }
            }
            // TODO [5] fetch primary keys
            try (ResultSet tableResultSet = metaData.getPrimaryKeys(null, "PUBLIC", "EXTRACTION")) {
                while (tableResultSet.next()) {
                    String pkTable = tableResultSet.getString("PK_NAME");
                    System.out.println(pkTable);
                }
            }
            // TODO [4] fetch unique indexes
            try (ResultSet tableResultSet = metaData.getIndexInfo(null, null, "EXTRACTION", true, true)) {
                while (tableResultSet.next()) {
                    String uiTable = tableResultSet.getString("COLUMN_NAME");
                    System.out.println(uiTable);
                }
            }
            // TODO [3] fetch foreign keys
            try (ResultSet tableResultSet = metaData.getExportedKeys(null, null, "EXTRACTION")) {
                while (tableResultSet.next()) {
                    String fkTable = tableResultSet.getString("FKCOLUMN_NAME");
                    System.out.println(fkTable);
                }
            }
            // TODO [6] fetch sequences
            try (ResultSet tableResultSet = metaData.getImportedKeys(null, null, "EXTRACTION")) {
                while (tableResultSet.next()) {
                    String sequence = tableResultSet.getString("KEY_SEQ");
                    System.out.println(sequence);
                }
            }
        }

        for (String table : tables) {
            // TODO [7] Use string templates
            String entityName = table; // TODO [2] Capitalize, FOO_BAR replace with FooBar
            System.out.printf("public class %s {}%n", WordUtils.capitalizeFully(entityName));
        }



    }

}
