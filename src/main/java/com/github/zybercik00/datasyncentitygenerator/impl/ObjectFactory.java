package com.github.zybercik00.datasyncentitygenerator.impl;

import org.apache.velocity.app.VelocityEngine;

import java.sql.DatabaseMetaData;

public interface ObjectFactory {
    DataLoader getDataLoader(DatabaseMetaData metaData);

    DataSaver getDataSaver();

    DataTransform getTransformData();

    DataInit getDataInit();

    ConnectionToDatabase getConnectionToDatabase();

    VelocityEngine getVelocityEngine();
}
