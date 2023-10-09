package com.github.zybercik00.datasyncentitygenerator;

import com.github.zybercik00.datasyncentitygenerator.impl.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Log4j2
public class DataSyncEntityGeneratorApplication {

    public static void main(String[] args) throws Exception {

        ObjectFactory objectFactory = new ObjectFactoryImpl();
        run(objectFactory);

    }

    private static void run(ObjectFactory objectFactory) throws IOException, SQLException {
        List<JdbcTable> tables;
        objectFactory.getConnectionToDatabase().loadProperties();
        // TODO [8] Connection from springboot
        try (Connection connection = objectFactory.getConnectionToDatabase().getConnection()) {
            objectFactory.getDataInit().loadSchema(connection);

            DatabaseMetaData metaData = connection.getMetaData();
            tables = objectFactory.getDataLoader(metaData).loadTables();
        }
        List<JavaClass> classes = objectFactory.getTransformData().transformTablesToJavaSources(tables);
        objectFactory.getDataSaver().emitJavaSources(classes);
    }

}
