package com.github.zybercik00.datasyncentitygenerator.impl;

import java.sql.DatabaseMetaData;

public interface DataLoaderFactory {

    DataLoader getDataLoader(DatabaseMetaData metaData);
}
