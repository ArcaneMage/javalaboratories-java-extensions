# java-extensions

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.javalaboratories/java-extensions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.javalaboratories/java-extensions)

This is a library of utilities to encourage and support functional programming in Java, With inspiration from functional 
programming articles and languages like Haskell and Scala, new monads and enhancements to existing ones have been introduced.
This page provides a brief description of the objects in the library, but it is encouraged to review the javadoc 
documentation for additional information and examples. 

### Maven Central Repository
The library is now available for download from Maven Central Repository. In the POM file add the maven dependency 
configuration below:
```
        <!-- https://mvnrepository.com/artifact/org.javalaboratories/java-extensions -->
        <dependency>
          <groupId>org.javalaboratories</groupId>
          <artifactId>java-extensions</artifactId>
          <version>1.1.1-RELEASE</version>
        </dependency>
```
Alternatively, for `Gradle` users, amend the `build.gradle` file with the following:
```
        // https://mvnrepository.com/artifact/org.javalaboratories/java-extensions
        compile group: 'org.javalaboratories', name: 'java-extensions', version: '1.1.1-RELEASE'
```
### CryptographyFactory
Use the `CryptographyFactory` class to gain access to both symmetric and asymmetric cryptography objects. It provides
a simple abstraction over the `Java Cryptography Extension (JCE)`. Also, an abstraction of the `KeyStore` class has been 
introduced to simplify `PublicKey` and `SecretKey` usage with the cryptography objects. Here are a couple of decryption
examples that use symmetric keys. Note: assume that the encrypted data in the examples was encrypted with the same
key:
```
        // Decrypt with a SecretKey sourced from a KeyStore. Note: the encrypted data must've been encrypted with the 
        // same key.
        secretKeyStore = SecretKeyStore.builder()
                .keyStoreStream(new FileInputStream(KEYSTORE_JCEJKS_FILE)
                .storePassword("changeit")
                .build();
                
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        SecretKey key = secretKeyStore.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD);
        
        byte[] result = cryptography.decrypt(key, encryptedData);
        
        System.out.println(new String(result));
        
        ...
        ...
        
        // ...Or decrypt with a String key. Note: the encrypted data must've been encrypted with the same key.
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        
        byte[] result = cryptography.decrypt("012345", encryptedData);
        
        String data = new String(result);
        
        System.out.println(new String(result));
```
Review the factory and other related classes for more information in the cryptography package.
### Either
`Either` class is a container, similar to the `Maybe` and `Optional` classes, that represents one of two possible values
(a disjoint union). Application and/or sub-routines often have one of two possible outcomes, a successful completion or
a failure, and so it is common to encapsulate these outcomes within an `Either` class. Convention dictates there is a 
`Left` and `Right` sides; "left" considered to be the "unhappy" outcome, and the "right" as the "happy" outcome or path.
So rather than a method throwing an exception, it can return an `Either` implementation that could be either a `Left` 
or a `Right` object, and thus allowing the client to perform various operations and decide on the best course of action.
In the example, the `parser.readFromFile` method returns an `Either` object, but notice the concise `client` code and 
readability and how it neatly manages both "unhappy" and "happy" outcomes.
```
        // Client code using parser object.
        String string = parser.readFromFile(file)
                .flatMap(parser::parse)
                .map(jsonObject::marshal)
                .fold(Exception::getMessage,s -> s);
        ...
        ...
        // Parser class (partial implementation)
        public class Parser {
            public Either<Exception,String> readFromFile(File file) {
            try {
                ...
                return Either.right(fileContent)
            } catch (FileNotFoundException e) {
                return Either.left(e);
            }
        }
```
Provided implementations of the `Either` are right-biased, which means operations like `map`,`flatMap` and others have 
no effect on the `Left` implementation, such operations return the "left" value unchanged.
### Eval
Some objects are expensive to create due to perhaps database access and/or complex calculations. Rather than creating
these objects before they are actually needed, `Eval` can leverage a lazy strategy, offering the access to the underlying
 `value` only at the point of use. Essentially, the library introduces three main strategies:
 1. _Always_ - Evaluation always retrieves the `value` at the point of use -- no caching is involved.
 2. _Eager_  - Evaluation occurs immediately and the `value` is therefore readily available.
 3. _Later_  - Evaluation retrieves the `value` at the point of use and caches it for efficient retrieval.
 Like `Maybe`, `Either` and other objects provided in this library, `Eval` implements `flatMap`, `map` and other useful
 operations. Scala's Cat library and lazy design pattern provided the inspiration for this object.
