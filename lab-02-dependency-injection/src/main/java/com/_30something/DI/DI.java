package com._30something.DI;

import java.security.AccessControlException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;

public class DI {
    private boolean registrationCompleted = false;
    private final HashMap<Class<?>, Class<?>> associatedImplementations = new HashMap<>();
    private final HashMap<Class<?>, Constructor<?>> associatedConstructors = new HashMap<>();
    private final HashMap<Class<?>, Object> singletonsInstances = new HashMap<>();

    public void registerClass(Class<?> newClass) throws InterfaceRegistrationException, ClassRegistrationException {
        if (registrationCompleted) {
            throw new AccessControlException("Registration completed for current DI");
        }
        if (newClass.isInterface()) {
            throw new InterfaceRegistrationException("Interface registered without implementation");
        }
        if (associatedConstructors.containsKey(newClass)) {
            throw new ClassRegistrationException("Double class registration");
        }
        List<Constructor<?>> constructors_list = Arrays.stream(newClass.getDeclaredConstructors()).toList();
        int injectedConstructorsCounter = 0;
        Constructor<?> supposedConstructor = null;
        for (Constructor<?> constructor : constructors_list) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                injectedConstructorsCounter++;
                supposedConstructor = constructor;
            }
        }
        if (injectedConstructorsCounter == 0) {
            throw new ClassRegistrationException("Injected constructor of " + newClass + " not found");
        }
        if (injectedConstructorsCounter > 1) {
            throw new ClassRegistrationException("Multiple injected constructors found in " + newClass);
        }
        if (!newClass.isAnnotationPresent(Singleton.class) &&
                !Objects.equals(Modifier.toString(supposedConstructor.getModifiers()), "public")) {
            throw new ClassRegistrationException("Supposed constructor of " + newClass + " must be public only");
        }
        associatedConstructors.put(newClass, supposedConstructor);
    }

    public void registerClass(Class<?> newInterface, Class<?> newImplementation)
            throws InterfaceRegistrationException, ClassRegistrationException {
        if (newImplementation.isInterface()) {
            throw new InterfaceRegistrationException("Attempt to register interface as implementation");
        }
        if (!newInterface.isInterface()) {
            throw new InterfaceRegistrationException("Attempt to register implementation for non-interface class");
        }
        if (associatedImplementations.containsKey(newInterface)) {
            throw new InterfaceRegistrationException("Attempt to register new implementation for interface");
        }
        if (!Arrays.stream(newImplementation.getInterfaces()).toList().contains(newInterface)) {
            throw new InterfaceRegistrationException("Implementation doesn't correspond to interface");
        }
        if (!associatedConstructors.containsKey(newImplementation)) {
            registerClass(newImplementation);
        }
        associatedImplementations.put(newInterface, newImplementation);
    }

    public void completeRegistration() throws ClassRegistrationException {
        for (Constructor<?> constructor : associatedConstructors.values()) {
            for (Parameter parameter : constructor.getParameters()) {
                if (!associatedConstructors.containsKey(parameter.getType()) &&
                        !associatedImplementations.containsKey(parameter.getType())) {
                    throw new ClassRegistrationException(
                            "Arguments of injected constructor " + constructor + " aren't registered");
                }
            }
            if (!constructor.isAnnotationPresent(Inject.class)) {
                throw new ClassRegistrationException("Constructor " + constructor + " must be marked with @Inject");
            }
        }
        registrationCompleted = true;
    }

    public <T> T resolveClass(Class<T> newClass) throws ClassNotFoundException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        if (!registrationCompleted) {
            throw new AccessControlException("Registration isn't completed for current DI");
        }
        if (!associatedConstructors.containsKey(newClass) && !associatedImplementations.containsKey(newClass)) {
            throw new ClassNotFoundException("Requested class not found");
        }
        if (newClass.isInterface()) {
            Class<?> implementation = associatedImplementations.get(newClass);
            return newClass.cast(resolveClass(implementation));
        }
        if (singletonsInstances.containsKey(newClass)) {
            return newClass.cast(singletonsInstances.get(newClass));
        }
        ArrayList<Object> createdInstances = new ArrayList<>();
        Constructor<?> constructor = associatedConstructors.get(newClass);
        for (Parameter parameter : constructor.getParameters()) {
            createdInstances.add(resolveClass(parameter.getType()));
        }
        constructor.setAccessible(true);
        T newInstance = newClass.cast(constructor.newInstance(createdInstances.toArray()));
        if (newClass.isAnnotationPresent(Singleton.class)) {
            singletonsInstances.put(newClass, newInstance);
        }
        return newInstance;
    }
}
