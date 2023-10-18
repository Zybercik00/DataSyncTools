package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Parameter;

@RequiredArgsConstructor
@Getter
@ToString
public class JavaAnnotation {

    private String getter = "@Getter \n";
    private String entity = "@Entity \n";
}