```
        Eval<Integer> eval = Eval.later(() -> {
            logger.info ("Running expensive calculation...",value);
            return 1 + 2 + 3;
        })
        // eval = Later[unset]

        eval.get();
        // Running expensive calculation...
        // eval = Later[7]

        eval.get();
        // eval = Later[7]
```
In the above case, `eval` object caches the results of the calculation, hence no repetition of the "Running expensive 
calculation" message. Review javadoc for additional details on supported operations. 
### EventBroadcaster
`EventBroadcaster` class has the ability to notify its `subscribera` of events they are interested in. It is a partial
implementation of the `Observer Design Pattern`. To complete the design pattern, implement the `EventSubscriber` 
interface and subclass `AbstractEvent` class for defining custom events or use the out-of-the-box `Event` objects in the
 `CommonEvents` class. It is recommended to encapsulate the `EventBroadcaster` object within a class considered to be 
 observable. 
```
        public class DownloadEvent extends AbstractEvent {
            public static final DOWNLOAD_EVENT = new DownloadEvent();
            public DownloadEvent() { super(); }
        }

        public class News implements EventSource {
            private EventPublisher<String> publisher;
            
            public News() {
                publisher = new EventBroadcaster<>(this);
            }

            public void addListener(EventSubscriber subscriber, Event... captureEvents) {
                publisher.subscribe(subscriber,captureEvents);
            }

            public void download() {
                ...
                ...
                publisher.publish(DOWNLOAD_EVENT,"Complete");
                ...             
            }
        }
        ...
        ...
        public class NewsListener implements EventSubscriber<String> {
            public notify(Event event, String value) {
                logger.info ("Received download event: {}",value);
            }
        }
        ...
        ...
        public class NewsPublisherExample {
            public static void main(String args[]) {
                News news = new News();
                NewsListener listener1 = new NewsListener();
                NewsListener<String> listener2 = (event,state) -> logger.info("Received download event: {}",state);

                news.addListener(listener1,DOWNLOAD_EVENT);
                news.addListener(listener2,DOWNLOAD_EVENT);

                news.download();
            }
        }
```
The `EventBroadcaster` class is thread-safe, but for additional information on this and associated classes, please refer
 to the javadoc details.

