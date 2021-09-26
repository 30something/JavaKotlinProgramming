## Вариантность обобщённых типов.

Обобщённый тип `Generics<T>` называется
1. **ковариантным по `T`**, если из "`Derived` является подтипом `Base`" следует, 
   что `Generic<Derived>` является подтипом `Generic<Base>`.
2. **контравариантными по `T`**, если из "`Derived` является подтипом `Base`" следует,
   что `Generic<Base>` является подтипом `Generic<Derived>`.
3. **инвариантным по `T`**, если `Generic<Derived>` и `Generic<Base>` не состоят ни в каком родстве.

## Вариантность в Java

- Java обобщённые типы по-умолчанию _инвариантны_ по всем своим параметрам типа. Но в Java есть механизм обозначения
вариантности по месту использования (use-site variance). 
Рассмотрим пример:
```java
interface Holder<T> {
    void consume(T object);
    T produce();
}

// ...

void useHolderValue(Holder<? extends Number> producer) { // (1)
    producer.produce(); // ok
    // producer.consume(228) compiler error - "consumer case"
}
void populateHolder(Holder<? super Number> consumer) {   // (2)
    consumer.consume(2); // ok
    consumer.consume(4L); // ok
    consumer.consume(1.0); // ok
    // Number n = consumer.produce(); // compiler error - "producer case"
}

void randomlyUseHolder(Holder<Number> holder) {          // (3)
   consumer.consume(2); // ok
   consumer.consume(4L); // ok
   consumer.consume(1.0); // ok
   Number n = consumer.produce(); // ok
}

// ...

void demo() {
    Holder<Integer> intHolder = new HolderImpl<>();
    useHolderValue(intHolder); // ok - covariant, Integer extends Number => Holder<Integer> 'extends' Holder<Number>
    // populateHolder(intHolder); // fail, accept only Holder<? super Number>, Integer isn't a superclass of Number.
    // randomlyUseHolder(intHolder); // fail, invariant parameter, accepts only exactly Holder<Number>.
    
    Holder<Number> numberHolder = new HolderImpl<>();
    useHolderValue(intHolder); // ok - exact type
    populateHolder(intHolder); // ok - exact type
    randomlyUseHolder(intHolder); // ok - exact type
    
    Holder<Serializable> serializableHolder = new HolderImpl<>();
    // useHolderValue(intHolder); // fail - exact type
    populateHolder(intHolder); // ok - contravariant, Serializable is a supertype of Number.
    // randomlyUseHolder(intHolder); // fail, invariant parameter, accepts only exactly Holder<Number>.
}
```
1. `Holder<? extends Number>` тип - ковариантен по T - Holder<Integer> подойдёт как Holder<Number>. 
Для таких объектов компилятором разрешено только читать из объекта. 
2. `Holder<? super Number` тип - контравариантен по T - Holder<Serializable> подойдет как Holder<Number>.
Для таких объектов разрешено только записывать в объект.
3. `Holder<Number>` тип - инвариантен по T. Подходит только точно Holder<Number>.
Разрешено, очевидно, всё.

Существует мнемоника **PECS** - **P**roducer - **E**xtends, **C**onsumer - **S**uper.
- Если из объекта будут только читать - producer - можно использовать ковариантность (? extends ..)
- Если в объект будут только писать - consumer - можно использовать контравариантность (? super ..)
- Если нужно и то, и другое (по-умолчанию) - тогда тип инвариантен (собственно, по-умолчанию) 

_Заметка._ встроенные Массивы в Java всегда _ковариантны_ по типу элементов.
Это в общем случае ошибочно, так что нужно быть аккуратным при
передаче массивов в методы, которые могут модифицировать массив-параметр.