package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;

import java.io.IOException;
import java.util.List;

public interface DataSaver {
    void emitJavaSources(List<JavaClass> classes) throws IOException;
}
