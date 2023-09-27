package com.github.zybercik00.datasyncentitygenerator;

public class Column {
    private String name;
    private String type;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
