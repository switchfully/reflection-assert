package com.switchfully;

import org.assertj.core.api.Assertions;

/*
    Please don't kill me. I wanted to write something pretty, but it was too much work to do so.
    Besides, reflection test are painfully slow to write...
    If you want to replace this with something more pretty, please be my guest.
 */
public class ReflectionTestUtil {

    public static ReflectionClass onClass(String className) {
        return checkClass(className);
    }

    private static ReflectionClass checkClass(String className) {
        Assertions.assertThatCode(() -> Class.forName(className)).as("Expecting class " + className + " to exist.").doesNotThrowAnyException();
        try {
            return new ReflectionClass(Class.forName(className));
        } catch (ClassNotFoundException classNotFoundException) {
            throw new RuntimeException("Should not happen", classNotFoundException);
        }
    }

}
