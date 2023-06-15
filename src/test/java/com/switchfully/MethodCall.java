package com.switchfully;

import com.google.common.primitives.Primitives;

import java.util.Arrays;

public record MethodCall(String name, Object... parameterValues) {

    public static MethodCall method(String name, Object... parameterValues) {
        return new MethodCall(name, parameterValues);
    }

    public Class[] parameterTypes() {
        return Arrays.stream(parameterValues()).map(t -> Primitives.unwrap(t.getClass())).toArray(Class[]::new);
    }
}
