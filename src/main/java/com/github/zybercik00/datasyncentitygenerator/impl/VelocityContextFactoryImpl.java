package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;

import java.util.HashMap;
import java.util.Map;

public class VelocityContextFactoryImpl implements VelocityContextFactory {

    @Override
    public VelocityContext getVelocityContext(JavaClass aClass) {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("date", new DateTool());
        contextMap.put("javaClass", aClass);
        contextMap.put("system", System.getProperties());
        return new VelocityContext(contextMap);
    }
}
