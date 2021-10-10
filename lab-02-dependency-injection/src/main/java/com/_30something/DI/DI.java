package com._30something.DI;

import javax.inject.Inject;
import java.lang.reflect.*;
import java.util.*;

public class DI {
    private boolean registrationCompleted = false;
    private final HashMap<Class<?>, Class<?>> associatedImplementations = new HashMap<>();
    private final HashMap<Class<?>, Constructor<?>> associatedConstructors = new HashMap<>();

    public void registerClass(Class<?> newClass) {
        try {
            if (registrationCompleted) {
                throw new Exception("Registration completed for current DI");
            }
            if (newClass.isInterface()) {
                throw new Exception("Interface registered without implementation");
            }
            if (associatedConstructors.containsKey(newClass)) {
                throw new Exception("Double class registration");
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
                throw new Exception("Injected constructor of " + newClass + " not found");
            }
            if (injectedConstructorsCounter > 1) {
                throw new Exception("Multiple injected constructors found in " + newClass);
            }
            if (!Objects.equals(Modifier.toString(supposedConstructor.getModifiers()), "public")) {
                throw new Exception("Supposed constructor of " + newClass + " must be public only");
            }
            associatedConstructors.put(newClass, supposedConstructor);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void registerClass(Class<?> newInterface, Class<?> newImplementation) {
        try {
            if (newImplementation.isInterface()) {
                throw new Exception("Attempt to register interface as implementation");
            }
            if (!newInterface.isInterface()) {
                throw new Exception("Attempt to register implementation for non-interface class");
            }
            if (associatedImplementations.containsKey(newInterface)) {
                throw new Exception("Attempt to register new implementation for interface");
            }
            boolean interfaceImplemented = false;
            for (Class<?> currentInterface : Arrays.stream(newImplementation.getInterfaces()).toList()) {
                if (currentInterface == newInterface) {
                    interfaceImplemented = true;
                    break;
                }
            }
            if (!interfaceImplemented) {
                throw new Exception("Implementation doesn't correspond to interface");
            }
            registerClass(newImplementation);
            associatedImplementations.put(newInterface, newImplementation);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void completeRegistration() {
        registrationCompleted = true;
    }

    public <T> T resolveClass(Class<T> newClass) {
        try {
            if (!registrationCompleted) {
                throw new Exception("Registration isn't completed for current DI");
            }
            if (!associatedConstructors.containsKey(newClass) && !associatedImplementations.containsKey(newClass)) {
                throw new Exception("Requested class not found");
            }
            if (newClass.isInterface()) {
                Class<?> implementation = associatedImplementations.get(newClass);
                return newClass.cast(resolveClass(implementation));
            }
            ArrayList<Object> createdInstances = new ArrayList<>();
            Constructor<?> constructor = associatedConstructors.get(newClass);
            for (Parameter parameter : constructor.getParameters()) {
                Class<?> argClass = parameter.getType();
                createdInstances.add(resolveClass(argClass));
            }
            constructor.setAccessible(true);
            return newClass.cast(constructor.newInstance(createdInstances.toArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
