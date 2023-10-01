package com.github.zybercik00.datasyncentitygenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws SQLException, IOException {
        List<JdbcTable> tables = new ArrayList<>();
        List<JavaClass> classes = new ArrayList<>();

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
                statement.execute("""
                        create table MATERIAL
                        (
                            MATERIAL_ID      BIGINT not null primary key,
                            CONTENT          VARCHAR(255),
                            STATUS_INVENTORY VARCHAR(255),
                            LOT              VARCHAR(255),
                            MATERIAL_NAME    VARCHAR(255),
                            WEIGHT           VARCHAR(255),
                            SUPPLIER         BIGINT,
                            WAREHOUSE        BIGINT
                        )
                        """);
            }
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet tableResultSet = metaData.getTables(null, "PUBLIC", null, null)) {
                while (tableResultSet.next()) {
                    String tableName = tableResultSet.getString("TABLE_NAME");
                    JdbcTable table = new JdbcTable();
                    table.setName(tableName);
                    tables.add(table);
                }
            }

            for (JdbcTable table : tables) {
                // TODO [1] fetch columns (names and types)
                List<JdbcColumn> columns = new ArrayList<>();
                try (ResultSet columnsResultSet = metaData.getColumns(null, "PUBLIC", "EXTRACTION", null)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
//                        String columnSize = columns.getString("COLUMN_SIZE");
                        int datatype = columnsResultSet.getInt("DATA_TYPE");
//                        String isNullable = columns.getString("IS_NULLABLE");
//                        String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                        columns.add(new JdbcColumn(columnName, datatype));
                    }
                }
                table.setColumns(columns);
            }

            for (JdbcTable table : tables) {
                JavaClass javaClass = new JavaClass();
                javaClass.setPackageName("com.github.zybercik00.material");
                String entityName = CaseConverter.toPascalCase(table.getName());
                javaClass.setName(entityName);


                ArrayList<JavaField> fields = new ArrayList<>();
                for (JdbcColumn column : table.getColumns()) {
                    Class<?> fieldType = TypesConverter.convertTypes(column.getType());
                    String fieldName = CaseConverter.toCamelCase(column.getName());
                    JavaField javaField = new JavaField(fieldType, fieldName);
                    fields.add(javaField);
                }
                javaClass.setFields(fields);

                classes.add(javaClass);
            }

            for (JavaClass javaClass : classes) {
                Set<String> imports = new LinkedHashSet<>();
                for (JavaField javaField : javaClass.getFields()) {
                    Class<?> fieldType = javaField.getType();
                    imports.add(fieldType.getName());
                }
                javaClass.setImports(imports);
            }

            extractMetaData(tables, metaData);
        }
        String outputPath = "data-sync-entity-generator/src/main/java/";

//        StringBuilder clasTemplate = new StringBuilder();

//        String entityName = null;
//        for (Column column : fields){
//            String type = convertTypesToImports(column.getType());
//            clasTemplate.append(type);
//
//        }
//        for (String table : tables) {
//            // TODO [7] Use string templates
//            entityName = table;
//            clasTemplate.append(String.format("public class %s {%n\n", toPascalCase(entityName)));
//        }
//        for (Column column : fields) {
//            Column columnName = column;
//            String type = convertTypes(column.getType());
//            clasTemplate.append(String.format("private %s; %n", type + toCamelCase(String.valueOf(columnName))));
//        }
//
//        clasTemplate.append("\n}\n");
//
//        String fileName = toPascalCase(entityName) + ".java";
//        String filePath = outputPath + fileName;


        Files.createDirectories(Paths.get(outputPath));
        for (JavaClass aClass : classes) {
            String packageName = aClass.getPackageName();
            Path packageDirectory = Paths.get(outputPath, packageName.replace('.', '/'));
            Files.createDirectories(packageDirectory);

            Path javaSourcePath = packageDirectory.resolve(aClass.getName() + ".java");
            System.out.printf("Writing class %s.%s to %s%n", aClass.getPackageName(), aClass.getName(), javaSourcePath.toAbsolutePath());

            String javaSource = generateJavaSource(aClass);
            Files.writeString(javaSourcePath, javaSource);
        }
    }

    private static String generateJavaSource(JavaClass aClass) {
        // TODO Use Velocity https://en.wikipedia.org/wiki/Apache_Velocity
        // TODO Usage: https://www.baeldung.com/apache-velocity

        String velocityTemplate = """
                package $packageName;

                #foreach( $anImport in $imports )
                import $anImport;
                #end

                public class $name {
                }
                """;

        StringBuilder javaSource = new StringBuilder()
                .append("package ").append(aClass.getPackageName()).append(";\n")
                .append("\n");
        for (String anImport : aClass.getImports()) {
            javaSource.append("import ").append(anImport).append(";\n");
        }
        javaSource.append("\n")
                .append("public class ").append(aClass.getName()).append(" {\n")
                .append("\n");
        for (JavaField field : aClass.getFields()) {
            javaSource.append("  ").append("private ")
                    .append(field.getType().getSimpleName()).append(" ")
                    .append(field.getName()).append(";\n");
        }
        javaSource.append("\n")
                .append("}\n")
        ;
        return javaSource.toString();
    }

    private static void extractMetaData(List<JdbcTable> tables, DatabaseMetaData metaData) throws SQLException {
        for (JdbcTable table : tables) {
            // TODO [5] fetch primary keys
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, "PUBLIC", table.getName())) {
                while (primaryKeys.next()) {
                    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyName = primaryKeys.getString("PK_NAME");

                }
            }
        }
        for (JdbcTable table : tables) {
            // TODO [4] fetch unique indexes
            try (ResultSet tableResultSet = metaData.getIndexInfo(null, null, table.getName(), true, true)) {
                while (tableResultSet.next()) {
                    String uiTable = tableResultSet.getString("INDEX_NAME");
                }
            }
        }
        for (JdbcTable table : tables) {
            // TODO [3] fetch foreign keys
            try (ResultSet foreignKeys = metaData.getExportedKeys(null, null, table.getName())) {
                while (foreignKeys.next()) {
                    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                    String fkTableName = foreignKeys.getString("FKTABLE_NAME");
                    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                }
            }
        }
        for (JdbcTable table : tables) {
            // TODO [6] fetch sequences
            try (ResultSet tableResultSet = metaData.getImportedKeys(null, null, table.getName())) {
                while (tableResultSet.next()) {
                    String sequence = tableResultSet.getString("KEY_SEQ");
                }
            }
        }
    }

//    public static String convertTypesToImports(String value) {
//        switch (value) {
//            case "2":
//                return "import java.math.BigDecimal;\n ";
//            case "93":
//                return "import java.security.Timestamp;\n ";
//            case "-5":
//                return "\n ";
//        }
//        return value;
//    }
//
//    public static Class<?> convertTypes(int sqlType) {
//        return switch (sqlType) {
//            case Types.BIGINT -> Long.class;
//            case Types.TIMESTAMP -> Timestamp.class;
//            case Types.NUMERIC -> BigDecimal.class;
////            case "12" -> "String";
////            case "1" -> "String";
////            case "4" -> "int";
////            case "8" -> "double";
////            case "91" -> "Date";
////            case "92" -> "Time";
////            case "2003" -> "Array ";
////            case "2000" -> "Object";
////            case "70" -> "URL";
////            case "2009" -> "String";
//            default -> throw new IllegalArgumentException("Unexpected type: " + sqlType);
//        };
//    }

}
