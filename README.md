# java-extensions

This a library of Java utilities to aid programming with lambda just that a little bit more joyful, particularly for 
Java-8 developers but not exclusively. 
### Holder
`Holder` class is a simple container, which is generally useful for mutating values within a lambda expression -- the
holder object is an effectively final object allowing its contents to be mutated.
```
        Holder<Integer> base = Holders.writableHolder(220);
        
        List<Long> values = Arrays.asList(10,20,30)       
        
        values.stream()
            .forEach(n -> base.set(base.get() + n));
        
        System.out.println(base.get());
``` 
`Holders` utility class can create several implementations of `Holder` objects, including a thread-safe and a read-only
implementations. 
### Nullable
The library introduces `Nullable` object, which is a "drop-in" replacement for `Optional`. It has features that are only 
found in the `Optional` class in Java-11/13 but also includes new features. Currently, there is a consideration to make
the class implement the `Iterable` interface, rational being that it is a container, although it contains one element.
However, there are use cases for this requirement.

### Reducers
`Reducers` are collectors but with a difference. Most of them return `Stream` objects, and so it is possible to continue
functional programming within a stream context. Many of the `Collectors` methods have been implemented in `Reducers` class, 
again as a possible "drop-in" replacement for `Collectors` class.

```
        List<String> strings = Arrays.asList("9","7","5","76","2","40","101");

        strings.stream()
                .map(Integer::parseInt)
                .peek(n -> System.out.print(n+" "))
                .collect(Reducers.summingInt(Integer::valueOf))
                .findFirst()
                .ifPresent(n -> System.out.println("= "+n)); 

                
        Outputs: 9 7 5 76 2 40 101 = 240         
```

## Feedback

Development is ongoing. I have many ideas in the pipeline, and of course will consider your ideas and recommendations. 
If you encounter any bugs, please raise an issue(s).