package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;
import com.github.zybercik00.datasyncentitygenerator.JdbcTable;

import java.util.List;

public interface DataTransform {
    List<JavaClass> transformTablesToJavaSources(List<JdbcTable> tables);
}
