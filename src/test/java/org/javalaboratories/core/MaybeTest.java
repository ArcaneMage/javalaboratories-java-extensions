package org.javalaboratories.core;

import org.javalaboratories.core.tuple.Pair;
import org.javalaboratories.core.tuple.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class MaybeTest {

    private Maybe<String> maybe;
    private Maybe<String> empty;

    private static final Consumer<String> DO_NOTHING_CONSUMER = (value) -> {};
    private static final Runnable DO_NOTHING_RUNNABLE = () -> {};

    @BeforeEach
    public void setup() {
        maybe = Maybe.of("Hello World");
        empty = Maybe.ofNullable(null);
    }

    @Test
    public void testOf_Pass() {
        assertNotNull(maybe);
        assertNotNull(empty);
    }

    @Test
    public void testIsPresent_Pass() {
        assertTrue(maybe.isPresent());
        assertFalse(empty.isPresent());
    }

    @Test
    public void testContains_Pass() {
        assertTrue(maybe.contains("Hello World"));
        assertFalse(maybe.contains("Does not exist"));
        assertFalse(empty.contains("Hello World"));
    }

    @Test
    public void testExists_Pass() {
        assertTrue(maybe.exists(v -> v.length() > 0));
        assertFalse(maybe.exists(v -> v.length() == 0));
        assertFalse(empty.exists(v -> v.length() == 0));
    }

    @Test
    public void testFilter_Pass() {
        Maybe<String> uncertainty = maybe.filter(value -> value.length() > 0);
        Maybe<String> uncertainty2 = empty.filter(value -> value.length() > 0);

        assertTrue(uncertainty.isPresent());
        assertFalse(uncertainty.isEmpty());

        assertFalse(uncertainty2.isPresent());
        assertTrue(uncertainty2.isEmpty());
    }

    @Test
    public void testFilterNot_Pass() {
        Maybe<String> uncertainty = maybe.filterNot(value -> value.length() > 0);
        Maybe<String> uncertainty2 = maybe.filterNot(value -> value.length() > 11);
        Maybe<String> uncertainty3 = empty.filterNot(value -> value.length() > 0);

        assertFalse(uncertainty.isPresent());
        assertTrue(uncertainty.isEmpty());

        assertTrue(uncertainty2.isPresent());
        assertFalse(uncertainty2.isEmpty());

        assertFalse(uncertainty3.isPresent());
        assertTrue(uncertainty3.isEmpty());
    }

    @Test
    public void testFlatten_Pass() {
        Maybe<Maybe<String>> nested = Maybe.of(Maybe.of("Flattery will get you nowhere"));
        Maybe<Maybe<String>> nothingNested = Maybe.of(Maybe.empty());

        String result = nested
                .<String>flatten() // Type witness needed here :o May consider redesign for this.
                .map(s -> s +", but chocolates might!")
                .fold("",s -> s);

        String nothing = nothingNested
                .<String>flatten()
                .fold("",s -> s);

        assertEquals("Flattery will get you nowhere, but chocolates might!",result);
        assertEquals("",nothing);
    }

    @Test
    public void testForAll_Pass() {
        assertTrue(maybe.forAll(value -> value.length() > 0));
        assertTrue(empty.forAll(value -> value.length() > 0));
    }

    @Test
    public void testFold_Accumulator_Pass() {
        List<Maybe<String>> maybes = Arrays.asList(Maybe.of("This"),Maybe.of("is"),Maybe.empty(),Maybe.of("cool!!!"));
        List<Maybe<String>> emptyMaybes = Arrays.asList(Maybe.empty(),Maybe.empty());

        String message = Maybe.fold(maybes,"",(a,b) -> a.equals("") ? b : a+" "+b);
        String nothing = Maybe.fold(emptyMaybes,"Initially there was nothing",(a,b) -> a.equals("") ? b : a+" "+b);

        assertEquals("This is cool!!!", message);
        assertEquals("Initially there was nothing",nothing);
    }

    @Test
    public void testFold_Reducing_Pass() {
        String value = maybe.fold("Unset",v -> v);
        String unset = empty.fold("Unset",v -> v);
        int valueLength = maybe.fold(-1,String::length);
        int unsetLength = empty.fold(-1,String::length);

        assertEquals("Hello World",value);
        assertEquals(11,valueLength);
        assertEquals("Unset",unset);
        assertEquals(-1,unsetLength);
    }

    @Test
    public void testGroupBy_Reducing_Pass() {
        // Given
        List<Maybe<Integer>> numbers = Arrays.asList(Maybe.of(10),Maybe.of(15),Maybe.empty(),Maybe.of(20),Maybe.of(25));

        // When
        Map<String,List<Integer>> partitions = Maybe.groupBy(numbers,v -> v < 20 ? "Group A" : "Group B");

        // Then
        assertEquals(2,partitions.size());
        assertEquals(2,partitions.get("Group A").size());
        assertTrue(partitions.get("Group A").containsAll(Arrays.asList(10,15)));
        assertEquals(2,partitions.get("Group B").size());
        assertTrue(partitions.get("Group B").containsAll(Arrays.asList(20,25)));

        // Verify immutability
        assertThrows(UnsupportedOperationException.class,() -> partitions.put("Group C",Arrays.asList(1,2)));

        assertThrows(UnsupportedOperationException.class,() -> partitions.get("Group A").add(16));
        assertThrows(UnsupportedOperationException.class,() -> partitions.get("Group B").add(35));
    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World",maybe.get());
    }

    @Test
    public void testGetOrElse_Pass() {
        assertEquals("Hello World", maybe.getOrElse("Hello Mars"));
        assertEquals("Hello Mars", empty.getOrElse("Hello Mars"));
    }

    @Test
    public void testGet_Fail() {
        assertThrows(NoSuchElementException.class, () -> empty.get());
    }

    @Test
    public void testMap_FunctorLaws_Pass() {
        // Given
        Maybe<Integer> value = Maybe.of(-1);

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
        Maybe<Integer> value = Maybe.of(-1);
        Function<Integer,Maybe<Integer>> fa = x -> Maybe.of(x * 2);
        Function<Integer,Maybe<Integer>> fb = Maybe::of;

        // (1) Left Identity law
        assertEquals(value.flatMap(fa), fa.apply(-1));

        // (2) Right Identity law
        assertEquals(value.flatMap(fb), value);

        // (3) Associative law
        assertEquals(value.flatMap(fa).flatMap(fb),fa.apply(value.get()).flatMap(fb));
    }

    @Test
    public void testFlatMap_Pass() {
        Parser parser = new Parser();

        Maybe<String> maybe = Maybe.of("Initial string value");

        maybe.flatMap(parser::parse)
                .ifPresent(value -> assertEquals("{Parsed} Initial string value",value));

        assertFalse(empty.flatMap(value -> Maybe.of("HELLO WORLD"))
                .isPresent());
    }

    @Test
    public void testIfPresent_Pass() {
        AtomicBoolean bool = new AtomicBoolean(false);
        maybe.ifPresent(value -> bool.set(true));

        assertTrue(bool.get());
    }

    @Test
    public void testIfPresentOrElse_Pass() {
        AtomicBoolean bool = new AtomicBoolean(false);

        maybe.ifPresentOrElse(value -> bool.set(true), DO_NOTHING_RUNNABLE);
        assertTrue(bool.get());

        bool.set(false);
        empty.ifPresentOrElse(DO_NOTHING_CONSUMER, () -> bool.set(true));
        assertTrue(bool.get());
    }

    @Test
    public void testIsEmpty_Pass() {
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testMap_Pass() {
        assertEquals(11, (int) maybe.map(String::length).get());
        assertEquals(Maybe.empty(),maybe.map(m -> null));
    }

    @Test
    public void testOr_Pass() {
        String value = maybe
                .or(() -> Maybe.of("Good Morning World"))
                .get();
        assertEquals("Hello World",value);

        value = empty
                .or(() -> Maybe.of("Good Morning World"))
                .get();
        assertEquals("Good Morning World",value);
    }

    @Test
    public void testOrElse_Pass() {
        assertEquals("Hello World", maybe.orElse("Hello Mars"));
        assertEquals("Hello Mars", empty.orElse("Hello Mars"));

    }

    @Test
    public void testOrElseGet_Pass() {
        assertEquals("Hello World", empty.orElseGet(() -> "Hello World"));
    }

    @Test
    public void testOrElseThrow_Fail() {
        assertThrows(NoSuchElementException.class, () -> empty.orElseThrow());
    }


    @Test
    public void testOrElseThrowException_Fail() {
        assertThrows(IllegalArgumentException.class, () -> empty.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    public void testOrElseThrow_Pass() throws IllegalArgumentException {
        assertEquals("Hello World",maybe.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    public void testToList_Pass() {
        List<String> list1 = maybe.toList();
        assertEquals("Hello World", list1.get(0));
        assertEquals(1, list1.size());

        List<String> list2 = empty.toList();
        assertEquals(0, list2.size());

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> list1.add("second"));
        assertThrows(UnsupportedOperationException.class,() -> list2.add("second"));
    }

    @Test
    public void testForEach_Pass() {
        maybe.forEach(Eval.cpeek(value -> value.get().equals("Hello World"),value -> assertEquals("Hello World",value.get())));
    }

    @Test
    public void testToMap_Pass() {
        Map<String,String> map = maybe.toMap(v -> "first");
        assertEquals("Hello World", map.get("first"));

        Map<String,String> emptyMap = empty.toMap(v -> "first");
        assertEquals(0, emptyMap.size());

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> map.put("second","110"));
        assertThrows(UnsupportedOperationException.class,() -> emptyMap.put("second","120"));
    }

    @Test
    public void testToSet_Pass() {
        Set<String> set1 = maybe.toSet();
        assertTrue(set1.contains("Hello World"));

        Set<String> set2 = empty.toSet();
        assertEquals(0, set2.size());

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> set1.add("110"));
        assertThrows(UnsupportedOperationException.class,() -> set2.add("120"));
    }

    @Test
    public void testToStream_Pass() {
        List<String> list = maybe.stream()
                .filter("Hello World"::equals)
                .collect(Collectors.toList());

        assertEquals(1, list.size());
        assertEquals("Hello World",list.get(0));

        list = empty.stream()
                .collect(Collectors.toList());

        assertEquals(0, list.size());
    }

    @Test
    public void testUseCaseFilterMapIfPresent_Pass() {
        maybe
                .filter("Hello World"::equals)
                .map(String::length)
                .ifPresent(value -> assertEquals(11, (int) value));
    }

    @Test
    public void testUseCaseFilterMapIsPresent_Pass() {
        assertFalse(maybe
                .filter("Not Found"::equals)
                .map(String::length)
                .isPresent());
    }

    @Test
    public void testEquals_Pass() {
        Maybe<String> twin = Maybe.of("Hello World");
        assertEquals(maybe, maybe);
        assertEquals(maybe,twin);
    }

    @Test
    public void testHashCode_Pass() {
        Maybe<String> twin = Maybe.of("Hello World");
        assertEquals(maybe.hashCode(),twin.hashCode());
    }

    @Test
    public void testToString_Pass() {
       assertEquals("Maybe[Hello World]",maybe.toString());
       assertEquals("Maybe[isEmpty]",empty.toString());
    }

    @Test
    public void testUnzip_Pass() {
        // Given (Setup)
        Maybe<Pair<Maybe<String>,Maybe<Integer>>> zip1 = this.maybe.zip(Maybe.of(64));
        Maybe<Pair<String,Integer>> zip2 = Maybe.of(Tuple.of("Pair",64).asPair());
        Maybe<Pair<Maybe<String>,Maybe<Integer>>> zip3 = Maybe.of(Tuple.of(Maybe.of("Pair"),Maybe.<Integer>empty()).asPair());

        // When
        Pair<Maybe<String>,Maybe<Integer>> maybe = this.maybe.unzip(); // (empty,empty)
        Pair<Maybe<String>,Maybe<Integer>> unzipped1 = zip1.unzip(); // (String,Integer)
        Pair<Maybe<String>,Maybe<Integer>> unzipped2 = zip2.unzip(); // (empty,empty)
        Pair<Maybe<String>,Maybe<Integer>> unzipped3 = zip3.unzip(); // (empty,empty)

        // Then
        assertTrue(zip1.isZipped());
        assertFalse(zip2.isZipped());
        assertFalse(zip3.isZipped());

        assertTrue(maybe._1().isEmpty());
        assertTrue(maybe._2().isEmpty());
        assertFalse(unzipped1._1().isEmpty());
        assertFalse(unzipped1._2().isEmpty());
        assertTrue(unzipped2._1().isEmpty());
        assertTrue(unzipped2._2().isEmpty());
        assertTrue(unzipped3._1().isEmpty());
        assertTrue(unzipped3._2().isEmpty());

        assertEquals("Hello World",unzipped1._1().orElseThrow());
        assertEquals(64,unzipped1._2().orElseThrow());
    }

    @Test
    public void testZip_Pass() {
        // Given (Setup)

        // When
        Maybe<Pair<Maybe<String>,Maybe<Integer>>> maybePair = this.maybe.zip(Maybe.of(64));
        Maybe<Pair<Maybe<String>,Maybe<Integer>>> maybePair2 = this.maybe.zip(Maybe.empty());
        Maybe<Pair<Maybe<String>,Maybe<Integer>>> maybePair3 = this.empty.zip(Maybe.of(64));

        Pair<Maybe<String>,Maybe<Integer>> pair = maybePair.orElseThrow();

        // Then
        assertTrue(maybePair.isPresent());
        assertTrue(maybePair2.isEmpty());
        assertTrue(maybePair3.isEmpty());
        assertEquals("Hello World",pair._1().orElse(null));
        assertEquals(64,pair._2().orElse(null));
    }

    @Test
    public void testApplicable_Pass() {
        // When
        Maybe<Integer> number = Maybe.of(0);
        Maybe<Integer> nothing = Maybe.empty();

        // Given
        Function<Integer,Integer> add = n -> n + 10;

        Maybe<Integer> value1 = number.apply(Maybe.of(add))
                                     .apply(Maybe.of(add));

        Maybe<Integer> value2 = nothing.apply(Maybe.of(add));

        // Then
        assertEquals(Maybe.of(20),value1);
        assertTrue(value2.isEmpty());
    }

    @Test
    public void testToEither_Pass() {
        // Given
        Either<Exception,Integer> either = Either.right(5);
        Maybe<Integer> maybe = Maybe.ofEither(either);

        // Then
        assertEquals(Maybe.of(5),maybe);
    }

    // Some contrived use case for flatMap
    private static class Parser {
        public Maybe<String> parse(String value) {
            return Maybe.of("{Parsed} "+value);
        }
    }

}
