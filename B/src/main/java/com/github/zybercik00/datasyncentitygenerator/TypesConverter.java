package com.github.zybercik00.datasyncentitygenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;

public class TypesConverter {

    public static String convertTypesToImports(String value) {
        switch (value) {
            case "2":
                return "import java.math.BigDecimal;\n ";
            case "93":
                return "import java.security.Timestamp;\n ";
            case "-5":
                return "\n ";
        }
        return value;
    }

    public static Class<?> convertTypes(int sqlType) {
        return switch (sqlType) {
            case Types.BIGINT -> Long.class;
            case Types.TIMESTAMP -> Timestamp.class;
            case Types.NUMERIC -> BigDecimal.class;
//            case "12" -> "String";
//            case "1" -> "String";
//            case "4" -> "int";
//            case "8" -> "double";
//            case "91" -> "Date";
//            case "92" -> "Time";
//            case "2003" -> "Array ";
//            case "2000" -> "Object";
//            case "70" -> "URL";
//            case "2009" -> "String";
            default -> throw new IllegalArgumentException("Unexpected type: " + sqlType);
        };
    }
}
