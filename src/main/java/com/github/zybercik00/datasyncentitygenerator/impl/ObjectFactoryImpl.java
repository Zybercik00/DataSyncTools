package com.github.zybercik00.datasyncentitygenerator.impl;

import org.apache.velocity.app.VelocityEngine;

import java.sql.DatabaseMetaData;

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
}
