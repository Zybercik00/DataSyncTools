package com.github.zybercik00.datasyncentitygenerator;

import com.github.zybercik00.datasyncentitygenerator.impl.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Log4j2
@Component
public class DataSyncEntityGeneratorApplication {

    @Autowired
    private ObjectFactory objectFactory;
    public static void main(String[] args) throws Exception {

        var context = new AnnotationConfigApplicationContext();
        context.scan("com.github.zybercik00.datasyncentitygenerator");
        context.refresh();

        var bean = context.getBean(ObjectFactoryImpl.class);
        bean.run(new ObjectFactoryImpl());
//        ObjectFactory objectFactory = new ObjectFactoryImpl();
//        run(objectFactory);
    }

}
