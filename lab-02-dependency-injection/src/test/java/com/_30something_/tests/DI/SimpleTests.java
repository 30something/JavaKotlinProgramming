package com._30something_.tests.DI;

import com._30something.DI.ClassRegistrationException;
import com._30something.DI.InterfaceRegistrationException;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlException;

import com._30something.DI.DI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;

class Car {
    public final int speed;

    @Inject
    public Car() {
        this.speed = 0;
    }

    public String start() {
        return "Car makes beep";
    }
}

class Plane {
    public int getWeight() {
        return weight;
    }

    public final int weight;

    @Inject
    public Plane() {
        this.weight = 42;
    }
}

class Train {
    public final int weight = 10;
    public final int height;

    @Inject
    public Train() {
        this.height = 15;
    }
}

class Bicycle {
    public Bicycle() {}
}

class Bicycle1 {
    @Inject
    private Bicycle1() {}
}

class Bicycle2 {
    private final Car supportCar;

    @Inject
    public Bicycle2(Car supportCar) {
        this.supportCar = supportCar;
    }

    public Car getSupportCar() {
        return supportCar;
    }
}

class Bicycle3 {
    public final String type;
    public final int model;

    @Inject
    public Bicycle3(int model) {
        this.model = model;
        type = "Standard";
    }

    @Inject
    public Bicycle3(String type, int model) {
        this.type = type;
        this.model = model;
    }
}

class Bicycle4 extends Bicycle2 {
    @Inject
    public Bicycle4(Car supportCar) {
        super(supportCar);
    }
}

class Bicycle5 {
    @Inject
    public Integer gearsNumber;

    @Inject
    public Bicycle5() {
        gearsNumber = 10;
    }
}

class Bicycle6 {
    @Inject
    public Bicycle6() {
        System.out.println("Bicycle no. 6 created ^_^");
    }
}

class BicyclesCollection {
    public Bicycle6 bicycle6;
    public Bicycle5 bicycle5;
    public Bicycle4 bicycle4;
    public Bicycle2 bicycle2;

    @Inject
    public BicyclesCollection(Bicycle6 bicycle6, Bicycle5 bicycle5, Bicycle4 bicycle4, Bicycle2 bicycle2) {
        this.bicycle6 = bicycle6;
        this.bicycle5 = bicycle5;
        this.bicycle4 = bicycle4;
        this.bicycle2 = bicycle2;
    }
}

