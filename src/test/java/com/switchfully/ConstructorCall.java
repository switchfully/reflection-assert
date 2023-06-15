package com.switchfully;

import com.google.common.primitives.Primitives;

import java.util.Arrays;

public record ConstructorCall(Object... constructorValues) {

    public Class[] constructorTypes() {
        return Arrays.stream(constructorValues()).map(t -> Primitives.unwrap(t.getClass())).toArray(Class[]::new);
    }
}
