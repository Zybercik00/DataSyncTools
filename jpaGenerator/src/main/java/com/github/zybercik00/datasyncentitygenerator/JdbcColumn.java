package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class JdbcColumn {
    private final String name;
    private final int type;
}