### Floadgate, Torrent
These classes are used to detect possible thread-safe issues in target objects by subjecting them to method calls 
from multiple threads. Currently, they do no assert the state of the target object, but generate log information
for analysis. Each `Floodgate` is configured with a specific number of thread workers with each worker calling the 
target object's method repeatedly for a configured number of times. For example the `Floodgate` in the example code below
configures 5 (default) thread workers to repeatedly call the `add(10)` method 5 (default) times, and so the expected 
total of the additions is 250, as opposed to 240, clearly indicating lost updates. 
```
        Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> statistics.add(10));

        floodgate.open();
        List<Integer> results = floodgate.flood();

        logger.info("UnsafeStatics statistics={}", unsafe);

        >> output:  statistics=UnsafeStatistics(total=240, requests=24, average=10.0 
```
`Floodgate` is really designed to flood one method/resource, but it is possible to target multiple methods of an object 
under test with this class, but consider the use of `Torrent` instead for this purpose. `Torrent` manages and controls 
multiple `Floodgates`, ensuring a fairer distribution of thread workers in the core thread pool as well as triggering the 
flood of all floodgates simultaneously. These features increase the likelihood of detecting thread-safe issues in the 
target object. Review the javadoc for more information.
```
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate("print", () -> unsafe.print()) 
                .withFloodgate("add", () -> unsafe.add(10))
                .build();

        torrent.open();
        torrent.flood();
``` 
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
### Holders
In cases where it may be necessary to capture and manipulate values from or in lambdas, often containers are used, such as
Atomic wrappers. This library now has new and improved Holders that are themselves applicatives, monads and functors, which
means `map`, `flatMap` and others are available for operations on the contained value. The most basic operations include
the `get` and `set` methods to read or mutate the contained value. Although they are mutable, holder objects are thread-safe, 
in that the reference to the contained value cannot be changed by more than one thread. Ensure the contained value is 
itself thread-safe to guarantee complete thread safety. Factory methods are provided to create both mutable immutable
holders. Below, are examples of usage:
```
        // This example demenstrates side-effects where the Holder object
        // captures the subtotal of the even numbers. Although it is mutated
        // it is thread-safe.
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        Holder<Double> subtotal = Holder.of(0.0);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .collect(() -> subtotal,(a,b) -> a.set(b + a.get()),(a,b) -> a.set(b.get()))
                .map(n -> n / 2)
                .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals(30, subtotal.get());
        assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15.0",result);

        ...
        ...
                
        // In this example, the Holder object is created and mutated within the 
        // context of the reducer.
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .reduce(Holder.of(0.0),(h,v) -> h.map(n -> n + v),(a,b) -> a.map(n -> n + b.fold(0.0,v -> v)))
                .map(n -> n / 2)
                .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15.0",result);
```
### Maybe
The library introduces `Maybe` class, which is a "drop-in" replacement for `Optional`. It has features that are only 
available in the `Optional` class in Java-9/11/13 but it also includes new features. For example, the following is
 possible:
