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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * All {@link Eval} implementations inherit from this class.
 * <p>
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
public abstract class AbstractEval<T> implements Eval<T> {
    T value;

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
        return value;
    }

    /**
     * Constructs this {@link Eval} object with a {@code value}.
     * <p>
     * Depending on the {@code strategy}, the {@code value} maybe {@code null},
     * indicating that the {@code value} will be deferred at the point of use
     * and not before.
     * <p>
     * @param value to be backed by this {@link Eval} implementation.
     */
    public AbstractEval(final T value) {
        this.value = value;
    }

    /**
     * Creates a new instance of this {@code Eval} implementation, boxing the
     * new {@code value}.
     *
     * @param value to be {@code boxed}.
     * @param <U> Type of value to be boxed.
     * @return a new {@link Eval} instance with the new {@code value}.
     */
    protected abstract <U> AbstractEval<U> boxEval(final U value);

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
    public final Maybe<Eval<T>> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        return predicate.test(value()) ? Maybe.of(this) : Maybe.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <U> Eval<U> flatMap(final Function<? super T,? extends Eval<U>> mapper) {
        Objects.requireNonNull(mapper,"Expected flatMap function");
        return mapper.apply(value());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <U> AbstractEval<U> map(final Function<? super T,? extends U> mapper) {
        Objects.requireNonNull(mapper,"Expected map function");
        return boxEval(mapper.apply(value()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <U> AbstractEval<U> mapFn(final Function<? super T,? extends Recursion<? extends U>> mapper) {
        Objects.requireNonNull(mapper,"Expected map function");
        return boxEval(mapper.apply(value()).result());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractEval<T> peek(final Consumer<? super T> consumer) {
        AbstractEval<T> result = (AbstractEval<T>) Eval.super.peek(consumer);
        consumer.accept(value);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> toList() {
        return Collections.singletonList(value());
    }

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
}
