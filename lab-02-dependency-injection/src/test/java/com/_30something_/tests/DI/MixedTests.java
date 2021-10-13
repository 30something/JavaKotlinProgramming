package com._30something_.tests.DI;

import com._30something.DI.ClassRegistrationException;
import com._30something.DI.InterfaceRegistrationException;
import java.lang.reflect.InvocationTargetException;

import com._30something.DI.DI;
import org.junit.jupiter.api.Test;

public class MixedTests {
    @Test
    public void mixedTestsFirst() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();

    }

    @Test
    public void mixedTestsSecond() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();

    }
}
