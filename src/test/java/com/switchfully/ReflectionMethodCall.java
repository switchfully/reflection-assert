package com.switchfully;

import org.assertj.core.api.Assertions;

import java.util.function.Predicate;

public class ReflectionMethodCall<RETURN> {
    private final RETURN reflectionObjectToReturnTo;
    private final Object returnValue;
    private String printedText;

    ReflectionMethodCall(RETURN reflectionObjectToReturnTo, Object returnValue, String printedText) {
        this.reflectionObjectToReturnTo = reflectionObjectToReturnTo;
        this.returnValue = returnValue;
        this.printedText = printedText;
    }

    public ReflectionMethodCall<RETURN> expectReturnToBe(Object expectedReturnValue) {
        validateCorrectReturnValue(expectedReturnValue);
        return this;
    }

    public ReflectionMethodCall<RETURN> expectToBePrinted(String expectedPrintedText) {
        Assertions.assertThat(printedText).isEqualToNormalizingNewlines(expectedPrintedText);
        return this;
    }

    public <TYPE> ReflectionMethodCall<RETURN> expectResultToFollowConstraint(Predicate<TYPE> predicate) {
        Assertions.assertThat(predicate.test((TYPE) returnValue)).as("Expecting \"" + returnValue + "\" to follow the given constraint").isTrue();
        return this;
    }

    public Object retrieveResult() {
        return returnValue;
    }

    public RETURN and() {
        return reflectionObjectToReturnTo;
    }

    private void validateCorrectReturnValue(Object expectedReturnValue) {
        Assertions.assertThat(returnValue).isEqualTo(expectedReturnValue);
    }

}
