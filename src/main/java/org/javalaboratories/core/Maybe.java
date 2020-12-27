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

import lombok.EqualsAndHashCode;
import org.javalaboratories.util.Generics;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-null value. If the
 * value is present {@code isPresent()} returns {@code true}. If no value is
 * present the object is considered {@code empty()}
 * <p>
 * This object is a drop-in replacement for {@code Optional}. If you're
 * familiar with the {@code Optional}, class, you will be familiar with the API
 * of this class. For Java 8 developers, this object offers a wealth of
 * methods that only users of Java 9 and above enjoy, for example {@code
 * IfPresentOrElse}, {@code stream()} and much more.
 * <p>
 * <pre>
 *   {@code
 *    Maybe<String> maybeHelloWorld = Maybe.of("Hello World");
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
 * @see MaybeDouble
 * @see MaybeInt
 * @see MaybeLong
 * @author Kevin H, Java Laboratories
 */
@EqualsAndHashCode
public final class Maybe<T> implements Iterable<T> {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<T> delegate;

    private static final Maybe<?> EMPTY = new Maybe<>();

    public static <T> Maybe<T> of(T value) {
        return new Maybe<>(value);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Maybe<T> of(Optional<T> optional) {
        Objects.requireNonNull(optional);
        return ofNullable(optional.orElse(null));
    }

    public static <T> Maybe<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public static <T> Maybe<T> empty() {
        return Generics.unchecked(EMPTY);
    }

    public Maybe<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return delegate == delegate.filter(predicate) ? this : empty();
    }

    @SuppressWarnings("unchecked")
    public <U> Maybe<U> flatMap(Function<? super T,? extends Maybe<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        T value = value();
        if (value == null) return empty();
        else return Objects.requireNonNull((Maybe<U>) mapper.apply(value));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public T get() {
        return delegate.get();
    }

    public void ifPresent(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        delegate.ifPresent(action);
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable elseAction) {
        Objects.requireNonNull(action);
        if (this.value() == null) elseAction.run();
        else action.accept(value());
    }

    public boolean isEmpty() {
        return !delegate.isPresent();
    }

    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        Optional<U> result = delegate.map(mapper);
        return result.isPresent() ? Maybe.of(result) : empty();
    }

    public Maybe<T> or (Supplier<? extends Maybe<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (this.value() != null) return this;
        else return Objects.requireNonNull(Generics.unchecked(supplier.get()));
    }

    public T orElse(T other) {
       return delegate.orElse(other);
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return delegate.orElseGet(supplier);
    }

    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    public <E extends Throwable> T orElseThrow(Supplier<? extends E> exSupplier) throws E {
        return delegate.orElseThrow(exSupplier);
    }

    public Stream<T> stream() {
        if (value() == null) return Stream.of();
        else return Stream.of(value());
    }

    public Optional<T> toOptional() {
        return delegate;
    }

    public List<T> toList() {
        if (this.value() != null) return Collections.singletonList(value());
        else return Collections.emptyList();
    }

    public <K,V> Map<K,V> toMap(Function<? super T, ? extends K> keyMapper,Function<? super T,? extends V> valueMapper) {
        Objects.requireNonNull(keyMapper);
        Objects.requireNonNull(valueMapper);
        if (this.value() != null) return Collections.singletonMap(keyMapper.apply(value()), valueMapper.apply(this.value()));
        else return Collections.emptyMap();
    }

    public String toString() {
        if (this.value() == null) return "Maybe[isEmpty]";
        else return String.format("Maybe[%s]", value());
    }

    public boolean isPresent() {
        return delegate.isPresent();
    }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    private Maybe() {
        delegate = Optional.empty();
    }

    private Maybe(T value) {
        this.delegate = Optional.of(Objects.requireNonNull(value));
    }

    private T value() {
        return delegate.orElse(null);
    }
}
