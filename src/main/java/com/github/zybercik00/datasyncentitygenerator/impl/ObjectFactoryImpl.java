package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;
import com.github.zybercik00.datasyncentitygenerator.JdbcTable;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

@Service
public class ObjectFactoryImpl implements ObjectFactory {

    private final VelocityEngine velocityEngine;
    private final DataSaverImpl dataSaver;
    private final DataTransformImpl transformData;
    private final DataInitImpl dataInit;
    private final ConnectionToDatabaseImpl connectionToDatabase;
    private final VelocityContextFactory velocityContextFactory;
    private final TemplateLoader templateLoader;


    public ObjectFactoryImpl() {
        velocityContextFactory = new VelocityContextFactoryImpl();
        velocityEngine = getVelocityEngine();
        templateLoader = new TemplateLoaderImpl(velocityEngine);
        connectionToDatabase = new ConnectionToDatabaseImpl();
        dataInit = new DataInitImpl();
        transformData = new DataTransformImpl();
        dataSaver = new DataSaverImpl(velocityContextFactory, templateLoader);
    }

    @Override
    public  DataLoader getDataLoader(DatabaseMetaData metaData) {
        return new DataLoaderImpl(metaData);
    }

    @Override
    public  DataSaver getDataSaver() {
        return dataSaver;
    }

    @Override
    public DataTransform getTransformData() {
        return transformData;
    }

    @Override
    public  DataInit getDataInit() {
        return dataInit;
    }

    @Override
    public  ConnectionToDatabase getConnectionToDatabase() {
        return connectionToDatabase;
    }

    @Override
    public VelocityEngine getVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
        return velocityEngine;
    }
    public void run(ObjectFactory objectFactory) throws IOException, SQLException {
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
