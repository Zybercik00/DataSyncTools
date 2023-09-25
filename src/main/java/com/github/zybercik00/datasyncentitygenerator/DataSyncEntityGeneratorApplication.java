package com.github.zybercik00.datasyncentitygenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.WordUtils;


public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws SQLException {
        List<String> tables = new ArrayList<>();
        List<String> fields = new ArrayList<>();
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
            try (ResultSet columns = metaData.getColumns(null, "PUBLIC", null, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnSize = columns.getString("COLUMN_SIZE");
                    String datatype = columns.getString("DATA_TYPE");
                    String isNullable = columns.getString("IS_NULLABLE");
                    String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                    fields.add(columnName);
                    fields.add(columnSize);
                    fields.add(datatype);
                    fields.add(isNullable);
                    fields.add(isAutoIncrement);
                }

            }
            // TODO [5] fetch primary keys
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, "PUBLIC", "EXTRACTION")) {
                while (primaryKeys.next()) {
                    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyName = primaryKeys.getString("PK_NAME");
                    fields.add(primaryKeyColumnName);
                    fields.add(primaryKeyName);
                }
            }
            // TODO [4] fetch unique indexes
            try (ResultSet tableResultSet = metaData.getIndexInfo(null, null, "EXTRACTION", true, true)) {
                while (tableResultSet.next()) {
                    String uiTable = tableResultSet.getString("INDEX_NAME");
                }
            }
            // TODO [3] fetch foreign keys
            try (ResultSet foreignKeys = metaData.getExportedKeys(null, null, "EXTRACTION")) {
                while (foreignKeys.next()) {
                    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                    String fkTableName = foreignKeys.getString("FKTABLE_NAME");
                    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                }
            }
            // TODO [6] fetch sequences
            try (ResultSet tableResultSet = metaData.getImportedKeys(null, null, "EXTRACTION")) {
                while (tableResultSet.next()) {
                    String sequence = tableResultSet.getString("KEY_SEQ");
                }
            }
        }

        for (String table : tables) {
            // TODO [7] Use string templates
            String entityName = table; // TODO [2] Capitalize, FOO_BAR replace with FooBar
            System.out.printf("public class %s {}%n", WordUtils.capitalizeFully(entityName));
        }
        for (String column : fields) {
            String columnName = column;
            System.out.println((columnName).toLowerCase().replace("_", ""));
        }



    }

}
