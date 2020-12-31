package org.javalaboratories.core;


import org.javalaboratories.util.Holder;
import org.javalaboratories.util.Holders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
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
    public void testFilter_Pass() {
        maybe
                .filter(value -> value.equals("Hello World"))
                .ifPresent(value -> assertEquals("Hello World",value));

        assertFalse(maybe
                        .filter(value -> value.equals("Hello"))
                        .isPresent());
        assertTrue(empty.filter(value -> value.equals("Empty"))
                .isEmpty());
    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World",maybe.get());
    }

    @Test
    public void testGet_Fail() {
        assertThrows(NoSuchElementException.class, () -> empty.get());
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
        assertEquals(11, (int) maybe.map (String::length).get());
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
        assertEquals("Hello World", empty.orElse("Hello World"));
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
        List<String> list = maybe.toList();
        assertEquals("Hello World", list.get(0));
        assertEquals(1, list.size());

        list = empty.toList();
        assertEquals(0, list.size());
    }

    @Test
    public void testForEach_Pass() {
        Holder<Boolean> forEachHolder = Holders.writableHolder(false);

        maybe.forEach(v -> forEachHolder.set(true));
        assertTrue(forEachHolder.get());

        forEachHolder.set(false);
        empty.forEach(v -> forEachHolder.set(true));
        assertFalse(forEachHolder.get());
    }

    @Test
    public void testToMap_Pass() {
        Map<String,String> map = maybe.toMap(v -> "first", v -> v);
        assertEquals("Hello World", map.get("first"));

        Map<String,String> emptyMap = empty.toMap(v -> "first",v -> v);
        assertEquals(0, emptyMap.size());
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

    // Some contrived use case for flatMap
    private static class Parser {
        public Maybe<String> parse(String value) {
            return Maybe.of("{Parsed} "+value);
        }
    }

}
