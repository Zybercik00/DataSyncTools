package com.github.zybercik00.datasyncentitygenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws SQLException {
        List<String> tables = new ArrayList<>();
        List<Column> fields = new ArrayList<>();
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
                    fields.add(new Column(columnName, datatype));
                }

            }
            // TODO [5] fetch primary keys
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, "PUBLIC", "EXTRACTION")) {
                while (primaryKeys.next()) {
                    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyName = primaryKeys.getString("PK_NAME");

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
        String outputPath = "/Users/kamilchmiel/Desktop/Projects/DataSyncExcelDraft/data-sync-entity-generator/src/main/java/com/github/zybercik00/datasyncentitygenerator/";

        StringBuilder clasTemplate = new StringBuilder();

        String entityName = null;
        for (Column column : fields){
            String type = convertTypesToImports(column.getType());
            clasTemplate.append(type);

        }
        for (String table : tables) {
            // TODO [7] Use string templates
            entityName = table;
            clasTemplate.append(String.format("public class %s {%n\n", CaseConverter.toPascalCase(entityName)));
        }
        for (Column column : fields) {
            Column columnName = column;
            String type = convertTypes(column.getType());
            clasTemplate.append(String.format("private %s; %n", type + CaseConverter.toCamelCase(String.valueOf(columnName))));
        }

        clasTemplate.append("\n}\n");

        String fileName = CaseConverter.toPascalCase(entityName) + ".java";
        String filePath = outputPath + fileName;
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("package com.github.zybercik00.datasyncentitygenerator;\n\n");
            fileWriter.write(clasTemplate.toString());
            fileWriter.close();
            System.out.println("Plik GeneratedClass.java został utworzony.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String convertTypesToImports(String value) {
        switch (value) {
            case "2":
                return "import java.math.BigDecimal;\n ";
            case "93":
                return "import java.security.Timestamp;\n ";
            case "-5":
                return "\n ";
        }
        return value;
    }

    public static String convertTypes(String value) {
        switch (value) {
            case "-5":
               return "long ";
            case "93":
                return "Timestamp ";
            case "2":
                return "BigDecimal ";
            case "12":
                return "String ";
            case "1":
                return "String ";
            case "4":
                return "int ";
            case "8":
                return "double ";
            case "91":
                return "Date ";
            case "92":
                return "Time ";
            case "2003":
                return "Array  ";
            case "2000":
                return "Object ";
            case "70":
                return "URL ";
            case "2009":
                return "String ";
        }
        return value;
    }


}
