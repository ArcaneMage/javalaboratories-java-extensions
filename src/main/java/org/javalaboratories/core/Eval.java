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
import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.core.holders.Holder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * There are multiple evaluation strategies supported by their respective
 * implementations. The following strategies are supported:
 * <ol>
 *     <li>Eager - the evaluation is immediate and the {@code value} is available
 *     for processing</li>
 *     <li>Later - the evaluation of the {@code value} deferred until it is
 *     required for use. The evaluation of the {@code value} is performed once
 *     and cached.</li>
 *     <li>Always - the {@code value} is always evaluated just before use and
 *     never cached.</li>
 * </ol>
 * <p>
 * This implementation was inspired by the Scala's Cat implementation
 * of {@code Eval}.
 *
 * @param <T> Type of evaluated value encapsulated with in {@link Eval}.
 */
public abstract class Eval<T> extends CoreApplicative<T> implements Monad<T>, Exportable<T> {
    @Serial
    private static final long serialVersionUID = 1372673159914117700L;

    /**
     * Evaluate object for {@code FALSE} Boolean value
     */
    public static final Eval<Boolean> FALSE = Eval.eager(false);
    /**
     * Evaluate object for {@code TRUE} Boolean value
     */
    public static final  Eval<Boolean> TRUE = Eval.eager(true);
    /**
     * Evaluate object for {@code ZERO} Integer value
     */
    public static final  Eval<Integer> ZERO = Eval.eager(0);
    /**
     * Evaluate object for {@code ONE} Integer value
     */
    public static final  Eval<Integer> ONE = Eval.eager(1);
    /**
     * Evaluate object for {@code EMPTY} String value
     */
    public static final Eval<String> EMPTY = Eval.eager("");

