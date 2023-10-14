package com.github.zybercik00.datasyncentitygenerator.impl;

import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;

@Component
public class DataLoaderFactoryImpl implements DataLoaderFactory{


    @Override
    public DataLoader getDataLoader(DatabaseMetaData metaData) {
        return new DataLoaderImpl(metaData);
    }

}
