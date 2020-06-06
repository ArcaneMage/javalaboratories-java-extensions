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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-null value. If the value is present {@code isPresent()}
 * returns {@code true}. If no value is present the object is considered {@code empty()}
 * <p>
 * This object is a drop-in replacement for {@code Optional}. If you're familiar with the {@code Optional},
 * class, you will be familiar with the API of this object. For Java 8 developers, this object offers a wealth of methods
 * that only users of Java 9 and above enjoy, for example {@code IfPresentOrElse}, {@code stream()} and much
 * more.
 * <p>
 * <pre>
 *   {@code
 *    Nullable<String> maybeHelloWorld = Nullable.writableHolder("Hello World");
 *
 *    maybeHelloWorld
 *          .filter("Hello World"::equals)
 *          .map(String::length)
 *          .ifPresent(value -> System.out.println(value));
 *
 *    maybeHelloWorld
 *          .filter("Hello World"::equals)
 *          .map(String::length)
 *          .ifPresentOrElse(System.out::println,
 *              () -> System.out.println("Empty World"));
 *
 *    maybeHelloWorld
 *          .map(String::length)
 *          .filter(s -> s <= 11)
 *          .orElseThrow(IllegalStateException::new);
 *
 *    List<String> list = maybeHelloWorld
 *                          .toList();
 *   }
 * </pre>
 *
 * @param <T> the type of value
 * @see NullableDouble
 * @see NullableInt
 * @see NullableLong
 * @author Kevin H, Java Laboratories
 */
@SuppressWarnings("WeakerAccess")
public final class Nullable<T> implements Iterable<T> {

    private T value;

    private static final Nullable<?> EMPTY = new Nullable<>();

    public static <T> Nullable<T> of(T value) { return new Nullable<>(value); }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Nullable<T> of(Optional<T> optional) {
        Objects.requireNonNull(optional);
        return ofNullable(optional.orElse(null));
    }

    public static <T> Nullable<T> ofNullable(T value) { return value == null ? empty() : of(value); }

    @SuppressWarnings("unchecked")
    public static <T> Nullable<T> empty() {
        return (Nullable<T>) EMPTY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nullable<?> nullable = (Nullable<?>) o;
        return Objects.equals(value, nullable.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public Nullable<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if ( this.value == null ) return this;
        else return predicate.test(value) ? this : empty();
    }

    @SuppressWarnings("unchecked")
    public <U> Nullable<U> flatMap(Function<? super T,? extends Nullable<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if ( this.value == null ) return empty();
        else return Objects.requireNonNull((Nullable<U>) mapper.apply(value));
    }

    public T get() {
        if ( this.value == null ) throw new NoSuchElementException();
        else return value;
    }

    public void ifPresent(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if ( this.value != null )
            action.accept(value);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable elseAction ) {
        Objects.requireNonNull(action);
        if ( this.value == null ) elseAction.run();
        else action.accept(value);
    }

    public boolean isEmpty() { return this.value == null; }

    public <U> Nullable<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if ( this.value == null )
            return empty();
        else
            return Nullable.ofNullable(mapper.apply(value));
    }

    @SuppressWarnings("unchecked")
    public Nullable<T> or (Supplier<? extends Nullable<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (this.value != null) return this;
        else return Objects.requireNonNull((Nullable<T>) supplier.get());
    }

    public T orElse(T other) {
       return this.value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        if (this.value != null) return value;
        else return Objects.requireNonNull(supplier.get());
    }

    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exSupplier) throws E {
        if ( this.value != null ) return value;
        else throw exSupplier.get();
    }

    public Stream<T> stream() {
        if ( value == null ) return Stream.of();
        else return Stream.of(value);
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public List<T> toList() {
        if (this.value != null) return Collections.singletonList(value);
        else return Collections.emptyList();
    }

    public <K,V> Map<K,V> toMap(Function<? super T, ? extends K> keyMapper,Function<? super T,? extends V> valueMapper ) {
        Objects.requireNonNull(keyMapper);
        Objects.requireNonNull(valueMapper);
        if ( this.value != null ) return Collections.singletonMap(keyMapper.apply(value), valueMapper.apply(this.value));
        else return Collections.emptyMap();
    }

    public String toString() {
        if ( this.value == null ) return "Nullable[isEmpty]";
        else return String.format("Nullable[%s]", value);
    }

    public boolean isPresent() { return value != null; }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    private Nullable() { value = null; }

    private Nullable(T value) {
        this.value = value;
        Objects.requireNonNull(value);
    }

}