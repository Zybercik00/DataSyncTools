package com.github.zybercik00.datasyncentitygenerator;

public class CaseConverter {

    public static String toCamelCase(String in) {
        StringBuilder targetTxt = new StringBuilder();
        boolean first = false;
        boolean afterUnderscore = false;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if ((first || afterUnderscore) && Character.isAlphabetic(c)) {
                targetTxt.append(Character.toUpperCase(c));
                first = false;
                afterUnderscore = false;
            } else if (c == '_') {
                afterUnderscore = true;
                first = true;
            } else if (Character.isAlphabetic(c)) {
                targetTxt.append(Character.toLowerCase(c));
            }
        }
        return targetTxt.toString();
    }
    public static String toPascalCase(String in) {
        StringBuilder targetTxt = new StringBuilder();
        boolean first = true;
        boolean afterUnderscore = false;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if ((first || afterUnderscore) && Character.isAlphabetic(c)) {
                targetTxt.append(Character.toUpperCase(c));
                first = false;
                afterUnderscore = false;
            } else if (c == '_') {
                afterUnderscore = true;
            } else if (Character.isAlphabetic(c)) {
                targetTxt.append(Character.toLowerCase(c));
            }
        }
        return targetTxt.toString();
    }
}
