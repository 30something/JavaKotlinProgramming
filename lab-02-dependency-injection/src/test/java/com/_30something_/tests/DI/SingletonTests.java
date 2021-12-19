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

@Singleton
class Factory {
    private final Car car;
    private final Plane plane;
    private final Train train;
    private final Bicycle5 bicycle;
    private final Bus bus;
    public static final Factory instance = new Factory(
            new Car(), new Train(), new Plane(), new Bicycle5(), Bus.instance);

    @Inject
    private Factory(Car car, Train train, Plane plane, Bicycle5 bicycle5, Bus bus) {
        this.car = car;
        this.train = train;
        this.plane = plane;
        this.bicycle = bicycle5;
        this.bus = bus;
    }

    public Car getCar() {
        return car;
    }

    public Bus getBus() {
        return bus;
    }
}

@Singleton
class Bus {
    @Inject
    private Integer capacity;
    public static final Bus instance = new Bus();

    @Inject
    private Bus() {
        this.capacity = 50;
    }
}

public class SingletonTests {
    @Test
    public void singletonTestsMain() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI singletonDI = new DI();
        singletonDI.registerClass(Car.class);
        singletonDI.registerClass(Plane.class);
        singletonDI.registerClass(Train.class);
        singletonDI.registerClass(Bicycle5.class);
        singletonDI.registerClass(Factory.class);
        Assertions.assertThrows(ClassRegistrationException.class, () -> singletonDI.registerClass(Factory.class));
        Assertions.assertThrows(ClassRegistrationException.class, singletonDI::completeRegistration);
        singletonDI.registerClass(Bus.class);
        singletonDI.completeRegistration();
        Assertions.assertThrows(AccessControlException.class, () -> singletonDI.registerClass(Factory.class));
        Car myCar1 = singletonDI.resolveClass(Car.class);
        Car myCar2 = singletonDI.resolveClass(Car.class);
        Assertions.assertNotNull(myCar1);
        Assertions.assertNotNull(myCar2);
        Assertions.assertNotEquals(myCar1, myCar2);
        Factory factory = singletonDI.resolveClass(Factory.class);
        Factory newFactory = singletonDI.resolveClass(Factory.class);
        Assertions.assertNotNull(factory);
        Assertions.assertNotNull(newFactory);
        Assertions.assertNotNull(Factory.instance);
        Assertions.assertEquals(factory, newFactory);
        Car myFactoryCar1 = factory.getCar();
        Car myFactoryCar2 = factory.getCar();
        Car myFactoryCar3 = newFactory.getCar();
        Assertions.assertNotNull(myFactoryCar1);
        Assertions.assertNotNull(myFactoryCar2);
        Assertions.assertNotNull(myFactoryCar3);
        Assertions.assertEquals(myFactoryCar1, myFactoryCar2);
        Assertions.assertEquals(myFactoryCar1, myFactoryCar3);
        Assertions.assertEquals(myFactoryCar2, myFactoryCar2);
        Assertions.assertThrows(AccessControlException.class, () -> singletonDI.registerClass(Bicycle.class));
        Assertions.assertThrows(ClassNotFoundException.class, () -> singletonDI.resolveClass(Bicycle.class));
        Bus bus1 = factory.getBus();
        Bus bus2 = factory.getBus();
        Assertions.assertNotNull(bus1);
        Assertions.assertNotNull(bus2);
        Assertions.assertEquals(bus1, bus2);
        Bus bus3 = singletonDI.resolveClass(Bus.class);
        Bus bus4 = singletonDI.resolveClass(Bus.class);
        Assertions.assertNotNull(bus3);
        Assertions.assertNotNull(bus4);
        Assertions.assertEquals(bus3, bus4);
    }
}
