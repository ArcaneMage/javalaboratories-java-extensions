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

import org.javalaboratories.core.concurrency.PrimaryAction;
import org.javalaboratories.core.concurrency.Promise;
import org.javalaboratories.core.concurrency.Promises;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * There are multiple evaluation strategies supported by their respective
 * implementations. The following strategies are supported:
 * <ol>
 *     <li>Eager - the evaluation is immediate and the {@code value} is available
 *     for processing</li>
 *     <p>
 *     <li>Later - the evaluation of the {@code value} deferred until it is
 *     required for use. The evaluation of the {@code value} is performed once
 *     and cached.</li>
 *     <p>
 *     <li>Always - the {@code value} is always evaluated just before use and
 *     never cached.</li>
 * </ol>
 *
 * @param <T> Type of evaluated value encapsulated with in {@link Eval}.
 */
public interface Eval<T> extends Functor<T>, Iterable<T>, Serializable {

    /**
     * Provides an implementation of the {@code Always} strategy.
     * <p>
     * The evaluation is <i>always</i> performed just before use and never cached.
     *
     * @param supplier function to compute evaluation of {@code value}.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Always} strategy implementation.
     */
    static <T> Eval<T> always(final Supplier<T> supplier) { return new Always<>(supplier);}

    /**
     * Provides an implementation of the {@code Eager} strategy.
     * <p>
     * The evaluation is immediate and ready for use.
     *
     * @param value underlying value of the {@link Eval} object.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Eager} strategy implementation.
     */
    static <T> Eval<T> eager(final T value) { return new Always<>(() -> value);}

    /**
     * Provides an implementation of the {@code Later} strategy.
     * <p>
     * The evaluation is performed once just before use and cached.
     *
     * @param supplier function to compute evaluation of {@code value}.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Later} strategy implementation.
     */
    static <T> Eval<T> later(final Supplier<T> supplier) { return new Later<>(supplier);}


    /**
     * Provides an implementation of the {@code PromiseLater} strategy.
     * <p>
     * The evaluation is performed asynchronously and once just before use and
     * cached.
     *
     * @param supplier function to compute evaluation of {@code value}.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Later} strategy implementation.
     */
    static <T> Eval<T> promiseLater(final Supplier<T> supplier) {
        return new PromiseLater<>(supplier);
    }

    /**
     * Returns {@code this} {@link Maybe} object that satisfies the {@code
     * predicate} function.
     *
     * @param predicate function to apply test.
     * @return {@code Maybe} object that agrees/or meets the {@code predicate's}
     * test.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    Maybe<Eval<T>> filter(final Predicate<? super T> predicate);

    /**
     * Returns {@code this} {@link Maybe} object that does NOT satisfy the
     * {@code predicate} function.
     *
     * @param predicate function to apply test.
     * @return {@code Maybe} object that agrees/or meets the {@code predicate's}
     * test if {@code this} is nonempty.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    default Maybe<Eval<T>> filterNot(final Predicate<? super T> predicate) {
        Maybe<Eval<T>> maybe = filter(predicate);
        return maybe.isPresent() ? Maybe.empty() : Maybe.of(this);
    }

    /**
     * Transforms the {@link Eval} value with the {@code mapper}.
     *
     * @param <U> Type of transformed value.
     * @param mapper function with which to perform the transformation.
     * @return transformed {@link Eval} object.
     */
    <U> Eval<U> flatMap(final Function<? super T,? extends Eval<U>> mapper);

    /**
     * @return encapsulated {@code value} from {@link Eval}. Some implementations
     * evaluate the {@code value} lazily.
     */
    T get();

    /**
     * {@inheritDoc}
     */
    @Override
    default Iterator<T> iterator() {
        return toList().iterator();
    }

    /**
     * {@inheritDoc}
     */
    <U> Eval<U> map(final Function<? super T,? extends U> mapper);

    /**
     * Transforms the {@link Eval} value with the {@code mapper} function with
     * the assistance of a recursive function.
     * <p>
     * The evaluated {@code value} is passed to the recursive function for
     * processing and the resultant {@code value} encapsulated within the
     * returned {@link Eval}.
     *
     * @param <U> Type of transformed value.
     * @param mapper function with which to perform the transformation.
     * @return transformed {@link Eval} object.
     *
     * @see Recursion
     */
    <U> Eval<U> mapFn(final Function<? super T,? extends Recursion<U>> mapper);

    /**
     * {@inheritDoc}
     * <p>
     * This operation does NOT evaluate the {@code value}, but allows read access
     * to the current state of it.
     */
    default Eval<T> peek(final Consumer<? super T> consumer) {
        return (Eval<T>) Functor.super.peek(consumer);
    }

    /**
     * Reserve the evaluated value for future use in an {@link Eval}.
     * <p>
     * Consider an {@code Always} implementation constantly deriving {@code
     * values} that are expensive in terms of processing time and/or resources.
     * In such circumstances, use this method to cache the resultant {@code
     * value}.
     *
     * @return an {@link Eval} with the "cached" value.
     */
    default Eval <T> reserve() {
        return this;
    }

    /**
     * Returns an immutable list of containing {@code this} {@code value}.
     * <p>
     *
     * @return a {@link List} object containing a {@code value} from {@code
     * this} object.
     */
    List<T> toList();

