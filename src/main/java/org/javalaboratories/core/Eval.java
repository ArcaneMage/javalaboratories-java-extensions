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

import org.javalaboratories.core.concurrency.AsyncEval;
import org.javalaboratories.core.concurrency.PrimaryAction;
import org.javalaboratories.core.concurrency.Promise;
import org.javalaboratories.core.concurrency.Promises;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;

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
     * Provides an implementation of the {@code Always} strategy.
     * <p>
     * The evaluation is <i>always</i> performed just before use and never cached.
     *
     * @param trampoline function to compute evaluation of {@code value}
     *                   recursively with stack-safety.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Always} strategy implementation.
     */
    static <T> Eval<T> alwaysRecursive(final Trampoline<T> trampoline) { return new Always<>(trampoline);}

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
    static <T> AsyncEval<T> asyncLater(final Supplier<T> supplier) {
        return new AsyncLater<>(supplier);
    }

    /**
     * A method to provide the ability to inspect the state of {@code Consumer}
     * functions.
     * <p>
     * Designed to be used with the likes of {@link Collection#forEach} methods of
     * or any {@link Consumer} function.
     *
     * @param consumer function provides an {@link Eval} object encapsulating
     *                 current {@code value}.
     * @param <T> Type of {@code value}
     * @return a {@link Consumer} function with augmented {@code cpeek} behaviour
     * to enable {@code value} inspection.
     */
    static <T> Consumer<T> cpeek(final Consumer<? super Eval<T>> consumer) {
        return cpeek(value -> {consumer.accept(value); return null;},null);
    }

    /**
     * A method to provide the ability to inspect the state of a {@code Consumer}
     * and perform an {@code action} as result of that state.
     * <p>
     * Designed to be used with the likes of {@link Collection#forEach} methods of
     * or any {@link Consumer} function. The {@code action} will always be
     * executed even in the event of an exception, but there is no {@code
     * action}, then it will be ignored.
     * <p>
     * This is particularly useful to both inspect and perform an action based
     * on a state without any side-effects, which is in contrast to the
     * {@link org.javalaboratories.util.Holder} class. Example below illustrates
     * this:
     * <pre>
     *     {@code
     *          Maybe<Integer> maybe = Maybe.of(100);
     *          ...
     *          ...
     *          maybe.forEach(Eval.cpeek(value -> value.map(v -> v > 90),
     *             value -> assertTrue(value.get());
     *          ...
     *          ...
     *     }
     * </pre>
     *
     * @param function function provides an {@link Eval} object encapsulating
     *                 current {@code value}.
     * @param <T> Type of {@code value}
     * @param <U> Type of {@code value} returned from {@code function}
     * @return a {@link Consumer} function with augmented {@code cpeek} behaviour
     * to enable {@code value} inspection.
     */
    static <T,U> Consumer<T> cpeek(final Function<? super Eval<T>,? extends Eval<U>> function,
                                   final Consumer<? super Eval<U>> action) {
        Function<? super Eval<T>,? extends Eval<U>> fn = Objects.requireNonNull(function,"Expected function");
        return value -> {
            Eval<U> eval = null;
            try {
                eval = fn.apply(Eval.eager(value));
            } finally {
                if (action != null)
                    action.accept(eval);
            }
        };
    }

    /**
     * Provides an implementation of the {@code Eager} strategy.
     * <p>
     * The evaluation is immediate and ready for use.
     *
     * @param value underlying value of the {@link Eval} object.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Eager} strategy implementation.
     */
    static <T> Eval<T> eager(final T value) { return Eval.always(() -> value);}

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
     * Provides an implementation of the {@code Later} strategy.
     * <p>
     * The evaluation is <i>always</i> performed just before use and never cached.
     *
     * @param trampoline function to compute evaluation of {@code value}
     *                   recursively with stack-safety.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Always} strategy implementation.
     */
    static <T> Eval<T> laterRecursive(final Trampoline<T> trampoline) { return new Later<>(trampoline);}

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
     * {@inheritDoc}
     * <p>
     * This operation does NOT evaluate the {@code value}, but allows read access
     * to the current state of it.
     */
    Eval<T> peek(final Consumer<? super T> consumer);

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
    Eval <T> reserve();

    /**
     * Returns an immutable list of containing {@code this} {@code value}.
     * <p>
     *
     * @return a {@link List} object containing a {@code value} from {@code
     * this} object.
     */
    default List<T> toList() {
        return Collections.singletonList(get());
    }

    /**
     * @return {@link Maybe} object with encapsulated {@code value}. Some
     * implementations evaluate the {@code value} lazily.
     */
    default Maybe<T> toMaybe() {
        return Maybe.ofEval(this);
    }

    /**
     * Implements the {@code Always} strategy for the {@link Eval} interface.
     * <p>
     * The {@code value} is always evaluated just before use and never cached.
     *
     * @param <T> Type of lazily computed {@code value}.
     */
    class Always<T> implements Eval<T> {
        private final Trampoline<T> evaluate;
        T value;

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         * <p>
         * Accepts {@code trampoline} function, a function that is to be
         * recursively called lazily with stack-safety.
         *
         * @param trampoline function that computes the {@code value}.
         * @see Trampoline
         */
        public Always(final Trampoline<T> trampoline) {
            Objects.requireNonNull(trampoline,"Expected recursive function");
            evaluate = trampoline;
        }

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         */
        public Always(final Supplier<T> function) {
            Objects.requireNonNull(function,"Expected supplier");
            this.evaluate = Trampoline.more(() -> Trampoline.finish(function.get()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Maybe<Eval<T>> filter(final Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate,"Expect predicate function");
            T value = value();
            return predicate.test(value) ? Maybe.of(this) : Maybe.empty();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> Eval<U> flatMap(final Function<? super T, ? extends Eval<U>> mapper) {
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
        public <U> Eval<U> map(final Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper,"Expected mapping function");
            return flatMap(value -> Eval.eager(mapper.apply(value)));
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
            return flatMap(Eval::eager);
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
         * The default implementation is to evaluate the {@code value} by invoking
         * the {@link Trampoline} object.
         *
         * @return internal {@code value} encapsulated in this {@link Eval} object.
         * @throws IllegalStateException when {@link Trampoline} fails to {@code
         * conclude}.
         */
        protected T value() {
            value = evaluate.result();
            if (value instanceof Trampoline) {
                throw new IllegalStateException("Trampoline unresolvable -- " +
                        "review recursion logic");
            }
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
         * <p>
         * Accepts {@code trampoline} function, a function that is to be
         * recursively called lazily with stack-safety.
         *
         * @param trampoline function that computes the {@code value}.
         * @see Trampoline
         */
        public Later(final Trampoline<T> trampoline) {
            super(trampoline);
        }

        /**
         * Constructs implementation of {@link Eval} with the {@code Later}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         */
        private Later(final Supplier<T> function) {
            super(function);
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation evaluates the {@code value} lazily once and then
         * caches the resultant {@code value}.
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
    class AsyncLater<T> extends Later<T> implements AsyncEval<T> {
        private final Promise<T> promise;
        private Exception exception;

        /**
         * Constructs implementation of {@link Eval} with the {@code Later}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         */
        private AsyncLater(final Supplier<T> function) {
            super(function);
            promise = Promises.newPromise(PrimaryAction.of(function,this::handle));
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation evaluates the {@code value} by blocking for the
         * asynchronous process to complete -- evaluation only occurs once.
         *
         * @throws NoSuchElementException evaluation failure in asynchronous task.
         */
        @Override
        protected T value() {
            if (value == null) {
                value = promise.getResult().orElse(null);
                if (isRejected()) {
                    throw new NoSuchElementException("Evaluation not possible due" +
                            " to asynchronous exception");
                }
            }
            return value;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isComplete() {
            return promise.getState() != Promise.States.PENDING;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isFulfilled() {
            return promise.getState() == Promise.States.FULFILLED;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isRejected() {
            return promise.getState() == Promise.States.REJECTED;
        }

        /**
         * {@inheritDoc}
         */
        public Maybe<Exception> getException() {
            return Maybe.ofNullable(exception);
        }

        private void handle(final T value, final Throwable e) {
            exception = (Exception) e;
        }
    }
}
