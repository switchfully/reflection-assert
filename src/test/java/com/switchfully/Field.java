package com.switchfully;

public record Field(String name, Class type) {

    public static Field field(String name, Class type) {
        return new Field(name, type);
    }
}
