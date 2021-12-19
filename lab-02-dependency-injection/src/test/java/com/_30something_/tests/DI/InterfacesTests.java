package com._30something_.tests.DI;

import com._30something.DI.ClassRegistrationException;
import com._30something.DI.InterfaceRegistrationException;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlException;

import com._30something.DI.DI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;

interface Graph {
    Integer getSize();
    void add();
}

interface Fake {
    Integer getSize();
    void add();
}

class Tree implements Graph {
    @Inject
    public Integer size = 0;
    @Inject
    public String info;

    @Inject
    public Tree() {
        info = "Tree";
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void add() {
        size++;
    }
}

class SubTree {
    public String newInfo;

    @Inject
    public SubTree(Tree parentTree) {
        newInfo = parentTree.toString();
    }
}

class Path implements Graph {
    @Inject
    public Integer size = 0;
    @Inject
    public String info;

    @Inject
    public Path() {
        info = "Path";
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void add() {
        size++;
    }
}

class Cactus implements Graph {
    @Inject
    public Integer size = 0;
    @Inject
    public String info;

    public Cactus() {
        info = "Cactus";
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void add() {
        size++;
    }
}

class Node {
    @Inject
    public Integer data;

    @Inject
    public Node() {
        data = 42;
    }
}

public class InterfacesTests {
    @Test
    public void interfacesTestsMain() throws ClassRegistrationException, InterfaceRegistrationException,
            ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DI interfacesDi = new DI();
        Assertions.assertThrows(InterfaceRegistrationException.class, () -> interfacesDi.registerClass(Graph.class));
        Assertions.assertThrows(InterfaceRegistrationException.class, () -> interfacesDi.registerClass(Fake.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Graph.class, Graph.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Tree.class, Path.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Tree.class, Graph.class));
        Assertions.assertThrows(ClassRegistrationException.class,
                () -> interfacesDi.registerClass(Graph.class, Cactus.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Graph.class, Fake.class));
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Fake.class, Tree.class));
        interfacesDi.registerClass(Tree.class);
        interfacesDi.registerClass(Graph.class, Tree.class);
        Assertions.assertThrows(InterfaceRegistrationException.class,
                () -> interfacesDi.registerClass(Graph.class, Path.class));
        interfacesDi.registerClass(Path.class);
        Assertions.assertThrows(AccessControlException.class, () -> interfacesDi.resolveClass(Tree.class));
        Assertions.assertThrows(AccessControlException.class, () -> interfacesDi.resolveClass(Graph.class));
        Assertions.assertThrows(AccessControlException.class, () -> interfacesDi.resolveClass(Path.class));
        interfacesDi.registerClass(Node.class);
        interfacesDi.registerClass(SubTree.class);
        interfacesDi.completeRegistration();
        Assertions.assertThrows(ClassNotFoundException.class, () -> interfacesDi.resolveClass(Fake.class));
        Assertions.assertDoesNotThrow(() -> {
            Tree myTree = interfacesDi.resolveClass(Tree.class);
            Tree newTree = (Tree) interfacesDi.resolveClass(Graph.class);
            Assertions.assertNotNull(myTree);
            Assertions.assertNotNull(newTree);
            Assertions.assertEquals(myTree.getClass(), Tree.class);
            Assertions.assertEquals(newTree.getClass(), Tree.class);
            Assertions.assertEquals(myTree.size, 0);
            Assertions.assertEquals(myTree.info, "Tree");
            Assertions.assertEquals(newTree.size, 0);
            Assertions.assertEquals(newTree.info, "Tree");
        });
        Assertions.assertThrows(AccessControlException.class, () -> interfacesDi.registerClass(Cactus.class));
        Assertions.assertThrows(ClassNotFoundException.class, () -> interfacesDi.resolveClass(Cactus.class));
        Assertions.assertEquals(interfacesDi.resolveClass(Node.class).data, 42);
        SubTree mySubtree = interfacesDi.resolveClass(SubTree.class);
        Assertions.assertNotNull(mySubtree);
    }
}
