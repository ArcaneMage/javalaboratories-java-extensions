package org.javalaboratories.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class MaybeDoubleTest {

    private Optional<Double> optional;
    private MaybeDouble nullable;
    private MaybeDouble nullable2;
    private MaybeDouble empty;

    private static final Consumer<Double> DO_NOTHING_CONSUMER = (value) -> {};
    private static final Runnable DO_NOTHING_RUNNABLE = () -> {};

    @BeforeEach
    public void setup() {
        optional = Optional.of(99.9999);
        nullable = MaybeDouble.of(99.9999);
        nullable2 = MaybeDouble.of(optional);
        empty = MaybeDouble.empty();
    }

    @Test
    public void testOf_Pass() {
        assertNotNull(nullable);
        assertNotNull(nullable2);
        assertNotNull(empty);
    }

    @Test
    public void testEquals_Pass() {
        MaybeDouble twin = MaybeDouble.of(99.9999);
        assertEquals(nullable,twin);
        assertEquals(nullable, nullable);
    }

    @Test
    public void testHashCode_Pass() {
        MaybeDouble twin = MaybeDouble.of(99.9999);
        assertEquals(nullable.hashCode(),twin.hashCode());
    }

    @Test
    public void testGetAsDouble_Pass() {
        assertEquals(99.9999,nullable.getAsDouble());
    }

    @Test
    public void testIfPresent_Pass() {
        AtomicReference<Double> reference = new AtomicReference<>();

        nullable.ifPresent(v -> reference.set(v));
        assertEquals((Double)99.9999,reference.get());
    }

    @Test
    public void testIfPresentOrElse_Pass() {
        AtomicReference<Double> reference = new AtomicReference<>();

        nullable.ifPresentOrElse(v -> reference.set(v),DO_NOTHING_RUNNABLE);
        assertEquals((Double) 99.9999,reference.get());

        empty.ifPresentOrElse(DO_NOTHING_CONSUMER,() -> reference.set(0.0));
        assertEquals((Double) 0.0,reference.get());
    }

    @Test
    public void testIsEmpty_Pass() {
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testOrElseGet_Pass() {
        assertEquals(99.9999, empty.orElseGet(() -> 99.9999));
    }

    @Test
    public void testOrElse_Pass() {
        assertEquals(99.9999,empty.orElse(99.9999));
    }

    @Test
    public void testOrElseThrow_Fail() {
        assertThrows(NoSuchElementException.class, () -> empty.orElseThrow());

        assertEquals(99.9999,nullable.orElseThrow());
    }

    @Test
    public void testOrElseThrowException_Fail() {
        assertThrows(IllegalArgumentException.class, () -> empty.orElseThrow(IllegalArgumentException::new));

        assertEquals(99.9999,nullable.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    public void testIsPresent_Pass() {
        assertTrue(nullable.isPresent());
    }

    @Test
    public void testStream_Pass() {
        double value = nullable.stream()
                .map(v -> v + 0.0001)
                .filter(v -> v == 100.0)
                .findFirst()
                .getAsDouble();
        assertEquals(100, value);

        value = empty.stream()
                .sum();
        assertEquals(0.0,value);
    }

    @Test
    public void testToNullable_Pass() {
        Maybe<Double> value = nullable.toNullable();
        value.ifPresent((v -> assertEquals((Double) 99.9999,v)));
    }

    @Test
    public void testForEach_Pass() {
        nullable.forEach(Eval.cpeek(value -> value.map(v -> true),value -> assertTrue(value.get())));
    }

    @Test
    public void testToString_Pass() {
        assertEquals("NullableDouble[99.999900]",nullable.toString());
        assertEquals("NullableDouble[isEmpty]",empty.toString());
    }
}
