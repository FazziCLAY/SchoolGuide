package ru.fazziclay.schoolguide.util;

import org.junit.Test;

public class AppTraceTest {
    @Test
    public void test() {
        AppTrace appTrace = new AppTrace("Unit test app trace");
        appTrace.point("test function");
        appTrace.point("test2 function");
        appTrace.point("throwableTest function", new RuntimeException("Stub!"));
        appTrace.point("hello!\nSchoolguide top app!");
        System.out.println("SINGLELINE = \n" + appTrace.getText());
    }

    @Test
    public void testMultiLine() {
        AppTrace appTrace = new AppTrace("Unit test app trace\nhello from\nFazziCLAY");
        appTrace.point("point");
        appTrace.point("throwableTest function", new RuntimeException("Stub!"));
        appTrace.point("hello!\nSchoolguide top app!");
        System.out.println("MILTILINE = \n" + appTrace.getText());
    }
}
