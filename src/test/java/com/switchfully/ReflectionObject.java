package com.switchfully;

import org.assertj.core.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class ReflectionObject {

    private final Class aClass;
    private final Object object;

    ReflectionObject(Class aClass, Object object) {
        this.aClass = aClass;
        this.object = object;
    }

    public ReflectionMethodCall<ReflectionObject> callMethod(String methodName, Object... parameterValues) {

        MethodResult methodResult = OutputCaptor.captureOutput(() -> callMethodAndSaveResult(MethodCall.method(methodName, parameterValues)));
        ReflectionMethodCall<ReflectionObject> result = new ReflectionMethodCall<>(this, methodResult.returnValue(), methodResult.printedText());
        return result;
    }

    public ReflectionField expectField(String name) {
        return new ReflectionField(this, name);
    }

    public Object retrieveObject() {
        return object;
    }

    private <TYPE> void validateFieldsHaveCorrectValue(String name, Predicate<TYPE> predicate) {
        Assertions.assertThatCode(() -> aClass.getDeclaredField(name))
                .as("Expecting field " + name + " to exist.")
                .doesNotThrowAnyException();
        try {
            java.lang.reflect.Field declaredField = aClass.getDeclaredField(name);
            declaredField.setAccessible(true);
            Assertions.assertThat(predicate.test((TYPE) declaredField.get(object))).as("Field " + name + " needs to have the correct value").isTrue();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Should not happen " + e);
        }
    }

    private void validateFieldsHaveCorrectValue(FieldCheck... fieldValuesToCheck) {
        for (FieldCheck field : fieldValuesToCheck) {
            Assertions.assertThatCode(() -> aClass.getDeclaredField(field.name()))
                    .as("Expecting field " + field.name() + " to exist.")
                    .doesNotThrowAnyException();
            try {
                java.lang.reflect.Field declaredField = aClass.getDeclaredField(field.name());
                declaredField.setAccessible(true);
                Assertions.assertThat(declaredField.get(object)).as("Field " + field.name() + " needs to have the correct value").isEqualTo(field.value());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Should not happen " + e);
            }
        }
    }

    private Object callMethodAndSaveResult(MethodCall methodCall) {
        Assertions.assertThatCode(() -> aClass.getMethod(methodCall.name(), methodCall.parameterTypes()))
                .as("Expecting method " + methodCall.name() + " to exist.")
                .doesNotThrowAnyException();
        try {
            Method aMethod = aClass.getMethod(methodCall.name(), methodCall.parameterTypes());
            return aMethod.invoke(object, methodCall.parameterValues());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    public class ReflectionField {

        private final ReflectionObject reflectionObject;
        private final String name;

        public ReflectionField(ReflectionObject reflectionObject, String name) {
            this.reflectionObject = reflectionObject;
            this.name = name;
        }

        public ReflectionObject toHaveValue(Object value) {
            validateFieldsHaveCorrectValue(FieldCheck.fieldCheck(name, value));
            return reflectionObject;
        }

        public <TYPE> ReflectionObject toFollowConstraint(Predicate<TYPE> predicate) {
            validateFieldsHaveCorrectValue(name, predicate);
            return reflectionObject;
        }
    }
}
