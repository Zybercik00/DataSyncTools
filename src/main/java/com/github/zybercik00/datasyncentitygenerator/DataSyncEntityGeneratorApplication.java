package com.github.zybercik00.datasyncentitygenerator;

import lombok.extern.log4j.Log4j2;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@Log4j2
public class DataSyncEntityGeneratorApplication {

    // TODO Move properties to resources
    private static final String jdbcUrl = "db.url";
    private static final String jdbcUser = "db.username";
    private static final String jdbcPassword = "db.password";
    private static final String schemaPattern = "v.schemaPattern";
    private static final String targetPackage = "v.targetPackage";
    private static final String outputDirectory = "v.outputDirectory";
    private static final String velocityTemplate = "v.template";

    public static void main(String[] args) throws Exception {
        List<JdbcTable> tables;
        Properties dbProperties = new Properties();
        dbProperties.load(new FileInputStream("/Users/kamilchmiel/Desktop/Projects/DataSyncExcelDraft/data-sync-entity-generator/src/main/resources/database.properties"));
        // TODO [8] Connection from springboot
        try (Connection connection = DriverManager.getConnection(dbProperties.getProperty(jdbcUrl), dbProperties.getProperty(jdbcUser), dbProperties.getProperty(jdbcPassword))) {
            loadSchema(connection);

            DatabaseMetaData metaData = connection.getMetaData();
            tables = loadTables(metaData);
        }
        List<JavaClass> classes = transformTablesToJavaSources(tables);
        emitJavaSources(classes);

    }

    private static void loadSchema(Connection connection) throws SQLException {
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
    }

    private static List<JdbcTable> loadTables(DatabaseMetaData metaData) throws SQLException {
        List<JdbcTable> tables;
        tables = new ArrayList<>();
        loadTablesInfo(metaData, tables);
        for (JdbcTable table : tables) {
            loadTableColumnsInfo(table, metaData);
        }
        extractMetaData(tables, metaData);
        return tables;
    }

    private static List<JavaClass> transformTablesToJavaSources(List<JdbcTable> tables) {
        List<JavaClass> classes = new ArrayList<>();
        for (JdbcTable table : tables) {
            JavaClass javaClass = transformTableToJavaSource(table);
            classes.add(javaClass);
        }
        return classes;
    }

    private static void emitJavaSources(List<JavaClass> classes) throws IOException {
        String outputPath = outputDirectory;
        Files.createDirectories(Paths.get(outputPath));
        for (JavaClass aClass : classes) {
            emitJavaSource(aClass, outputPath);
        }
    }

    private static void loadTablesInfo(DatabaseMetaData metaData, List<JdbcTable> tables) throws SQLException {
        try (ResultSet tableResultSet = metaData.getTables(null, schemaPattern, null, null)) {
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                JdbcTable table = new JdbcTable();
                table.setName(tableName);
                tables.add(table);
            }
        }
    }

    private static void loadTableColumnsInfo(JdbcTable table, DatabaseMetaData metaData) throws SQLException {
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

    private static JavaClass transformTableToJavaSource(JdbcTable table) {
        JavaClass javaClass = generateJavaClass(table);
        generateClassFields(table, javaClass);
        calculateImports(javaClass);
        return javaClass;
    }

    private static void generateClassFields(JdbcTable table, JavaClass javaClass) {
        ArrayList<JavaField> fields = generateClassFields(table);
        javaClass.setFields(fields);
    }

    private static ArrayList<JavaField> generateClassFields(JdbcTable table) {
        ArrayList<JavaField> fields = new ArrayList<>();
        for (JdbcColumn column : table.getColumns()) {
            JavaField javaField = generateJavaField(column);
            fields.add(javaField);
        }
        return fields;
    }

    private static JavaClass generateJavaClass(JdbcTable table) {
        JavaClass javaClass = new JavaClass();
        javaClass.setPackageName(targetPackage);
        String entityName = CaseConverter.toPascalCase(table.getName());
        javaClass.setName(entityName);
        return javaClass;
    }

    private static JavaField generateJavaField(JdbcColumn column) {
        Class<?> fieldType = TypesConverter.convertTypes(column.getType());
        String fieldName = CaseConverter.toCamelCase(column.getName());
        return new JavaField(fieldType, fieldName);
    }

    private static void calculateImports(JavaClass javaClass) {
        Set<String> imports = new LinkedHashSet<>();
        for (JavaField javaField : javaClass.getFields()) {
            Class<?> fieldType = javaField.getType();
            imports.add(fieldType.getName());
        }
        javaClass.setImports(imports);
    }

    private static void emitJavaSource(JavaClass aClass, String outputPath) throws IOException {
        Path javaSourcePath = getJavaSourcePath(aClass, outputPath);
        Files.createDirectories(javaSourcePath.getParent());
        log.info(() -> "Writing class " + aClass.getPackageName() + "." + aClass.getName() +
                " to file " + javaSourcePath.toAbsolutePath());

        Template template = getTemplate();
        VelocityContext context = getVelocityContext(aClass);

        emitJavaSource(template, context, javaSourcePath);
        log.info(() -> "Class: " + aClass.getName() + " was written");
    }

    private static Template getTemplate() {
        VelocityEngine velocityEngine = getVelocityEngine();
        return velocityEngine.getTemplate(velocityTemplate);
    }

    // TODO Singleton
    private static VelocityEngine getVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
        return velocityEngine;
    }

    private static VelocityContext getVelocityContext(JavaClass aClass) {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("date", new DateTool());
        contextMap.put("javaClass", aClass);
        contextMap.put("system", System.getProperties());
        return new VelocityContext(contextMap);
    }

    private static void emitJavaSource(
            Template template,
            VelocityContext context,
            Path javaSourcePath) throws IOException {
        try ( StringWriter writer = new StringWriter() ) {
            template.merge(context, writer);
            // TODO Format Java source before writing
            Files.writeString(javaSourcePath, writer.toString());
        }
    }

    private static Path getJavaSourcePath(JavaClass aClass, String outputPath) {
        String packageDir = aClass.getPackageName().replace('.', '/');
        Path packagePath = Paths.get(outputPath, packageDir);
        return packagePath.resolve(aClass.getName() + ".java");
    }

    private static void extractMetaData(List<JdbcTable> tables, DatabaseMetaData metaData) throws SQLException {
        for (JdbcTable table : tables) {
            // TODO [5] fetch primary keys
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, schemaPattern, table.getName())) {
                while (primaryKeys.next()) {
                    String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                    String primaryKeyName = primaryKeys.getString("PK_NAME");

                }
            }
        }
        for (JdbcTable table : tables) {
            // TODO [4] fetch unique indexes
            try (ResultSet tableResultSet = metaData.getIndexInfo(null, schemaPattern, table.getName(), true, true)) {
                while (tableResultSet.next()) {
                    String uiTable = tableResultSet.getString("INDEX_NAME");
                }
            }
        }
        for (JdbcTable table : tables) {
            // TODO [3] fetch foreign keys
            try (ResultSet foreignKeys = metaData.getExportedKeys(null, schemaPattern, table.getName())) {
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
            try (ResultSet tableResultSet = metaData.getImportedKeys(null, schemaPattern, table.getName())) {
                while (tableResultSet.next()) {
                    String sequence = tableResultSet.getString("KEY_SEQ");
                }
            }
        }
    }
}
