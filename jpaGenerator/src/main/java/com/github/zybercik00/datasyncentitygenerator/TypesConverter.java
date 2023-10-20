package com.github.zybercik00.datasyncentitygenerator;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

@Log4j2
public class TypesConverter {


    public static Class<?> convertTypes(int sqlType) {
        return switch (sqlType) {
            case Types.BIGINT -> Long.class;
            case Types.TIMESTAMP -> Timestamp.class;
            case Types.NUMERIC -> BigDecimal.class;
            case Types.VARCHAR -> String.class;
            case Types.CHAR -> String.class;
            case Types.INTEGER -> Integer.class;
            case Types.DOUBLE -> Double.class;
            case Types.DATE -> Date.class;
            case Types.TIME -> Time.class;
            case Types.ARRAY -> Array.class;
            case Types.JAVA_OBJECT -> Object.class;
            case Types.DATALINK -> URL.class;
            case Types.SQLXML -> String.class;

            default -> throw new IllegalArgumentException("Unexpected type: " + sqlType);
        };
    }
}
