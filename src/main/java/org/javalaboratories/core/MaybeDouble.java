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
package org.javalaboratories.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@SuppressWarnings("WeakerAccess")
public class MaybeDouble implements Iterable<Double> {

    private Maybe<Double> value;

    private static final MaybeDouble EMPTY = new MaybeDouble();

    public static MaybeDouble empty() { return EMPTY; }

    public static MaybeDouble of(double value) { return new MaybeDouble(value); }

    public static MaybeDouble of(Optional<Double> value) {
        Objects.requireNonNull(value);
        return value.map(MaybeDouble::of).orElse(EMPTY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaybeDouble that = (MaybeDouble) o;
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

    public Maybe<Double> toNullable() {
        return isPresent() ? Maybe.of(this.value.get()) : Maybe.empty();
    }

    public String toString() {
        return this.isPresent() ? String.format("NullableDouble[%f]",this.value.get()) : "NullableDouble[isEmpty]";
    }

    @Override
    public Iterator<Double> iterator() {
        return this.value.iterator();
    }

    private MaybeDouble() { this.value = Maybe.empty(); }

    private MaybeDouble(double value) { this.value = Maybe.ofNullable(value); }
}
