package com.github.zybercik00.datasyncentitygenerator.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;

@Log4j2
@Component
public class DataLoaderFactoryImpl implements DataLoaderFactory{


    @Override
    public DataLoader getDataLoader(DatabaseMetaData metaData) {
        return new DataLoaderImpl(metaData);
    }

}
