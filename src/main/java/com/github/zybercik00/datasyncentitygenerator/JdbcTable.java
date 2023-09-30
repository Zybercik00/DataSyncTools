package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class JdbcTable {
    private String name;
    private List<JdbcColumn> columns;

}
