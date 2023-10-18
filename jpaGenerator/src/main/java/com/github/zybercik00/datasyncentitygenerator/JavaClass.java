package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

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
