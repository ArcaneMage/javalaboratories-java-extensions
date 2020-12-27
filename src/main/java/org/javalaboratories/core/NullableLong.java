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
import java.util.stream.LongStream;

@SuppressWarnings("WeakerAccess")
public class NullableLong implements Iterable<Long> {

    private Nullable<Long> value;

    private static final NullableLong EMPTY = new NullableLong();

    public static NullableLong empty() { return EMPTY; }

    public static NullableLong of(long value) { return new NullableLong(value); }

    public static NullableLong of(Optional<Long> value) {
        Objects.requireNonNull(value);
        return value.map(NullableLong::of).orElse(EMPTY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NullableLong that = (NullableLong) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public long getAsLong() { return value.get(); }

    public void ifPresent(Consumer<? super Long> action) {
        this.value.ifPresent(action);
    }

    public void ifPresentOrElse(Consumer<? super Long> action, Runnable emptyAction) {
        this.value.ifPresentOrElse(action,emptyAction);
    }

    public boolean isEmpty () { return this.value.isEmpty(); }

    public boolean isPresent() { return this.value.isPresent(); }

    public long orElse(long other) {
        return this.value.orElse(other);
    }

    public long orElseGet(Supplier<? extends Long> supplier) {
        return this.value.orElseGet(supplier);
    }

    public long orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    public <E extends Throwable> long orElseThrow(Supplier<? extends E> exceptionSupplier) throws E {
        return this.value.orElseThrow(exceptionSupplier);
    }

    public LongStream stream() {
        return this.value.isPresent() ? LongStream.of(this.value.get()) : LongStream.of();
    }

    public Nullable<Long> toNullable() {
        return isPresent() ? Nullable.of(this.value.get()) : Nullable.empty();
    }

    public String toString() {
        return this.isPresent() ? String.format("NullableLong[%d]",this.value.get()) : "NullableLong[isEmpty]";
    }

    @Override
    public Iterator<Long> iterator() {
        return this.value.iterator();
    }

    private NullableLong() { this.value = Nullable.empty(); }

    private NullableLong(long value) { this.value = Nullable.ofNullable(value); }
}
