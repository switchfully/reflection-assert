package com.switchfully;

import org.assertj.core.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.util.Lists.newArrayList;

public class ReflectionClass {
    private final Class aClass;

    ReflectionClass(Class aClass) {
        this.aClass = aClass;
    }

    public ReflectionObject callConstructor(Object... constructorValues) {
        return new ReflectionObject(aClass, callConstructor(new ConstructorCall(constructorValues)));
    }

    public ReflectionClass expectFields(Field... fields) {
        validateCorrectDefinedFields(newArrayList(PRIVATE, PRIVATE | FINAL), fields);
        return this;
    }

    public void expectConstants(Field... fields) {
        validateCorrectDefinedFields(newArrayList(PUBLIC | STATIC | FINAL, PRIVATE | STATIC | FINAL), fields);
    }

    public void expectEnumConstants(String... constants) {
        Assertions.assertThat(retrieveEnumConstantsAsStrings()).containsExactlyInAnyOrder(constants);
    }

    public ReflectionMethodCall<ReflectionClass> callStaticMethod(String methodName, Object... parameterValues) {
        MethodResult methodResult = OutputCaptor.captureOutput(() -> callStaticMethodAndSaveResult(MethodCall.method(methodName, parameterValues)));
        return new ReflectionMethodCall<>(this, methodResult.returnValue(), methodResult.printedText());
    }

    public Object getEnumValue(String enumName) {
        return callStaticMethod("valueOf", enumName).retrieveResult();
    }

    public Class retrieveClass() {
        return aClass;
    }

    private Object callStaticMethodAndSaveResult(MethodCall methodCall) {
        Assertions.assertThatCode(() -> aClass.getMethod(methodCall.name(), methodCall.parameterTypes()))
                .as("Expecting method " + methodCall.name() + " to exist.")
                .doesNotThrowAnyException();
        try {
            Method aMethod = aClass.getMethod(methodCall.name(), methodCall.parameterTypes());
            return aMethod.invoke(null, methodCall.parameterValues());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    private List<Object> retrieveEnumConstantsAsStrings() {
        return Arrays.stream(aClass.getEnumConstants()).map(enumConstant -> {
            try {
                return aClass.getMethod("name").invoke(enumConstant);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    private Object callConstructor(ConstructorCall constructorCall) {
        Assertions.assertThatCode(() -> aClass.getConstructor(constructorCall.constructorTypes()))
                .as("Expecting a constructor with following types " + Arrays.toString(constructorCall.constructorTypes()) + " to exist")
                .doesNotThrowAnyException();
        try {
            return aClass.getConstructor(constructorCall.constructorTypes()).newInstance(constructorCall.constructorValues());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Should not happen", e);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        }
    }


    private void validateCorrectDefinedFields(List<Integer> modifiers, Field... fields) {
        for (Field field : fields) {
            assertThatCode(() -> aClass.getDeclaredField(field.name())).as("Expecting field " + field.name() + " to exist.").doesNotThrowAnyException();
            try {
                java.lang.reflect.Field declaredField = aClass.getDeclaredField(field.name());
                Assertions.assertThat(declaredField.getType()).as("Expecting field " + field.name() + " to have type " + field.type()).isEqualTo(field.type());
                Assertions.assertThat(declaredField.getModifiers()).as("Expecting field " + field.name() + " to be private.").isIn(modifiers);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Should not happen", e);
            }
        }
    }

}
