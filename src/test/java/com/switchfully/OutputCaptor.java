package com.switchfully;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

public class OutputCaptor {

    public static MethodResult captureOutput(Supplier<Object> supplier) {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        Object returnValue = supplier.get();
        String printedText = outContent.toString();
        System.setOut(originalOut);
        return new MethodResult(returnValue, printedText);
    }
}
