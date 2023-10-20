package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Getter
@ToString
public class JavaType {

    private final Class<?> type;

}
