package com.github.zybercik00.datasyncentitygenerator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
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

                Set<String> imports = null;
                for (JavaClass javaImports : classes) {
                    imports = new LinkedHashSet<>();
                    for (JavaField javaField : javaClass.getFields()) {
                        Class<?> fieldType = javaField.getType();
                        imports.add(fieldType.getName());
                    }
                    javaClass.setImports(imports);
                }
            }
            extractMetaData(tables, metaData);
        }

        String outputPath = "data-sync-entity-generator/src/main/java/";

        Files.createDirectories(Paths.get(outputPath));
        for (JavaClass aClass : classes) {
            String packageName = aClass.getPackageName();
            Path packageDirectory = Paths.get(outputPath, packageName.replace('.', '/'));
            Files.createDirectories(packageDirectory);

            Path javaSourcePath = packageDirectory.resolve(aClass.getName() + ".java");
            System.out.printf("Writing class %s.%s to %s%n", aClass.getPackageName(), aClass.getName(), javaSourcePath.toAbsolutePath());

            Set<String> imports = aClass.getImports();
            List<JavaField> fields = aClass.getFields();
            String name = aClass.getName();

            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty("resource.loader", "class");
            velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            velocityEngine.init();

            Template template = velocityEngine.getTemplate("template.vm");

            VelocityContext context = new VelocityContext();
            context.put("packageName", packageName);
            context.put("imports", imports);
            context.put("name", name);
            context.put("fields", fields);

            StringWriter writer = new StringWriter();
            template.merge(context, writer);

            Files.writeString(javaSourcePath, writer.toString());
            System.out.println("Class: " + aClass.getName() + " was written");
        }

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
}
