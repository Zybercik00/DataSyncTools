package com.github.zybercik00.datasyncentitygenerator.impl;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

public class TemplateLoaderImpl implements  TemplateLoader{
    private final VelocityEngine velocityEngine;
    private  final String velocityTemplate = "v.template";

    public TemplateLoaderImpl(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    @Override
    public Template getTemplate() {
        return velocityEngine.getTemplate(velocityTemplate);
    }
}
