package com._30something_.tests.DI;

import com._30something.DI.ClassRegistrationException;
import com._30something.DI.InterfaceRegistrationException;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlException;

import com._30something.DI.DI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import javax.inject.Singleton;

class Class1 {
    public Class2 class2;
    public String info;

    @Inject
    public Class1(Class2 class2) {
        this.class2 = class2;
        info = "Hello, I'm class1 :)";
    }
}

interface Class2 {
    String getInfo();
}

class Class3 implements Class2 {
    @Inject
    public Double specialImportantInfo;

    @Inject
    public Class3() {
        specialImportantInfo = 42.422442;
    }

    @Override
    public String getInfo() {
        return "Hello, I'm class3 :)";
    }
}

class Class4 {
    public Class1 class1;
    public Class5 class5;

    @Inject
    public Class4() {
        class1 = new Class1(new Class3());
        class5 = Class5.instance;
    }
}

@Singleton
class Class5 {
    public static final Class5 instance = new Class5();

    @Inject
    private Class5() {}

    public Class5 getInstance() {
        return instance;
    }
}

public class MixedTests {
    @Test
    public void mixedTestsFirst() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Cactus.class));
        myDi.registerClass(Bicycle2.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Bicycle.class));
        Assertions.assertThrows(InterfaceRegistrationException.class, () -> myDi.registerClass(Graph.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> myDi.registerClass(Graph.class, Factory.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> myDi.registerClass(Graph.class, Graph.class));
        myDi.registerClass(Graph.class, Tree.class);
        Assertions.assertThrows(ClassRegistrationException.class, myDi::completeRegistration);
        myDi.registerClass(Car.class);
        myDi.registerClass(Plane.class);
        Assertions.assertThrows(AccessControlException.class, () -> myDi.resolveClass(Graph.class));
        myDi.registerClass(Factory.class);
        Assertions.assertThrows(AccessControlException.class, () -> myDi.resolveClass(Tree.class));
        Assertions.assertThrows(ClassRegistrationException.class, myDi::completeRegistration);
        myDi.registerClass(Train.class);
        myDi.registerClass(Bus.class);
        myDi.registerClass(Bicycle5.class);
        myDi.completeRegistration();
        Assertions.assertDoesNotThrow(() -> {
            myDi.completeRegistration();
            myDi.completeRegistration();
            Graph graphRealization = myDi.resolveClass(Graph.class);
            Tree currentTree = myDi.resolveClass(Tree.class);
            Assertions.assertEquals(graphRealization.getClass(), Tree.class);
            Assertions.assertNotNull(graphRealization);
            Assertions.assertNotNull(currentTree);
        });
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Bicycle4.class));
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Bicycle4.class));
        Factory factory = myDi.resolveClass(Factory.class);
        Bus bus = myDi.resolveClass(Bus.class);
        Assertions.assertEquals(bus, factory.getBus());
        Car car = myDi.resolveClass(Car.class);
        Assertions.assertNotEquals(car, myDi.resolveClass(Car.class));
        Assertions.assertNotEquals(car, factory.getCar());
        Assertions.assertNotEquals(car, myDi.resolveClass(Factory.class).getCar());
    }

    @Test
    public void mixedTestsSecond() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();
        myDi.registerClass(Class4.class);
        myDi.registerClass(Class1.class);
        Assertions.assertThrows(ClassRegistrationException.class, myDi::completeRegistration);
        myDi.registerClass(Class2.class, Class3.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Class3.class));
        myDi.registerClass(Class5.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Class5.class));
        myDi.completeRegistration();
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Class1.class));
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Class2.class, Class3.class));
        Class1 class1 = myDi.resolveClass(Class1.class);
        Class2 class2 = myDi.resolveClass(Class2.class);
        Class3 class3 = myDi.resolveClass(Class3.class);
        Class4 class4 = myDi.resolveClass(Class4.class);
        Class5 class5 = myDi.resolveClass(Class5.class);
        Assertions.assertNotNull(class1);
        Assertions.assertNotNull(class3);
        Assertions.assertNotNull(class5);
        Assertions.assertEquals(class5, myDi.resolveClass(Class5.class));
        Assertions.assertNotEquals(class2, class3);
        Assertions.assertEquals(class2.getInfo(), "Hello, I'm class3 :)");
        Assertions.assertNotEquals(class2, myDi.resolveClass(Class2.class));
        Assertions.assertNotEquals(class4.class5, class5);
        Assertions.assertEquals(class4.class5, myDi.resolveClass(Class4.class).class5);
        Assertions.assertEquals(class4.class1.info, "Hello, I'm class1 :)");
        Assertions.assertEquals(class5.getInstance(), class4.class5);
    }
}