    /**
     * @return {@link Maybe} object with encapsulated {@code value}. Some
     * implementations evaluate the {@code value} lazily.
     */
    Maybe<T> toMaybe();

    /**
     * Implements the {@code Always} strategy for the {@link Eval} interface.
     * <p>
     * The {@code value} is always evaluated just before use and never cached.
     *
     * @param <T> Type of lazily computed {@code value}.
     */
    class Always<T> implements Eval<T> {
        private final Supplier<T> source;
        T value;

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         *
         * @param source function that computes the {@code value}.
         */
        public Always(final Supplier<T> source) {
            Objects.requireNonNull(source,"Supplier required");
            this.source = source;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Maybe<Eval<T>> filter(Predicate<? super T> predicate) {
            return Stream.of(value())
                    .filter(predicate)
                    .findFirst()
                    .map(value -> Maybe.of(Eval.eager(value)))
                    .orElse(Maybe.empty());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Eval<U> flatMap(Function<? super T, ? extends Eval<U>> mapper) {
            Objects.requireNonNull(mapper,"Expected mapping function");
            return mapper.apply(value());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get() {
            return value();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Eval<U> map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper,"Expected mapping function");
            return flatMap(value -> Eval.always(() -> mapper.apply(value)));
        }

        /**
         * {@inheritDoc}
         * @deprecated considering alternative design to internalise this behaviour.
         */
        @Override
        @Deprecated
        public <U> Eval<U> mapFn(Function<? super T, ? extends Recursion<U>> mapper) {
            return Eval.eager(mapper.apply(value()).result());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Eval<T> peek(final Consumer<? super T> consumer) {
            Objects.requireNonNull(consumer,"Expected consumer function");
            consumer.accept(value());
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Eval<T> reserve() {
            return flatMap(value -> Eval.later(() -> value()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<T> toList() {
            return Collections.singletonList(value());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Maybe<T> toMaybe() {
            return Maybe.ofNullable(value());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            String value = this.value == null ? "unset" : String.valueOf(this.value);
            return String.format("%s[%s]",getClass().getSimpleName(),value);
        }

        /**
         * Override in derived classes to evaluate or compute the value by other
         * means.
         * <p>
         * Because this method is used by core operations like {@link Eval#flatMap(Function)},
         * {@link Eval#map(Function)} and others, this method must always return a
         * {@code value}.
         * <p>
         * The default implementation is to immediately return a {@code value}.
         *
         * @return internal {@code value} encapsulated in this {@link Eval} object.
         */
        protected T value() {
            value = source.get();
            return value;
        }
    }

    /**
     * Implements the {@code Later} strategy for the {@link Eval} interface.
     * <p>
     * The evaluation of the {@code value} deferred until it is required for
     * use. The evaluation of the {@code value} is performed once and cached.
     *
     * @param <T> Type of lazily computed {@code value}.
     */
    class Later<T> extends Always<T> {
        /**
         * Constructs implementation of {@link Eval} with the {@code Later}
         * strategy.
         *
         * @param source function that computes the {@code value}.
         */
        private Later(final Supplier<T> source) {
            super(source);
        }
        /**
         * {@inheritDoc}
         * <p>
         * This implementation evaluates the {@code value} lazily and once only
         * and caches the resultant {@code value}.
         */
        @Override
        protected T value() {
            if (value == null)
                value = super.value();
            return value;
        }
    }

    /**
     * Implements the {@code PromiseLater} strategy for the {@link Eval}
     * interface.
     * <p>
     * The evaluation of the {@code value} occurs asynchronously. The evaluation
     * of the {@code value} is performed once and cached.
     *
     * @param <T> Type of lazily computed {@code value}.
     */
    class PromiseLater<T> extends Later<T> {
        private final Promise<T> promise;
        private Exception exception;

        /**
         * Constructs implementation of {@link Eval} with the {@code Later}
         * strategy.
         *
         * @param source function that computes the {@code value}.
         */
        private PromiseLater(final Supplier<T> source) {
            super(source);
            promise = Promises.newPromise(PrimaryAction.of(source,this::handle));
        }
        /**
         * {@inheritDoc}
         * <p>
         * This implementation evaluates the {@code value} by blocking for the
         * asynchronous process to complete -- evaluation only occurs once.
         */
        @Override
        protected T value() {
            if (value == null)
                value = promise.getResult().orElse(null);
            return value;
        }

        /**
         * @return true to indicate asynchronous process is complete whether
         * successfully or unsuccessfully.
         */
        public boolean isComplete() {
            return promise.getState() != Promise.States.PENDING;
        }

        /**
         * @return true to indicate asynchronous process has encountered an
         * exception.
         */
        public boolean isRejected() {
            return promise.getState() == Promise.States.REJECTED;
        }

        /**
         * @return true to indicate asynchronous process has completed
         * successfully.
         */
        public boolean isFulfilled() {
            return promise.getState() == Promise.States.FULFILLED;
        }

        /**
         * @return the exception thrown in the asynchronous process as a {@link
         * Maybe} object.
         */
        public Maybe<Exception> getException() {
            return Maybe.ofNullable(exception);
        }

        private void handle(final T value, final Throwable e) {
            exception = (Exception) e;
        }
    }
}
