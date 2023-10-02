package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor

public class JavaField {
    private final Class<?> type;// TODO Use custom type JavaType
    private final String name;

    @Override
    public String toString() {
        return
                type.getSimpleName() + " " + name;
    }
}
