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

import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code applicative functor} supports the ability to allow for functorial
 * computations to be sequenced as opposed to strict {@code functors}.
 * <p>
 * The default implementation will suffice for most {@code applicable functor}
 * types, the only requirement is the appropriate type conversion having called
 * the {@link #apply} method from derived classes. In situations where a more
 * appropriate behaviour required, override the default implementation of
 * {@link #apply} method while observing the {@code Applicative Functor Laws}
 *
 * @param <T> Type of this {@code applicable container}
 */
public abstract class Applicative<T> implements Functor<T> {

    /**
     * Returns a new {@code applicable functor} containing the {@code value}.
     *
     * @param value to be contained in the {@code applicable} container.
     * @param <U> Type of value.
     * @return new {@code applicable} container.
     */
    protected abstract <U> Applicative<U> pure(final U value);

    /**
     * {@inheritDoc}
     * @throws NullPointerException if mapper is null.
     */
    public <R> Applicative<R> map(final Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        return pure(mapper.apply(get()));
    }

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
    protected <R> Applicative<R> apply(final Applicative<Function<? super T,? extends R>> applicative)  {
        Objects.requireNonNull(applicative);
        Function<? super T,? extends R> f = applicative.get();
        return pure(f.apply(get()));
    }
}
