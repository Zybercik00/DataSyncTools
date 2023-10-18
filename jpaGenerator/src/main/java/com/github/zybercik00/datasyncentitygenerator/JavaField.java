package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class JavaField {

    private final JavaType type;
    private final String name;
    private final List<JavaAnnotation> annotationList;
}
