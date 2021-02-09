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
 * A {@code monad} is a structure that supports the function programming
 * {@code bind} operator through the {@code flatMap} operation.
 * <p>
 * This is important in that it is the only mechanism allowed in the world of
 * functional programming the ability to transform the {@code value} within the
 * {@code monad container}. {@code flatMap} can transform {@code functions} that
 * return {@code monad} containers. Using a functor's {@code map} instead on a
 * function returning a {@code monad}, causes the resultant transformation to be
 * a nested {@code monad}, which is not always desirable. In such scenarios, use
 * the {@code flatMap} instead to perform the transformation.
 *
 * @param <T> Type of value within the {@code monad}.
 */
public interface Monad<T> extends Functor<T> {

    /**
     * Transforms the function's contained value to a new {@code monad}
     * containing the transformed value.
     * <p>
     * The returned {@code Monad} must be the same kind of type as the concrete
     * implementation but containing the transformed {@code value}. Failing to
     * meet this contract will cause a {@link ClassCastException} to be thrown.
     * This problem is solved in languages such as Haskell and Scala with the
     * aid of Higher-Kinded Types (HKT). Looking into possible HKT simulations
     * to remedy this.
     *
     * @param mapper function to perform transformation.
     * @param <U> Type of transformed value.
     * @return new monad containing transformed value.
     */
    default <U> Monad<U> flatMap(final Function<? super T, ? extends Monad<U>> mapper) {
        Objects.requireNonNull(mapper,"Expected mapper function");
        T value = get();
        return Objects.requireNonNull(mapper.apply(value));
    }

    /**
     * Flatten and return internal {@link Monad} {@code value}, if possible.
     * <p>
     * If {@code this} contains a {@link Monad} value, it is returned, otherwise
     * {@code this} is returned.
     * <p>
     * @param <U> Type of value within {@code nested} {@link Monad} object.
     * @return flattened {@link Monad} object.
     */
    default <U> Monad<U> flatten() {
        @SuppressWarnings("unchecked")
        Function<? super T, ? extends Monad<U>> f = v -> (Monad<U>) v;
        @SuppressWarnings("unchecked")
        Monad<U> self = (Monad<U>) this;

        Monad<U> result;
        try {
            result = flatMap(f);
        } catch (ClassCastException e) {
            result = self;
        }
        return result;
    }
}
