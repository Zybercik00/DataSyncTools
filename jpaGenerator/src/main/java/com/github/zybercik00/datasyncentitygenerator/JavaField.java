package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
@RequiredArgsConstructor
@ToString
public class JavaField {

    private final JavaType type;
    private final String name;
    private final List<JavaAnnotation> annotationList;
}
