package com.excelsior.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@SuppressWarnings("WeakerAccess")
public class NullableDouble {

    private Nullable<Double> value;

    private static final NullableDouble EMPTY = new NullableDouble();

    public static NullableDouble empty() { return EMPTY; }

    public static NullableDouble of(double value) { return new NullableDouble(value); }

    public static NullableDouble of(Optional<Double> value) {
        Objects.requireNonNull(value);
        return value.map(NullableDouble::of).orElse(EMPTY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NullableDouble that = (NullableDouble) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public double getAsDouble() { return value.get(); }

    public void ifPresent(Consumer<? super Double> action) {
        this.value.ifPresent(action);
    }

    public void ifPresentOrElse(Consumer<? super Double> action, Runnable emptyAction) {
        this.value.ifPresentOrElse(action,emptyAction);
    }

    public boolean isEmpty () { return this.value.isEmpty(); }

    public boolean isPresent() { return this.value.isPresent(); }

    public double orElse(double other) {
        return this.value.orElse(other);
    }

    public double orElseGet(Supplier<? extends Double> supplier) {
        return this.value.orElseGet(supplier);
    }

    public double orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    public <E extends Throwable> double orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        return this.value.orElseThrow(exceptionSupplier);
    }

    public DoubleStream stream() {
        return this.value.isPresent() ? DoubleStream.of(this.value.get()) : DoubleStream.of();
    }

    public Nullable<Double> toNullable() {
        return isPresent() ? Nullable.of(this.value.get()) : Nullable.empty();
    }

    public String toString() {
        return this.isPresent() ? String.format("NullableDouble[%f]",this.value.get()) : "NullableDouble[isEmpty]";
    }

    private NullableDouble() { this.value = Nullable.empty(); }

    private NullableDouble(double value) { this.value = Nullable.ofNullable(value); }
}
