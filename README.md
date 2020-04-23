# java-extensions

This a library of Java utilities to aid programming with lambda just that a little bit more joyful, particularly for 
Java-8 developers but not exclusively. 
### Handlers
Handlers class provides a broad set of wrapper methods to handle checked exceptions within lambda expressions. Lambdas 
are generally short and concise, but checked exceptions can sometimes cause the lambda expression to look unwieldy. 
This class has many useful methods that compliment common functional interfaces. Each method wraps the function object 
into a function that transforms the checked exception to a `RuntimeException` object.

For example, here is an example of a method performing file input/output:
```
        public void writeFile(String file) throws IOException {
            ...
        }
 
        // Common technique is to handle the checked exception within the lambda expression :-
        
        Consumer<String> consumer = s -> {
            try {
                writeFile(s)
            } catch (IOException e) {
                ...
            }
        }
 
        // But using the Handlers class, the expression becomes :-
 
        Consumer<String> consumer = Handlers.consumer(s -> writeFile(s));
```
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
The library introduces `Nullable` class, which is a "drop-in" replacement for `Optional`. It has features that are only 
available in the `Optional` class in Java-11/13 but it also includes new features. For example, the following is possible:
```
    Nullable<Person> person = people.findById(10983);
    
    person.forEach(System.out::println);    
    
    ...
    
    person.ifPresentOrElse(System.out::println, () -> System.out.println("Person not found"))
    
    ...
    
    List<Person> list = person.toList();
```
Similarly, there are `NullableInt`,`NullableLong` and `NullableDouble` for `int`,`long` and `double` types respectively.
### Reducers
`Reducers` are collectors but with a difference. Most of them return `Stream` objects, and so it is possible to continue
functional programming within a stream context. Many of the `Collectors` methods have been implemented in `Reducers` class, 
again as a possible "drop-in" replacement for `Collectors` class. `Reducers` also support a comprehensive set of statistical
calculations such as mean, median, mode, standard deviation and much more. Expect to see an expansion of statistical 
functions in this area over the coming days.  

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
### StopWatch
StopWatch provides a convenient means for timings of methods. There are no explicit methods in the class to start and 
stop the timings, because these are naturally determined through the process of invoking the function that is currently
being timed. In other words, executing the function will start the `StopWatch` and when the function comes to a 
natural/unnatural conclusion, the `StopWatch` is automatically stopped. Number of instances of `StopWatch` is unlimited,
and so useful statistics are available of all the timed functions via the class' methods or via the `StopWatch.print()` 
method which prints pre-formatted data into a string. Every `StopWatch` instance has a unique name, which is useful when 
reviewing the statistics. Use the `StopWatch.time(Runnable)` or the `StopWatch.time(Consumer)` method to start the timings,
the latter is particularly useful for `Collection.forEach(Consumer)` and/or streams.
```
         StopWatch stopWatch = StopWatch.watch("methodOne");
         StopWatch stopWatch2 = StopWatch.watch("methodTwo");
 
         // This is a common usecase of the StopWatch
         stopWatch.time(() -> doSomethingMethod(1000));
 
         // Here is aother sceanario where the for each loop is measured.
         List<Integer> numbers = Arrays.asList(1,2,3,4);
    
         numbers.forEach(stopWatch2.time(n -> doSomethingMethod2(n)));
    
         // This command will print statistics for all StopWatch instances
         System.out.println(StopWatch.print());
    
         // Output :-
    
         Method                       Time (s)    %       Cycles Cycle Time(s)
         --------------------------------------------------------------------
         methodOne                00:00:00.504   8%            1 00:00:00.504
         methodTwo                00:00:01.451  92%            4 00:00:00.363

```
### Tuples
A tuple can be considered as a container of ordered elements of different types. Each element may not relate to each
other but collectively they have meaning. They are particularly useful for methods in that they enable them to 
return multiple values as single tuple object, as well as passing several values to a method in a single argument(s). The
tuple has many abilities including `join`, `truncate`, `transform`, `toMap` and much more. Another particularly useful 
feature of the tuples is that are immutable, making them thread-safe. Morever, they all implement the `Iterable`, 
`Serializable` and `Comparable` interfaces, and so they are sortable and their contents can be traversed easily. Here 
are some examples of usage:
```
         // Creating tuple with a depth of 2
         Tuple2<String,Integer> person = Tuple.of("James",12);
         
        // Retrieve values
        logger.info("Name: {}",person.value1());
        logger.info("Grade: {}",person.value2());
        
        // Outputs :-
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: James
        [main] INFO com.excelsior.core.tuple.TupleTest - Grade: 12
        
        // Setting values using a transformer method: Transform 2nd element
        Tuple2<String,Integer> modified = person.transform2(s -> 16);
        logger.info("Name: {}",modified.value1());
        logger.info("Grade: {}",modified.value2());
        
        // Outputs :-
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: James
        [main] INFO com.excelsior.core.tuple.TupleTest - Grade: 16
        
        // Create tuple from a collection
        List<String> people = Arrays.asList("James","Carl","Andrea","Sharon");

        Nullable<Tuple4<String,String,String,String>> maybeTuple = Tuple4.fromIterable(people);
        maybeTuple.ifPresent(tuple -> tuple.forEach(name -> logger.info("Name: {}",name)));
        
        // Outputs :-
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: James
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: Carl
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: Andrea
        [main] INFO com.excelsior.core.tuple.TupleTest - Name: Sharon
```

## Feedback
Development is ongoing. I have many ideas in the pipeline, and of course will consider your ideas and recommendations. 
If you encounter any bugs, please raise an issue(s).