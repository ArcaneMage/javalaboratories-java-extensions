package org.javalaboratories.core.holders;

import lombok.Getter;
import lombok.Setter;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class HolderTest {
    private static final Logger logger = LoggerFactory.getLogger(HolderTest.class);

    private Holder<String> readWriteHolder;
    private Holder<Person> readOnlyHolder;
    private Holder<String> synchronizedHolder;

    @Setter
    @Getter
    private static class Person {
        private String name;
        private int age;

        public Person(Person p) {
            this(p.name,p.age);
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age &&
                    name.equals(person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return STR."Person[name='\{name}\{'\''}\{']'}";
        }
    }

    @BeforeEach
    public void setup() {
        Person person = new Person("John Doe",26);

        readWriteHolder = Holder.of("Hello World");

        readOnlyHolder = Holder.of(new Person(person)).readOnly();

        synchronizedHolder = Holder.synchronizedHolder(readWriteHolder);
    }

    @Test
    public void testApplicative_Pass() {
        // When
        Holder<Integer> number1 = Holder.of(0);

        // Given
        Function<Integer,Integer> add = n -> n + 10;

        Holder<Integer> value1 = number1
                .apply(Holder.of(add))
                .apply(Holder.of(add));

        // Then
        assertEquals(20,value1.get());
    }

    @Test
    public void testReadOnly_Fail() {
        Holder<String> rholder = readWriteHolder.readOnly();

        assertEquals("Hello World", rholder.get());
        assertThrows(UnsupportedOperationException.class, () -> rholder.set("John Bishop"));
    }

    @Test
    public void testReadOnlySetGet_Fail() {
        Holder<Integer> accumulator = Holder.of(10).readOnly();

        assertThrows(UnsupportedOperationException.class, () -> accumulator.setGet(v -> v + 10));
    }

    @Test
    public void testReadOnlyGetSet_Fail() {
        Holder<Integer> accumulator = Holder.of(10).readOnly();

        assertThrows(UnsupportedOperationException.class, () -> accumulator.getSet(v -> v + 10));
    }

    @Test
    public void testReadWrite_Pass() {
        Holder<Person> mholder = readOnlyHolder.readWrite();

        assertEquals("John Doe", mholder.get().getName());
        mholder.set(new Person("James Smith",30));
        assertEquals("James Smith",mholder.get().getName());
    }

    @Test
    public void testReadOnlyState_Fail() {
        assertThrows(IllegalStateException.class, () -> readOnlyHolder.readOnly());
    }

    @Test
    public void testReadWriteState_Fail() {
        assertThrows(IllegalStateException.class, () -> readWriteHolder.readWrite());
    }

    @Test
    public void testSet_Pass() {
        readWriteHolder.set("Hello Galaxy");
        synchronizedHolder.set("Hello Galaxy");
        assertThrows(UnsupportedOperationException.class, () -> readOnlyHolder.set(new Person("John Smith",26)));
        assertEquals("Hello Galaxy", readWriteHolder.get());
        assertEquals("Hello Galaxy", synchronizedHolder.get());
    }

    @Test
    public void testExists_Pass() {
        Holder<Integer> empty = Holder.empty();

        assertFalse(empty.exists(e -> Integer.valueOf(0).equals(e)));
        assertTrue(readWriteHolder.exists(e -> e.equals("Hello World")));
        assertFalse(readWriteHolder.exists(e -> e.equals("Hello Galaxy")));
    }

    @Test
    public void testFunctorReadWrite_Pass() {
        Holder<Integer> holder = readWriteHolder
                .map(String::length);

        int length = holder
                .fold(0,n -> n);

        assertEquals(11, length);
    }

    @Test
    public void testMap_FunctorLaws_Pass() {
        // Given
        Holder<Integer> value = Holder.of(-1);

        // (1) Identity law
        assertEquals(value, value.map(Function.identity()));
        assertEquals(value, value.map(x -> x));

        // (2) If a function composition (g), (h), then the resulting functor should be the
        // same as calling f with (h) and then with (g)
        assertEquals(value.map(x -> (x + 1) * 2),value.map(x -> x + 1).map(x -> x * 2));
    }

    @Test
    public void testFlatMap_MonadLaws_Pass() {
        // Given
        Holder<Integer> value = Holder.of(-1);
        Function<Integer,Holder<Integer>> fa = x -> Holder.of(x * 2);
        Function<Integer,Holder<Integer>> fb = Holder::of;

        // (1) Left Identity law
        assertEquals(value.flatMap(fa), fa.apply(-1));

        // (2) Right Identity law
        assertEquals(value.flatMap(fb), value);

        // (3) Associative law
        assertEquals(value.flatMap(fa).flatMap(fb),fa.apply(value.get()).flatMap(fb));
    }

    @Test
    public void testFlatMap_Pass() {
        Holder<Integer> holder = Holder.of(5);
        int value = holder
                .flatMap(n -> Holder.of(n +2))
                .fold(0,n -> n);

        assertEquals(7,value);
    }

    @Test
    public void testFlatten_Pass() {
        // Given (setup)
        Holder<Holder<Integer>> value = Holder.of(Holder.of(105));

        // When
        String result = value
                .<Integer>flatten()
                .fold("", Object::toString);

        // Then
        assertEquals("105",result);
    }

    @Test
    public void testFilter_Pass() {
        String string = readWriteHolder
                .filter(s -> s.equals("Hello World"))
                .fold("",s -> s);

        assertEquals("Hello World",string);
    }

    @Test
    public void testFilterNot_Pass() {
        String string = readWriteHolder
                // Filter value when not equals to "Hello World"
                .filterNot(s -> s.equals("Hello World"))
                .fold("",s -> s);

        assertEquals("",string);
    }

    @Test
    public void testFunctorReadOnly_Pass() {
        Holder<Integer> holder = readOnlyHolder
                .map(p -> p.getName().length());

        int length = holder
                .fold(0,n -> n);

        assertEquals(length, 8);
        assertThrows(UnsupportedOperationException.class, () -> holder.set(10));
    }

    @Test
    public void testSetGet_Pass() {
        Holder<Integer> accumulator = Holder.of(10);

        assertEquals(15,accumulator.setGet(v -> v + 2 + 3));
    }

    @Test
    public void testGetSet_Pass() {
        Holder<Integer> accumulator = Holder.of(10);
        int result = accumulator.getSet(v -> v + 2 + 3);

        assertEquals(10, result);
        assertEquals(15, accumulator.get());
    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World", readWriteHolder.get());
        assertEquals("John Doe", readOnlyHolder.get().getName());
        assertEquals("Hello World", synchronizedHolder.get());
    }

    @Test
    public void testOr_Pass() {
        Holder<String> holder = Holder.empty();
        String rHolder = holder
                .or(() -> Holder.of("Hello World"))
                .fold("", s -> s);

        String rwHolder = readWriteHolder
                .or(() -> Holder.of("Hello Galaxy"))
                .fold("",s -> s);

        assertEquals("Hello World",rHolder);
        assertEquals("Hello World", rwHolder);
    }

    @Test
    public void testOrElse_Pass() {
        Holder<String> holder = Holder.empty();

        assertEquals("Empty",holder.orElse("Empty"));
        assertEquals("Hello World",readWriteHolder.orElse("Empty"));
    }

    @Test
    public void testOrElseGet_Pass() {
        Holder<String> holder = Holder.empty();

        assertEquals("Empty",holder.orElseGet(() -> "Empty"));
        assertEquals("Hello World",readWriteHolder.orElseGet(() -> "Empty"));
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Holder.of("Hello World"), readWriteHolder);
        assertNotEquals(Holder.of(new Person("John Doe",26)), readOnlyHolder);
        assertEquals(Holder.synchronizedHolder(readWriteHolder), synchronizedHolder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Holder.of("Hello World").hashCode(), readWriteHolder.hashCode());
        assertEquals(Holder.of(new Person("John Doe",26)).hashCode(), readOnlyHolder.hashCode());
        assertEquals(Holder.synchronizedHolder(readWriteHolder).hashCode(), synchronizedHolder.hashCode());
    }

    @Test
    public void testForEach_Pass() {
        Holder<Boolean> looped = Holder.of(false);
        readWriteHolder.forEach(s -> looped.set(true));

         Holder.empty()
                .forEach(s -> logger.info(String.valueOf(s)));

        assertInstanceOf(Iterable.class, readWriteHolder);
        assertTrue(looped.get());
    }

    @Test
    public void testPeek_Pass() {
        // Given (setup)
        LogCaptor logCaptor = LogCaptor.forClass(HolderTest.class);

        // When
        readWriteHolder
                .map(v -> STR."\{v}, Galaxy")
                .peek(v -> logger.info("Peek value as \"{}\"",v))
                .get();

        // Then
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(s -> s.equals("Peek value as \"Hello World, Galaxy\"")));
    }

    @Test
    public void testHolderComparable_Pass() {
        List<Holder<Integer>> list = Arrays.asList(Holder.of(9),Holder.of(5),Holder.of(3),Holder.of(8));

        String sorted = list.stream()
                .sorted()
                .peek(c -> logger.info(String.valueOf(c)))
                .map(h -> h.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }

    @Test
    public void testHolderComparable_Fail() {
        List<Holder<Person>> list = Arrays.asList(Holder.of(new Person("James May",26)),Holder.of(new Person("Alex Higgins",20)));

        assertThrows(ClassCastException.class, () ->
           list.stream()
                .sorted()
                .map(h -> h.fold("",Person::getName))
                .collect(Collectors.joining(",")));
    }

    @Test
    public void testUseCaseCollect_SideEffects_Pass() {
        Holder<Double> total = Holder.of(0.0);
        IntStream
                .range(0,1000)
                .parallel()
                .filter(n -> n % 2 == 0)
                .forEach(n -> total.setGet(v -> v + n));

        assertEquals(249500.0, total.get());
    }

    @Test
    public void testUseCasesReduce_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .reduce(Holder.of(0.0),(h,v) -> h.map(n -> n + v),(a,b) -> a.map(n -> n + b.fold(0.0,v -> v)))
                .map(n -> n / 2)
                .fold("",n -> STR."Sum of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals("Sum of even numbers (2,4,6,8,10) / 2 = 15.0",result);
        logger.info(result);
    }

    @Test
    public void testUseCasesNoHolders_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        double meanEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToDouble(Double::valueOf)
                .reduce(0.0, Double::sum) / 2;

        String result = STR."Sum of even numbers (2,4,6,8,10) / 2 = \{meanEven}";

        assertEquals("Sum of even numbers (2,4,6,8,10) / 2 = 15.0",result);
        logger.info(result);
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", readWriteHolder.toString());
        assertEquals("Holder[value=Person[name='John Doe']] (Read-Only)", readOnlyHolder.toString());
        assertEquals("Holder[value=Hello World]", synchronizedHolder.toString());
    }
}
