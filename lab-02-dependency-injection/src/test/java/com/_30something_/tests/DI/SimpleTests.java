package com._30something_.tests.DI;

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

public class SimpleTests {
    @Test
    public void testSimpleFirst() {
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
    }

    @Test
    public void testSimpleSecond() {

    }
}
