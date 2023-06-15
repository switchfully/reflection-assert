package com.switchfully;

public record FieldCheck(String name, Object value) {

    public static FieldCheck fieldCheck(String name, Object value) {
        return new FieldCheck(name, value);
    }
}
