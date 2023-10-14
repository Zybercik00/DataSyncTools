package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.JavaClass;
import org.apache.velocity.VelocityContext;

public interface VelocityContextFactory {
    VelocityContext getVelocityContext(JavaClass aClass);
}