    /**
     * Provides an implementation of the {@code Always} strategy.
     * <p>
     * The evaluation is <i>always</i> performed just before use and never cached.
     *
     * @param supplier function to compute evaluation of {@code value}.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Always} strategy implementation.
     */
    public static <T> Eval<T> always(final Supplier<T> supplier) { return new Always<>(supplier);}

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
    public static <T> Eval<T> alwaysRecursive(final Trampoline<T> trampoline) { return new Always<>(trampoline);}

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
    public static <T> Consumer<T> cpeek(final Consumer<? super Eval<T>> consumer) {
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
     * {@link Holder} class. The purity of the functions
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
    public static <T> Consumer<T> cpeek(final Predicate<? super Eval<T>> predicate, final Consumer<? super Eval<T>> action) {
        Predicate<? super Eval<T>> p = Objects.requireNonNull(predicate,"Expected predicate");
        return value -> {
            Eval<T> eval;
            if (action != null && p.test((eval = Eval.eager(value))))
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
    public static <T> Eval<T> eager(final T value) { return new Eager<>(value);}

    /**
     * Provides an implementation of the {@code Later} strategy.
     * <p>
     * The evaluation is performed once just before use and cached.
     *
     * @param supplier function to compute evaluation of {@code value}.
     * @param <T> Type of resultant {@code value}.
     * @return an {@code Later} strategy implementation.
     */
    public static <T> Eval<T> later(final Supplier<T> supplier) { return new Later<>(supplier);}

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
    public static <T> Eval<T> laterRecursive(final Trampoline<T> trampoline) { return new Later<>(trampoline);}

    /**
     * Provides the ability to perform a sequence functorial computations on
     * the {@code applicable functor} container.
     *
     * @param applicative to apply computation.
     * @param <R> Type of value transformed having applied the function.
     * @return a new applicative with resultant value having applied the
     * encapsulated function.
     * @throws NullPointerException if function is null;
     */
    public <R> Eval<R> apply(final Applicative<Function<? super T,? extends R>> applicative)  {
        return (Eval<R>) super.apply(applicative);
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
    public Maybe<Eval<T>> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate,"Expect predicate function");
        return predicate.test(value()) ? Maybe.of(this) : Maybe.empty();
    }

    /**
     * Returns {@code this} {@link Maybe} object that does NOT satisfy the
     * {@code predicate} function.
     *
     * @param predicate function to apply test.
     * @return {@code Maybe} object that agrees/or meets the {@code predicate's}
     * test if {@code this} is nonempty.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Maybe<Eval<T>> filterNot(final Predicate<? super T> predicate) {
        return filter(predicate.negate());
    }

    /**
     * Transforms the {@link Eval} value with the {@code mapper}.
     *
     * @param <U> Type of transformed value.
     * @param mapper function with which to perform the transformation.
     * @return transformed {@link Eval} object.
     *
     * TODO: Resolve trampoline behaviour to enable flatMap recursion
     */
    @Override
    public <U> Eval<U> flatMap(final Function<? super T,? extends Monad<U>> mapper) {
        return (Eval<U>) Monad.super.flatMap(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public<U> Eval<U> flatten() {
        return (Eval<U>) Monad.super.<U>flatten();
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
    public <U> Eval<U> map(final Function<? super T,? extends U> mapper) {
        return (Eval<U>) super.<U>map(mapper);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This operation does NOT evaluate the {@code value}, but allows read access
     * to the current state of it.
     */
    @Override
    public Eval<T> peek(final Consumer<? super T> consumer) {
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
    public abstract Eval<T> reserve();

    /**
     * Invokes evaluation of the {@code value} and returns {@code this} with
     * resolved {@code value}.
     *
     * @return Eval object with resolved value.
     */
    public Eval<T> resolve() {
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
    @Override
    public List<T> toList() {
        return Collections.singletonList(get());
    }

    /**
     * Returns an immutable {@link Map} that represents the current state
     * of this {@link Eval}.
     *
     * @param keyMapper supply key for the context {@code value}.
     * @param <K> type of key used to map {@code this} value.
     * @return an immutable Map.
     */
    @Override
    public <K> Map<K,T> toMap(final Function<? super T,? extends K> keyMapper) {
        Objects.requireNonNull(keyMapper,"Require keyMapper function");
        return Collections.unmodifiableMap(fold(Collections.emptyMap(),
                value -> Collections.singletonMap(keyMapper.apply(value),value)));
    }

    /**
     * @return {@link Maybe} object with encapsulated {@code value}. Some
     * implementations evaluate the {@code value} lazily.
     */
    public Maybe<T> toMaybe() {
        return Maybe.ofEval(this);
    }

    /**
     * Returns an immutable {@link Set} that represents the state of {@code this}
     * {@link Eval}.
     *
     * @return an immutable set object.
     */
    @Override
    public Set<T> toSet() {
        return Collections.unmodifiableSet(fold(Collections.emptySet(),Collections::singleton));
    }

    /**
     * Override in derived classes to evaluate or compute the value by other
     * means.
     * <p>
     * Because this method is used by core operations like {@link Eval#flatMap(Function)},
     * {@link Eval#map(Function)} and others, this method must always return a
     * {@code value}.
     *
     * @return internal {@code value} encapsulated in this {@link Eval} object.
     */
    protected abstract T value();

    /**
     * Implements the {@code Always} strategy for the {@link Eval} interface.
     * <p>
     * The {@code value} is always evaluated just before use and never cached.
     *
     * @param <T> Type of lazily computed {@code value}.
     */
    @EqualsAndHashCode(callSuper=false,onlyExplicitlyIncluded=true)
    public static class Always<T> extends Eval<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 518963023579340195L;

        transient final ReentrantLock lock = new ReentrantLock();
        private transient final Trampoline<T> evaluate;

        @EqualsAndHashCode.Include
        protected final EvalValue<T> value;

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         */
        public Always(final Supplier<T> function) {
            this(function, false);
        }

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         * @param caching set to {@code true} to cache evaluated result: behave
         *                  like {@code Later}.
         */
        Always(final Supplier<T> function, final boolean caching) {
            this(Trampoline.more(() -> Trampoline.finish(Objects.requireNonNull(function,"Expected supplier").get())), caching);
        }

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
            this(trampoline,false);
        }

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         * <p>
         * Accepts {@code trampoline} function, a function that is to be
         * recursively called lazily with stack-safety.
         *
         * @param trampoline function that computes the {@code value}.
         * @param caching set to {@code true} to cache evaluated result: behave
         *                  like {@code Later}.
         * @see Trampoline
         */
        Always(final Trampoline<T> trampoline, final boolean caching) {
            Arguments.requireNonNull("Expected both trampoline and value objects",trampoline, caching);
            evaluate = trampoline;
            this.value = new EvalValue<>(caching);
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
            return String.format("%s[%s]",getClass().getSimpleName(),value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Always<U> pure(final U value) {
            return (Always<U>) Eval.always(() -> value).resolve();
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
            lock.lock();
            try {
                T value = this.value.setGet(evaluate.result());
                if (value instanceof Trampoline) {
                    throw new IllegalStateException("Trampoline unresolvable -- " +
                            "review recursion logic");
                }
                return value;
            } finally {
                lock.unlock();
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
    @EqualsAndHashCode(callSuper=true)
    public static class Later<T> extends Always<T> {
        @Serial
        private static final long serialVersionUID = -8848701870767131627L;

        transient private final ReentrantLock lock = new ReentrantLock();

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
            super(trampoline,true);
        }

        /**
         * Constructs implementation of {@link Eval} with the {@code Later}
         * strategy.
         *
         * @param function function that computes the {@code value}.
         */
        public Later(final Supplier<T> function) {
            super(function,true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Later<U> pure(final U value) {
            return (Later<U>) Eval.later(() -> value).resolve();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected T value() {
            lock.lock();
            try {
                return this.value.isEmpty() ? super.value() : this.value.get();
            } finally {
                lock.unlock();
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
    @EqualsAndHashCode(callSuper=true)
    public static final class Eager<T> extends Later<T> {
        @Serial
        private static final long serialVersionUID = -4956876354953747651L;

        Eager(final T value) {
            super((Supplier<T>)() -> value);
            // Cache the value immediately
            // It is okay to call this public instance method: class is final.
            resolve();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Eager<U> pure(final U value) {
            return (Eager<U>) Eval.eager(value);
        }
    }
}

/**
 * Encapsulates {@code value} yet to be evaluated by the {@link Eval}
 * implementations.
 * <p>
 * This mutable object is thread-safe.
 * @param <E> Type of {@code value}
 */
@EqualsAndHashCode()
final class EvalValue<E> implements Serializable {
    @Serial
    private static final long serialVersionUID = -797325625285441119L;

    private E element;
    private final boolean caching;

    @EqualsAndHashCode.Exclude
    transient private final ReentrantLock lock;

    @EqualsAndHashCode.Exclude
    private final List<Consumer<E>> modes =
            Arrays.asList(
                    value -> {if (element == null) element = value;},
                    value -> element = value
            );
    /**
     * Constructs this {@code value}
     * <p>
     * @param caching set to {@code true} to enable "caching" (write once).
     */
    public EvalValue(boolean caching) {
        element = null;
        this.caching = caching;
        this.lock = new ReentrantLock();
    }

    /**
     * Sets this {@code value}
     */
    public E setGet(final E e) {
        lock.lock();
        try {
            modes.get(caching ? 0 : 1).accept(e);
            return element;
        } finally {
            lock.unlock();
        }
    }

    public E get() {
        lock.lock();
        try {
            return element;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return {@code true} if container is occupied.
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return element == null;
        } finally {
            lock.unlock();
        }
    }
    @Override
    public String toString() {
        lock.lock();
        try {
            return isEmpty() ? "unset" : String.valueOf(this.element);
        } finally {
            lock.unlock();
        }
    }
}
