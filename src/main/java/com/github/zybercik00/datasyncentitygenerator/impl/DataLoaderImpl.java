package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JdbcColumn;
import com.github.zybercik00.datasyncentitygenerator.JdbcTable;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataLoaderImpl implements DataLoader {

    private  final String schemaPattern = "v.schemaPattern";
    private final DatabaseMetaData metaData;

    public DataLoaderImpl(DatabaseMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
     public List<JdbcTable> loadTables() throws SQLException {
        List<JdbcTable> tables;
        tables = new ArrayList<>();
        loadTablesInfo(tables);
        for (JdbcTable table : tables) {
            loadTableColumnsInfo(table);
        }
        extractMetaData(tables);
        return tables;
    }

    private  void loadTablesInfo(List<JdbcTable> tables) throws SQLException {
        try (ResultSet tableResultSet = metaData.getTables(null, schemaPattern, null, null)) {
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                JdbcTable table = new JdbcTable();
                table.setName(tableName);
                tables.add(table);
            }
        }
    }

    private  void loadTableColumnsInfo(JdbcTable table) throws SQLException {
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

    private  void extractMetaData(List<JdbcTable> tables) throws SQLException {
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
