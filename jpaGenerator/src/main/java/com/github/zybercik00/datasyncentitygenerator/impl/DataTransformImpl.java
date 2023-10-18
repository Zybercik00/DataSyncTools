package com.github.zybercik00.datasyncentitygenerator.impl;

import com.github.zybercik00.datasyncentitygenerator.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataTransformImpl implements DataTransform {
    private  final String targetPackage = "v.targetPackage";

     @Override
     public List<JavaClass> transformTablesToJavaSources(List<JdbcTable> tables) {
        List<JavaClass> classes = new ArrayList<>();
        for (JdbcTable table : tables) {
            JavaClass javaClass = transformTableToJavaSource(table);
            classes.add(javaClass);
        }
        return classes;
    }

    private  JavaClass transformTableToJavaSource(JdbcTable table) {
        JavaClass javaClass = generateJavaClass(table);
        generateClassFields(table, javaClass);
        calculateImports(javaClass);
        return javaClass;
    }

    private  void generateClassFields(JdbcTable table, JavaClass javaClass) {
        ArrayList<JavaField> fields = generateClassFields(table);
        javaClass.setFields(fields);
    }

    private  ArrayList<JavaField> generateClassFields(JdbcTable table) {
        ArrayList<JavaField> fields = new ArrayList<>();
        for (JdbcColumn column : table.getColumns()) {
            JavaField javaField = generateJavaField(column);
            fields.add(javaField);
        }
        return fields;
    }

    private  JavaClass generateJavaClass(JdbcTable table) {
        JavaClass javaClass = new JavaClass();
        javaClass.setPackageName(targetPackage);
        String entityName = CaseConverter.toPascalCase(table.getName());
        javaClass.setName(entityName);
        return javaClass;
    }

    private  JavaField generateJavaField(JdbcColumn column) {

         JavaType javaType = new JavaType(TypesConverter.convertTypes(column.getType()));

         String fieldName = CaseConverter.toCamelCase(column.getName());
         JavaAnnotation annotation = new JavaAnnotation();

        return new JavaField(javaType, fieldName, (List<JavaAnnotation>) annotation);
    }

    private  void calculateImports(JavaClass javaClass) {
        Set<String> imports = new LinkedHashSet<>();
        for (JavaField javaField : javaClass.getFields()) {
            JavaType fieldType = javaField.getType();
            imports.add((fieldType.getType()).getName());
        }
        javaClass.setImports(imports);
    }
}
