package com.github.zybercik00.datasyncentitygenerator;

import com.github.zybercik00.datasyncentitygenerator.impl.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

@Log4j2
@Component
public class DataSyncEntityGeneratorApplication {

    private final ConnectionToDatabase connectionToDatabase;
    private final DataInit dataInit;
    private final DataLoaderFactory dataLoaderFactory;
    private final DataTransform dataTransform;
    private final DataSaver dataSaver;


    public DataSyncEntityGeneratorApplication(ConnectionToDatabase connectionToDatabase,
                                              DataInit dataInit,
                                              DataLoaderFactory dataLoaderFactory,
                                              DataTransform dataTransform,
                                              DataSaver dataSaver) {
        this.dataLoaderFactory = dataLoaderFactory;
        this.connectionToDatabase = connectionToDatabase;
        this.dataInit = dataInit;
        this.dataTransform = dataTransform;
        this.dataSaver = dataSaver;
    }

    public static void main(String[] args) throws Exception {

        try ( var context = new AnnotationConfigApplicationContext() ) {
            context.scan("com.github.zybercik00.datasyncentitygenerator");
            context.refresh();

            DataSyncEntityGeneratorApplication application = context.getBean(DataSyncEntityGeneratorApplication.class);
            application.run();
        }

    }
    public void run() throws IOException, SQLException {
        List<JdbcTable> tables;
        connectionToDatabase.loadProperties();
        // TODO [8] Connection from springboot
        try (Connection connection = connectionToDatabase.getConnection()) {
            dataInit.loadSchema(connection);

            DatabaseMetaData metaData = connection.getMetaData();
            DataLoader dataLoader = dataLoaderFactory.getDataLoader(metaData);
            tables = dataLoader.loadTables();
        }
        List<JavaClass> classes = dataTransform.transformTablesToJavaSources(tables);
        dataSaver.emitJavaSources(classes);
    }

}