```
    Maybe<Person> person = people.findById(10983);
    
    person.forEach(System.out::println);    
    
    ...
    
    person.ifPresentOrElse(System.out::println, () -> System.out.println("Person not found"))
    
    ...
    
    List<Person> list = person.toList();
```
Similarly, there are `NullableInt`,`NullableLong` and `NullableDouble` for `int`,`long` and `double` types respectively. 
Release v1.0.5 includes many new features found in Scala and Haskell such as `filterNot`, `flatten` and `fold`-- 
review javadoc for further details.
### Promise
The `Promise` object is a lightweight abstraction of the `CompletableFuture` object, the inspiration of which came from 
the JavaScript's Promise object behaviour. This implementation provides an easily understood API for asynchronous 
submission of tasks encapsulated as `Action` objects with comprehensive exception management. The example below 
demonstrates the ability to perform I/O and transformation of data asynchronously, which is then output to the console 
in the main thread:
```
    Promise<String> promise = Promises
       .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
       .then(TransmuteAction.of(value -> "Value read from the database: "+value));
 
       String result = promise.getResult()
            .IfPresent(result -> System.out::println(result)); 
``` 
There's a lot more to discover about `Promise` objects -- review the source's Javadoc for details. 
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
and so useful statistics are available of all the timed functions via the class' methods or via the `StopWatch.getTime` 
methods. Every `StopWatch` instance has a unique name, which is useful when reviewing the timings. Use the
`StopWatch.time(Runnable)` method to start the timings.
```
         StopWatch stopWatch = StopWatch.watch("methodOne");
         StopWatch stopWatch2 = StopWatch.watch("methodTwo");
 
         // This is a common usecase of the StopWatch
         stopWatch.time(() -> doSomethingMethod(1000));
 
         // Want review all the timings? This is easy!
         StopWatch.forEach((a,b) -> logger.info("{} \t-> {}",a,b));
         
         // Output :-
         10:47:20.394 [main] INFO  StopWatch - methodOne 	-> 00:00:01.000
         10:47:20.399 [main] INFO  StopWatch - methodTwo 	-> 00:00:00.000
```
### Tuples
A tuple can be considered as a container of ordered elements of different types. Each element may not relate to each
other but collectively they have meaning. They are particularly useful for methods in that they enable them to 
return multiple values as a single tuple object, as well as passing several values to a method in a single argument(s).
The tuple has many abilities including `join`, `truncateAt`, `mapAt`, `match`,`toList`,`toMap` and much more. Another 
particularly useful feature of tuples is that they are immutable, making them thread-safe. Moreover, they all implement 
the `Iterable`, `Serializable`, and `Comparable` interfaces, allowing their contents can be traversed easily, sortable in 
collections and persistable. Here are some examples of usage:
```
        Tuple3<String,Integer,Integer> tupleEarth = of("Earth",7926,92955807);
        // tupleEarth: ("Earth",7926,92955807), diameter in miles, distance from Sun in miles

        tupleEarth.value2();
        // tupleEarth.value2(): 7926

        Tuple3<String,Integer,Integer> kmEarth = tupleEarth.mapAt2(t -> Math.round((t / (float) 0.621371)));
        // tupleEarth: ("Earth",12756,92955807), diameter in km

        Tuple5<String,Integer,Integer,String,Integer> tupleEarthMoon = tupleEarth.join(of("Moon",2159));
        // earthMoon: ("Earth",7926,92955807,"Moon",2159), joined moon, diameter of 2159

        Tuple2<Tuple3<String,Integer,Integer>,Tuple2<String,Integer>> tuplePlanetaryBodies = tupleEarthMoon.spliceAt4();
        // planetaryBodies: (("Earth",7926,92955807),("Moon",2159))

        tupleEarth = tuplePlanetaryBodies.value1();
        // tupleEarth: ("Earth",7926,92955807)

        Tuple3<String,Integer,Integer> tupleMoon = tuplePlanetaryBodies.value2().join(92900000);
        // tupleMoon: ("Moon",2159,92900000), added moon distance from Sun

        Tuple7<String,String,String,String,String,String,String> tupleCoordinates = tupleEarth
                .truncateAt2()
                .addAt1("Milky Way")
                .join(of("Europe","England","Blackfriars","London","EC2 1QW"));
        // tupleCoordinates: ("Milky Way","Earth","Europe","England","Blackfriars","London","EC2 1QW")

        List<?> list = tupleCoordinates.toList();
        // list: ["Milky Way","Earth","Europe","England","Blackfriars","London","EC2 1QW"]

        tupleEarth.match(allOf("^Earth$"),(a,b,c) -> logger.info("Earth's distance from Sun {}",c));
        // Outputs: "Earth's distance from Sun 92955807"
```
### Try
`Try` is another class that represents a computation/operation that may either result in an exception or a success.
It is similar to the `Either` class type, but it dynamically decides the success/failure state. The implementation of the
`Try` class is inspired by Scala's Try class, and is considered to be a monad as well as a functor, which means the 
context of the container is transformable via the `flatMap` and `map` methods.

Below are some use cases demonstrating the elegant recovery strategies and other features:
````
        // Recovering from arithmetic exceptions: result1="Result1=1000"
        String result1 = Try.of(() -> 100 / 0)
                            .recover(t -> t instanceof ArithmeticException ? 100 : 100)
                            .map(n -> n * 10)
                            .filter(n -> n > 500)
                            .fold("",n -> "Result1="+n);

        // Using orElse to recover: result2="Result2=2500"
        String result2 = Try.of(() -> 100 / 0)
                            .orElse(100)
                            .map(n -> n * 25)
                            .filter(n -> n > 500)
                            .fold("",n -> "Result2="+n);

        // IOExceptions are handled gracefully too: result3=0
        int result3 = Try.of(() -> new String(Files.readAllBytes(Paths.get("does-not-exist.txt"))))
                            .orElse("")
                            .map(String::length)
                            .fold(-1,Function.identity());
````
There are many more operations available, the API is documented, so go ahead and explore them. There is a potential case
to abandon the use of the try-catch block in favour of a more functional programming approach.

## Feedback
Development is ongoing. I have many ideas in the pipeline, and of course will consider your ideas and recommendations. 
If you encounter any bugs, please raise an issue(s).

## License
Licensed under the Apache License, Version 2.0 (the "License")

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
