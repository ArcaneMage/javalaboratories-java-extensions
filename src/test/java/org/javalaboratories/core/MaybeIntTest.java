package org.javalaboratories.core;

import org.javalaboratories.util.Holder;
import org.javalaboratories.util.Holders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class MaybeIntTest {

    private Optional<Integer> optional;
    private MaybeInt nullable;
    private MaybeInt nullable2;
    private MaybeInt empty;

    private static final Consumer<Integer> DO_NOTHING_CONSUMER = (value) -> {};
    private static final Runnable DO_NOTHING_RUNNABLE = () -> {};

    @BeforeEach
    public void setup() {
        optional = Optional.of(99);
        nullable = MaybeInt.of(99);
        nullable2 = MaybeInt.of(optional);
        empty = MaybeInt.empty();
    }

    @Test
    public void testOf_Pass() {
        assertNotNull(nullable);
        assertNotNull(nullable2);
        assertNotNull(empty);
    }

    @Test
    public void testEquals_Pass() {
        MaybeInt twin = MaybeInt.of(99);
        assertEquals(nullable,twin);
        assertEquals(nullable, nullable);
    }

    @Test
    public void testHashCode_Pass() {
        MaybeInt twin = MaybeInt.of(99);
        assertEquals(nullable.hashCode(),twin.hashCode());
    }

    @Test
    public void testGetAsInt_Pass() {
        assertEquals(99,nullable.getAsInt());
    }

    @Test
    public void testIfPresent_Pass() {
        AtomicReference<Integer> reference = new AtomicReference<>();

        nullable.ifPresent(v -> reference.set(v));
        assertEquals((Integer) 99,reference.get());
    }

    @Test
    public void testIfPresentOrElse_Pass() {
        AtomicReference<Integer> reference = new AtomicReference<>();

        nullable.ifPresentOrElse(v -> reference.set(v),DO_NOTHING_RUNNABLE);
        assertEquals((Integer) 99,reference.get());

        empty.ifPresentOrElse(DO_NOTHING_CONSUMER,() -> reference.set(0));
        assertEquals((Integer) 0,reference.get());
    }

    @Test
    public void testIsEmpty_Pass() {
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testOrElseGet_Pass() {
        assertEquals(99, empty.orElseGet(() -> 99));
    }

    @Test
    public void testOrElse_Pass() {
        assertEquals(99,empty.orElse(99));
    }

    @Test
    public void testOrElseThrow_Fail() {
        assertThrows(NoSuchElementException.class, () -> empty.orElseThrow());

        assertEquals(99,nullable.orElseThrow());
    }

    @Test
    public void testOrElseThrowException_Fail() {
        assertThrows(IllegalArgumentException.class, () -> empty.orElseThrow(IllegalArgumentException::new));

        assertEquals(99,nullable.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    public void testIsPresent_Pass() {
        assertTrue(nullable.isPresent());
    }

    @Test
    public void testStream_Pass() {
        int value = nullable.stream()
                .map(v -> v + 1)
                .filter(v -> v == 100)
                .findFirst()
                .getAsInt();
        assertEquals(100, value);

        value = empty.stream()
                .sum();
        assertEquals(0,value);
    }

    @Test
    public void testToNullable_Pass() {
        Maybe<Integer> value = nullable.toNullable();
        value.ifPresent((v -> assertEquals((Integer) 99,v)));
    }

    @Test
    public void testForEach_Pass() {
        Holder<Boolean> forEachHolder = Holders.writableHolder(false);

        nullable.forEach(v -> forEachHolder.set(true));
        assertTrue(forEachHolder.get());

        forEachHolder.set(false);
        empty.forEach(v -> forEachHolder.set(true));
        assertFalse(forEachHolder.get());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("NullableInt[99]",nullable.toString());
        assertEquals("NullableInt[isEmpty]",empty.toString());
    }
}
