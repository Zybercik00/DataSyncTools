package com.github.zybercik00.datasyncentitygenerator.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataInitImpl implements DataInit {

     @Override
     public void loadSchema(Connection connection) throws SQLException {
        //
        try (Statement statement = connection.createStatement()) {
            // TODO [9] Install using liquibase
            // TODO load scripts from resources
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
}
