package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Set;

@Log4j2
@Getter
@Setter
@ToString
public class JavaClass {

    private String packageName;
    private Set<String> imports;
    private List<JavaAnnotation> annotationList;
    private String name;
    private List<JavaField> fields;


}
