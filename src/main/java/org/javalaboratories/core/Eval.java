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
 * <p>
 * This implementation was inspired by the Scala's Cat implementation
 * of {@code Eval}.
 *
 * @param <T> Type of evaluated value encapsulated with in {@link Eval}.
 */
public interface Eval<T> extends Monad<T>, Iterable<T>, Serializable {

    /**
     * Evaluate object for {@code FALSE} Boolean value
     */
    Eval<Boolean> FALSE = Eval.eager(false);
    /**
     * Evaluate object for {@code TRUE} Boolean value
     */
    Eval<Boolean> TRUE = Eval.eager(true);
    /**
     * Evaluate object for {@code ZERO} Integer value
     */
    Eval<Integer> ZERO = Eval.eager(0);
    /**
     * Evaluate object for {@code ONE} Integer value
     */
    Eval<Integer> ONE = Eval.eager(1);
    /**
     * Evaluate object for {@code EMPTY} String value
     */
    Eval<String> EMPTY = Eval.eager("");

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
        return cpeek(value -> true,consumer);
    }

    /**
     * A method to provide the ability to inspect the state of a {@code Consumer}
     * and perform an {@code action} as result of that state.
     * <p>
     * Designed to be used with the likes of {@link Collection#forEach} methods of
     * or any {@link Consumer} function. The {@code action} is executed only when
     * the {@code predicate} resolves to {@code true}.
     * <p>
     * This is particularly useful to both inspect and perform an action based
     * on a state without any side-effects, which is in contrast to the
     * {@link org.javalaboratories.util.Holder} class. The purity of the functions
     * is dependent on the implementation. Example below illustrates this:
     * <pre>
     *     {@code
     *          Maybe<Integer> maybe = Maybe.of(100);
     *          ...
     *          ...
     *          maybe.forEach(Eval.cpeek(value -> value.get() > 95,
     *             value -> assertEquals(100,value.get())));
     *          ...
     *          ...
     *     }
     * </pre>
     *
     * @param predicate function to evaluate {@link Eval}.
     * @param action to be executed when {@code predicate} evaluates to {@code
     * true}.
     * @param <T> Type of {@code value}
     * @return a {@link Consumer} function with augmented {@code cpeek} behaviour
     * to enable {@code value} inspection.
     */
    static <T> Consumer<T> cpeek(final Predicate<? super Eval<T>> predicate, final Consumer<? super Eval<T>> action) {
        Predicate<? super Eval<T>> p = Objects.requireNonNull(predicate,"Expected predicate");
        return value -> {
            Eval<T> eval;
            if (action != null && p.test(eval = Eval.eager(value)))
                action.accept(eval);
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
    static <T> Eval<T> eager(final T value) { return new Eager<>(value);}

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
        return filter(predicate.negate());
    }

    /**
     * Transforms the {@link Eval} value with the {@code mapper}.
     *
     * @param <U> Type of transformed value.
     * @param mapper function with which to perform the transformation.
     * @return transformed {@link Eval} object.
     */
    @Override
    default <U> Eval<U> flatMap(final Function<? super T,? extends Monad<U>> mapper) {
        return (Eval<U>) Monad.super.flatMap(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <U> Eval<U> flatten() {
        return (Eval<U>) Monad.super.flatten();
    }

    /**
     * @return encapsulated {@code value} from {@link Eval}. Some implementations
     * evaluate the {@code value} lazily.
     */
    T get();

    /**
     * {@inheritDoc}
     */
    @Override
    default T getOrElse(T other) {
        return get();
    }

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
    default Eval<T> peek(final Consumer<? super T> consumer) {
        return (Eval<T>) Monad.super.peek(consumer);
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
    Eval<T> reserve();

    /**
     * Invokes evaluation of the {@code value} and returns {@code this} with
     * resolved {@code value}.
     *
     * @return Eval object with resolved value.
     */
    default Eval<T> resolve() {
        get();
        return this;
    }

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
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    class Always<T> implements Eval<T> {
        final Object lock = new Object();
        private final Trampoline<T> evaluate;
        @EqualsAndHashCode.Include
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
        private Always(final Trampoline<T> trampoline) {
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
            return predicate.test(value()) ? Maybe.of(this) : Maybe.empty();
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
            return Eval.eager(mapper.apply(value()));
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
            String value;
            synchronized (lock) {
                value = this.value == null ? "unset" : String.valueOf(this.value);
            }
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
            synchronized(lock) {
                value = evaluate.result();
                if (value instanceof Trampoline) {
                    throw new IllegalStateException("Trampoline unresolvable -- " +
                            "review recursion logic");
                }
                return value;
            }
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
    @EqualsAndHashCode(callSuper = true)
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
        private Later(final Trampoline<T> trampoline) {
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
            synchronized(lock) {
                if (value == null)
                    value = super.value();
                return value;
            }
        }
    }

    /**
     * Implements the {@code Eager} strategy for the {@link Eval} interface.
     * <p>
     * The evaluation of the {@code value} is immediate and ready for use.
     *
     * @param <T> Type of {@code value}.
     */
    @EqualsAndHashCode(callSuper = true)
    final class Eager<T> extends Later<T> {
        private Eager(final T value) {
            super((Supplier<T>)() -> value);
            // Cache the value immediately
            // It is okay to call this public instance method: class is final.
            resolve();
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
    @EqualsAndHashCode(callSuper = true)
    final class AsyncLater<T> extends Later<T> implements AsyncEval<T> {
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
            Maybe<T> maybe = promise.getResult();
            if (isRejected()) {
                throw new NoSuchElementException("Evaluation not possible due" +
                        " to asynchronous exception");
            }
            synchronized(lock) {
                if (value == null) {
                    value = maybe.orElse(null);
                }
                return value;
            }
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
            synchronized (lock) {
                return Maybe.ofNullable(exception);
            }
        }

        private void handle(final T value, final Throwable e) {
            synchronized(lock) {
                exception = (Exception) e;
            }
        }
    }
}
