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
import org.javalaboratories.util.Arguments;
import org.javalaboratories.util.Generics;

import java.util.*;
import java.util.function.*;
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
 * {@link Maybe} class inherits {@link Optional} class behaviour via {@code
 * composition}, but does provide additional behaviour generally found in other
 * languages like Scala and Haskell:
 * <ul>
 *     <li>contains(element)</li>
 *     <li>exists(Predicate)</li>
 *     <li>filterNot(Predicate)</li>
 *     <li>flatten()</li>
 *     <li>forAll(Predicate)</li>
 *     <li>fold(default,Function)</li>
 *     <li>fold(Iterable,identity,BiFunction<)/li>
 *     <li>forEach(Consumer)</li>
 *     <li>iterator</li>
 *     <li>toList</li>
 *     <li>toMap</li>
 * </ul>
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

    /**
     * Factory method to create an instance of the {@link Maybe} object.
     *
     * @param value represented by {@code Maybe} object.
     * @param <T> Type of value.
     * @return a {@code Maybe} encapsulating {@code value}
     */
    public static <T> Maybe<T> of(final T value) {
        return new Maybe<>(value);
    }

    /**
     * Factory method to create an instance of the {@link Maybe} object.
     * <p>
     * If the use case demands the {@code value} to be {@code null}, this
     * method will provide a {@code Maybe} instance with an {@code empty}
     * value, otherwise a {@code Maybe} object is returned with the
     * encapsulated {@code value}.
     * <p>
     * @param value represented by {@code Maybe} object.
     * @param <T> Type of value.
     * @return a {@code Maybe} encapsulating {@code value}
     */
    public static <T> Maybe<T> ofNullable(final T value) {
        return value == null ? Maybe.empty() : Maybe.of(value);
    }

    /**
     * Factory method to return an instance of {@link Maybe} object with an
     * {@code empty} value.
     * <p><
     * @param <T> Type of value.
     * @return an {@code empty} {@link Maybe} object.
     */
    public static <T> Maybe<T> empty() {
        return Generics.unchecked(EMPTY);
    }

    /**
     * Returns the final result of all the {@code Maybe} nonempty values
     * returned from each element of the {@link Iterable} object having applied
     * the {@code accumulator} function on each of them.
     * <p>
     * This method is similar to the {@link Stream#reduce} method, except that
     * it does not support parallelism.
     *
     * @param values to perform {@code accumulator} function.
     * @param identity is the initial value of the fold operation.
     * @param accumulator a function that takes two parameters: an interim result
     *                    of the fold operation and the next element.
     * @param <T> Type of nonempty {@code value} in {@link Maybe} object.
     * @param <U> Type of returned value from the fold {@code operation}.
     * @return the final result of of the fold operation.
     */
    public static <T,U> U fold(final Iterable<Maybe<T>> values,U identity,final BiFunction<U,? super T,U> accumulator) {
        Arguments.requireNonNull("Requires values,identity and accumulator",identity,values,accumulator);
        U result = identity;
        for (Maybe<T> value : values) {
            if (value.isPresent())
                result = accumulator.apply(result,value.get());
        }
        return result;
    }

    /**
     * Determines whether {@code this} contains the {@code element}.
     * <p>
     * @param element with which to test equality with this {@link Maybe} {@code
     * value}
     * @return true when {@code element} equals the encapsulated {@code value}.
     */
    public boolean contains(final T element) {
        T value = value();
        return value != null && value.equals(element);
    }

    /**
     * Returns {@code true} when {@code this} is nonempty and the {@code
     * predicate} function applied returns {@code true}.
     * <p>
     * If {@code this} is empty, the {@code predicate} function is NOT applied.
     *
     * @param predicate function with which to apply to the {@code Maybe}
     *                  value.
     * @return true when {@code predicate} test returns {@code true}.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public boolean exists(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        T value = value();
        return value != null && predicate.test(value);
    }

    /**
     * Returns {@code true} when {@code this} is {@code empty} or the {@code
     * predicate} function returns {@code true}.
     * <p>
     * The {@code predicate} is not applied if {@code this} is {@code empty}, but
     * {@code true} is returned. However, if {@code this} is nonempty, then the
     * {@code predicate} is applied and the resultant boolean {@code result} is
     * returned.
     *
     * @param predicate function to apply when the {@code this} is nonempty.
     * @return true when {@code this} is empty; or resultant value of the
     * {@code predicate} function.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public boolean forAll(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        T value = value();
        return value == null || predicate.test(value);
    }

    /**
     * Returns {@code this} {@link Maybe} object if the {@code value} is nonempty
     * and satisfies the {@code predicate} function.
     * <p>
     * If {@code this} is {@code empty}, the {@code predicate} function is NOT
     * applied and the {@link Maybe#empty()} is returned.
     *
     * @param predicate function to apply when {@code this} is nonempty.
     * @return {@code Maybe} object that agrees/or meets the {@code predicate's}
     * test if {@code this} is nonempty.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Maybe<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        T value = value();
        return value != null
                ? delegate.filter(predicate).isPresent()
                ? this : Maybe.empty()
                : Maybe.empty();
    }

    /**
     * Returns {@code this} {@link Maybe} object if the {@code value} is nonempty
     * and does NOT satisfy the {@code predicate} function.
     * <p>
     * If {@code this} is {@code empty}, the {@code predicate} function is NOT
     * applied and the {@link Maybe#empty()} is returned.
     *
     * @param predicate function to apply when {@code this} is nonempty.
     * @return {@code Maybe} object that agrees/or meets the {@code predicate's}
     * test if {@code this} is nonempty.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Maybe<T> filterNot(final Predicate<? super T> predicate) {
        Maybe<T> result = filter(predicate);
        return result.isPresent() ? Maybe.empty() : this;
    }

    /**
     * Transforms the {@link Maybe} value with the {@code function} when {@code
     * this} is nonempty.
     *
     * @param mapper function with which to perform the transformation.
     * @param <U> Type of transformed value.
     * @return transformed {@link Maybe} object.
     */
    public <U> Maybe<U> flatMap(final Function<? super T,? extends Maybe<U>> mapper) {
        Objects.requireNonNull(mapper);
        T value = value();
        return value != null ? Objects.requireNonNull(mapper.apply(value)) : Maybe.empty();
    }

    /**
     * Flatten and return internal {@link Maybe} {@code value}, if possible.
     * <p>
     * If {@code this} contains a {@link Maybe} value, it is returned, otherwise
     * {@code this} is returned.
     * <p>
     * @param <U> Type of value within {@code nested} {@link Maybe} object.
     * @return flattened {@link Maybe} object.
     */
    public <U> Maybe<U> flatten() {
        T value = value();
        return value instanceof Maybe ? Generics.unchecked(value) : Generics.unchecked(this);
    }

    /**
     * Returns result of {@code function} when {@code this} is nonempty, otherwise
     * {@code emptyValue} is returned if {@code value} is {@code empty}.
     *
     * @param function to perform operation if the {@code value} is {@code nonempty}.
     * @param emptyValue to use if {@code this} is {@code empty}
     * @param <U> Type of returned value from {@code function}.
     * @return results of {@code function} is {@code this} is nonempty, or
     * {@code emptyValue}
     */
    public <U> U fold(final U emptyValue, final Function<? super T,? extends U> function) {
        Objects.requireNonNull(function);
        return fold(Collections.singletonList(this),emptyValue,(a,b) -> function.apply(b));
    }

    /**
     * @return {@code value} if available, otherwise throws {@link
     * NoSuchMethodException}.
     *
     * @throws NoSuchElementException if {@code value} is unavailable.
     */
    public T get() {
        if (delegate.isPresent())
           return delegate.get();
        else
            throw new NoSuchElementException();
    }

    /**
     * Returns {@code value} of {@code this} object if available, otherwise the
     * {@code other} value is returned.
     *
     * @deprecated Consider using the {@link this#orElse(Object)} instead.
     */
    @Deprecated
    public T getOrElse(final T other) {
        return orElse(other);
    }

    /**
     * If {@code this} is nonempty, the {@code action} function is performed.
     *
     * @param action function to execute if {@code this} is nonempty.
     * @throws NullPointerException if {@code action} is null.
     */
    public void ifPresent(final Consumer<? super T> action) {
        Objects.requireNonNull(action);
        delegate.ifPresent(action);
    }

    /**
     * If {@code this} is nonempty, the {@code action} is performed, otherwise
     * the {@code elseAction} is executed instead.
     *
     * @param action function to perform if {@code this} is nonempty.
     * @param elseAction to perform if {@code this} is empty.
     * @throws NullPointerException if either {@code action} or {@code elseAction}
     * is null.
     */
    public void ifPresentOrElse(final Consumer<? super T> action, Runnable elseAction) {
        Arguments.requireNonNull(action,elseAction);
        T value = value();
        if (value == null)
            elseAction.run();
        else
            action.accept(value);
    }

    /**
     * @return true if {@code this} {@code value} is null.
     */
    public boolean isEmpty() {
        return !delegate.isPresent();
    }

    /**
     * If {@code this} is nonempty, transforms the {@code value} via the {@code
     * mapper} function.
     * <p>
     * If {@code this} is empty, {@link Maybe#empty()} is returned.
     *
     * @param mapper function to apply the transformation, if {@code value} is
     *               nonempty.
     * @param <U> Type of transformed {@value}
     * @return Maybe object of transformed {@code value}.
     * @throws NullPointerException if {@code mapper} is null.
     */
    public <U> Maybe<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);

        Optional<U> result = delegate.map(mapper);
        return result.map(Maybe::of).orElseGet(Maybe::empty);
    }

    /**
     * Returns {@code this} if nonempty, or perform {@code supplier} function to
     * derive {@link Maybe} alternative.
     *
     * @param supplier function to provide alternative {@link Maybe} object if
     *                 {@code this} is empty.
     * @return this if {@code this} nonempty, or resultant value of {@code supplier}
     * function.
     * @throws NullPointerException if {@code supplier} is {@code null} or resultant
     * {@link Maybe} returned from {@code supplier} function.
     */
    public Maybe<T> or (final Supplier<? extends Maybe<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        T value = value();
        return value != null ? this : Objects.requireNonNull(Generics.unchecked(supplier.get()));
    }

    /**
     * Returns {@code value} of {@code this} object if available, otherwise the
     * {@code other} value is returned.
     *
     * @param other the value is returned when {@code this} is empty.
     * @return value of {@code this} if nonempty, otherwise {@code other} is
     * returned.
     */
    public T orElse(final T other) {
       return delegate.orElse(other);
    }

    /**
     * Returns {@code value} of {@code this} object if available, otherwise the
     * {@code supplier} value is returned.
     *
     * @param supplier of the value is returned when {@code this} is empty.
     * @return value of {@code this} if nonempty, otherwise the {@code supplier}
     * value is returned.
     */
    public T orElseGet(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier);
        return delegate.orElseGet(supplier);
    }

    /**
     * Returns {@code value} of {@code this} object if available, otherwise a
     * {@link NoSuchElementException} exception is thrown.
     *
     * @return value of {@code this} if nonempty, otherwise the {@link
     * NoSuchElementException} object is thrown.
     * @throws NoSuchElementException if {@code this} is empty.
     */
    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns {@code value} of {@code this} object if available, otherwise an
     * {@code exception} is thrown supplied by the {@code exSupplier}.
     *
     * @return value of {@code this} if nonempty, otherwise an {@code exception}
     * is thrown supplied from the {@code exSupplier} function.
     * @param <E> Type of exception thrown in the event of {@code this} being
     *           empty.
     * @throws E is thrown when {@code this} is empty.
     */
    public <E extends Throwable> T orElseThrow(final Supplier<? extends E> exSupplier) throws E {
        return delegate.orElseThrow(exSupplier);
    }

    /**
     * @return a {@link Stream} object containing the {@code value} if {@code this}
     * is nonempty, otherwise an empty {@code Stream} is returned.
     */
    public Stream<T> stream() {
        return fold(Stream.of(),Stream::of);
    }

    /**
     * Returns an immutable list of containing {@code this} nonempty, {@code
     * value}.
     * <p>
     * If {@code this} is empty, and immutable empty {@link List} object is
     * returned.
     *
     * @return a {@link List} object containing a {@code value} from {@code
     * this} object, if available. Otherwise, an {@code empty} {@code List} is
     * returned.
     */
    public List<T> toList() {
        return Collections.unmodifiableList(fold(Collections.singletonList(this),Collections.emptyList(),
                (a,b) -> Collections.singletonList(b)));
    }

    /**
     * Returns {@link Map} containing {@code this} value if nonempty, otherwise
     * an empty {@code Map} collection is returned.
     *
     * @param keyMapper function to derive unique key with which insert the
     * {@code value}
     * @param <K>   Type of {@code map} key
     * @return a map containing {@code this} nonempty value, or an {@code empty}
     * map.
     */
    public <K> Map<K,T> toMap(final Function<? super T,? extends K> keyMapper) {
        Arguments.requireNonNull(keyMapper);
        T value = value();
        return Collections.unmodifiableMap(fold(Collections.singletonList(this),Collections.emptyMap(),
                (a,b) -> Collections.singletonMap(keyMapper.apply(b),value)));
    }

    /**
     * @return a string that represents the current state of {@code this} object.
     */
    public String toString() {
        T value = value();
        return value == null ? "Maybe[isEmpty]" : String.format("Maybe[%s]", value);
    }

    /**
     * @return true if {@code this} is nonempty.
     */
    public boolean isPresent() {
        return delegate.isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    /**
     * Constructs an instance of this {@link Maybe} object containing an empty
     * {@code value}.
     */
    private Maybe() {
        delegate = Optional.empty();
    }

    /**
     * Constructs an instance of this {@link Maybe} object containing a nonempty
     * {@code value}.
     * <p>
     * @throws NullPointerException if value is {@code null}
     */
    private Maybe(T value) {
        this.delegate = Optional.of(Objects.requireNonNull(value));
    }

    private T value() {
        return delegate.orElse(null);
    }
}
