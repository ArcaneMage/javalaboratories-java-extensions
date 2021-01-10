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

import org.javalaboratories.util.Generics;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
public interface Eval<T> extends Functor<T>, Iterable<T> {

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
    <U> Eval<U> mapFn(final Function<? super T,? extends Recursion<? extends U>> mapper);

    /**
     * @return encapsulated {@code value} from {@link Eval}. Some implementations
     * evaluate the {@code value} lazily.
     */
    T get();

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
    class Always<T> extends AbstractEval<T> {
        private final Supplier<T> source;

        /**
         * Constructs implementation of {@link Eval} with the {@code Always}
         * strategy.
         *
         * @param source function that computes the {@code value}.
         */
        private Always(final Supplier<T> source) {
            super(null);
            Objects.requireNonNull(source,"Supplier required");
            this.source = source;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Always<U> boxEval(U value) {
            Eval<T> result = Eval.always(source);
            ((Always<U>) result).value = value;
            return (Always<U>) result;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected T value() {
            return source.get();
        }
        Supplier<T> source() {
            return source;
        }
    }

    /**
     * Implements the {@code Eager} strategy for the {@link Eval} interface.
     * <p>
     * The {@code value} is always evaluated just before use and never cached.
     *
     * @param <T> Type of {@code value} to be backed by this {@link Eval}
     *           implementation.
     */
    class Eager<T> extends AbstractEval<T>  {
        private Eager(T value) {
            super(value);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> AbstractEval<U> boxEval(final U value) {
            return Generics.unchecked(Eval.eager(value));
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
        private Later(final Supplier<T> source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Later<U> boxEval(U value) {
            Eval<T> result = Eval.later(source());
            ((Later<U>) result).value = value;
            return (Later<U>) result;
        }
        /**
         * {@inheritDoc}
         * <p>
         * This implementation evaluates the {@code value} lazily and once only
         * and caches the resultant {@code value}.
         */
        protected T value() {
            if (value == null)
                value = super.value();
            return value;
        }
    }
}
