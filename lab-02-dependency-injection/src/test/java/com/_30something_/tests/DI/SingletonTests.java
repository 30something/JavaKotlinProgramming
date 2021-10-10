package com._30something_.tests.DI;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class Factory {
    public Car car;
    public Plane plane;
    public Train train;
    public final Factory instance = new Factory(new Car(), new Train(), new Plane());

    @Inject
    public Factory(Car car, Train train, Plane plane) {
        this.car = car;
        this.train = train;
        this.plane = plane;
    }
}

public class SingletonTests {

}
