package com.github.zybercik00.datasyncentitygenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Getter
@Setter
@ToString
public class JdbcTable {
    private String name;
    private List<JdbcColumn> columns;

}