public class SimpleTests {
    @Test
    public void testSimpleFirst() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();
        myDi.registerClass(Car.class);
        myDi.registerClass(Plane.class);
        myDi.registerClass(Train.class);
        myDi.completeRegistration();
        Car myCar = myDi.resolveClass(Car.class);
        Plane myPlane = myDi.resolveClass(Plane.class);
        Train myTrain = myDi.resolveClass(Train.class);
        Assertions.assertNotNull(myCar);
        Assertions.assertNotNull(myPlane);
        Assertions.assertNotNull(myTrain);
        Assertions.assertEquals(myCar.start(), "Car makes beep");
        Assertions.assertEquals(myPlane.getWeight(), 42);
        Assertions.assertEquals(myTrain.weight, 10);
    }

    @Test
    public void testSimpleSecond() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI myDi = new DI();
        myDi.registerClass(Car.class);
        Car newCar = new Car();
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Car.class));
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Bicycle.class));
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Bicycle1.class));
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Bicycle3.class));
        Bicycle2 myBicycle = new Bicycle2(newCar);
        Bicycle3 myStandardBicycle = new Bicycle3(42);
        Bicycle3 myNewStandardBicycle = new Bicycle3("Cool", 42);
        myDi.registerClass(Bicycle2.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> myDi.registerClass(Bicycle3.class));
        Assertions.assertThrows(AccessControlException.class, () -> myDi.resolveClass(Car.class));
        myDi.completeRegistration();
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Car.class));
        Assertions.assertThrows(AccessControlException.class, () -> myDi.registerClass(Plane.class));
        Assertions.assertThrows(ClassNotFoundException.class, () -> myDi.resolveClass(Plane.class));
        Assertions.assertThrows(ClassNotFoundException.class, () -> myDi.resolveClass(Train.class));
        Bicycle2 newMyBicycle = myDi.resolveClass(Bicycle2.class);
        Assertions.assertNotNull(newMyBicycle);
        Assertions.assertEquals(myBicycle.getSupportCar(), newCar);
        Assertions.assertNotNull(newMyBicycle.getSupportCar());
        Assertions.assertEquals(myStandardBicycle.model, myNewStandardBicycle.model);
        Assertions.assertThrows(ClassNotFoundException.class, () -> myDi.resolveClass(Bicycle3.class));
        Assertions.assertEquals(myStandardBicycle.model, myNewStandardBicycle.model);
    }

    @Test
    public void testSimpleThird() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI firstDi = new DI();
        firstDi.registerClass(Bicycle2.class);
        firstDi.registerClass(Plane.class);
        firstDi.registerClass(Train.class);
        Assertions.assertThrows(ClassRegistrationException.class, firstDi::completeRegistration);
        Assertions.assertThrows(AccessControlException.class, () -> firstDi.resolveClass(Car.class));
        Assertions.assertThrows(AccessControlException.class, () -> firstDi.resolveClass(Bicycle2.class));
        Assertions.assertThrows(ClassRegistrationException.class, () -> firstDi.registerClass(Train.class));
        Assertions.assertThrows(ClassRegistrationException.class, () -> firstDi.registerClass(Plane.class));
        firstDi.registerClass(Car.class);
        firstDi.completeRegistration();
        Assertions.assertThrows(AccessControlException.class, () -> firstDi.registerClass(Car.class));
        Assertions.assertThrows(AccessControlException.class, () -> firstDi.registerClass(Bicycle.class));
        Assertions.assertThrows(ClassNotFoundException.class, () -> firstDi.resolveClass(Bicycle.class));
        Assertions.assertThrows(AccessControlException.class, () -> firstDi.registerClass(Train.class));
        Bicycle2 newBicycle = new Bicycle2(firstDi.resolveClass(Car.class));
        Assertions.assertNotNull(newBicycle);
        Assertions.assertEquals(newBicycle.getSupportCar().getClass(), Car.class);
        DI secondDi = new DI();
        secondDi.registerClass(Bicycle4.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> secondDi.registerClass(Bicycle4.class));
        Assertions.assertThrows(AccessControlException.class, () -> secondDi.resolveClass(Bicycle4.class));
        secondDi.registerClass(BicyclesCollection.class);
        Assertions.assertThrows(ClassRegistrationException.class, secondDi::completeRegistration);
        secondDi.registerClass(Bicycle6.class);
        Assertions.assertThrows(ClassRegistrationException.class, secondDi::completeRegistration);
        secondDi.registerClass(Bicycle5.class);
        Assertions.assertThrows(ClassRegistrationException.class, secondDi::completeRegistration);
        secondDi.registerClass(Bicycle2.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> secondDi.registerClass(Bicycle4.class));
        Assertions.assertThrows(ClassRegistrationException.class, secondDi::completeRegistration);
        secondDi.registerClass(Car.class);
        secondDi.completeRegistration();
        BicyclesCollection collection = secondDi.resolveClass(BicyclesCollection.class);
        Assertions.assertNotNull(collection);
        collection.bicycle2 = secondDi.resolveClass(Bicycle2.class);
        Assertions.assertNotNull(collection.bicycle2);
        Assertions.assertEquals(collection.bicycle2.getSupportCar().speed, 0);
        collection.bicycle6 = secondDi.resolveClass(Bicycle6.class);
        Assertions.assertThrows(ClassNotFoundException.class, () -> secondDi.resolveClass(Bicycle3.class));
        collection.bicycle5 = secondDi.resolveClass(Bicycle5.class);
        Assertions.assertEquals(collection.bicycle5.gearsNumber, 10);
    }
}
