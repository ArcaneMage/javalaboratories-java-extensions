package org.javalaboratories.core;
/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@SuppressWarnings("WeakerAccess")
public class NullableDouble implements Iterable<Double> {

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

    @Override
    public Iterator<Double> iterator() {
        return this.value.iterator();
    }

    private NullableDouble() { this.value = Nullable.empty(); }

    private NullableDouble(double value) { this.value = Nullable.ofNullable(value); }
}
