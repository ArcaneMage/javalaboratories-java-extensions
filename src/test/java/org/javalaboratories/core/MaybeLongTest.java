package org.javalaboratories.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class MaybeLongTest {

    private Optional<Long> optional;
    private MaybeLong nullable;
    private MaybeLong nullable2;
    private MaybeLong empty;

    private static final Consumer<Long> DO_NOTHING_CONSUMER = (value) -> {};
    private static final Runnable DO_NOTHING_RUNNABLE = () -> {};

    @BeforeEach
    public void setup() {
        optional = Optional.of(99L);
        nullable = MaybeLong.of(99L);
        nullable2 = MaybeLong.of(optional);
        empty = MaybeLong.empty();
    }

    @Test
    public void testOf_Pass() {
        assertNotNull(nullable);
        assertNotNull(nullable2);
        assertNotNull(empty);
    }

    @Test
    public void testEquals_Pass() {
        MaybeLong twin = MaybeLong.of(99L);
        assertEquals(nullable,twin);
        assertEquals(nullable, nullable);
    }

    @Test
    public void testHashCode_Pass() {
        MaybeLong twin = MaybeLong.of(99L);
        assertEquals(nullable.hashCode(),twin.hashCode());
    }

    @Test
    public void testGetAsLong_Pass() {
        assertEquals(99L,nullable.getAsLong());
    }

    @Test
    public void testIfPresent_Pass() {
        AtomicReference<Long> reference = new AtomicReference<>();

        nullable.ifPresent(v -> reference.set(v));
        assertEquals((Long) 99L,reference.get());
    }

    @Test
    public void testIfPresentOrElse_Pass() {
        AtomicReference<Long> reference = new AtomicReference<>();

        nullable.ifPresentOrElse(v -> reference.set(v),DO_NOTHING_RUNNABLE);
        assertEquals((Long)99L,reference.get());

        empty.ifPresentOrElse(DO_NOTHING_CONSUMER,() -> reference.set(0L));
        assertEquals((Long) 0L,reference.get());
    }

    @Test
    public void testIsEmpty_Pass() {
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testOrElseGet_Pass() {
        assertEquals(99, empty.orElseGet(() -> 99L));
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
        long value = nullable.stream()
                .map(v -> v + 1)
                .filter(v -> v == 100)
                .findFirst()
                .getAsLong();
        assertEquals(100, value);

        value = empty.stream()
                .sum();
        assertEquals(0,value);
    }

    @Test
    public void testToNullable_Pass() {
        Maybe<Long> value = nullable.toNullable();
        value.ifPresent((v -> assertEquals((Long) 99L,v)));
    }

    @Test
    public void testToString_Pass() {
        assertEquals("NullableLong[99]",nullable.toString());
        assertEquals("NullableLong[isEmpty]",empty.toString());
    }

    @Test
    public void testForEach_Pass() {
        nullable.forEach(Eval.cpeek(value -> value.get() > 90L,value -> assertEquals(99L,value.get())));
    }
}
