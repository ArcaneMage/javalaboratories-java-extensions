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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@code functor} is a structure that supports one important operation:
 * {@code map}.
 * <p>
 * This is important in that it is the only mechanism allowed in the world of
 * functional programming the ability to transform the {@code value} within the
 * {@code functor container}.
 *
 * @param <T> Type of value within the {@code functor}.
 */
@FunctionalInterface
public interface Functor<T> extends ImmutableValue<T> {

    /**
     * Transforms the contained {@code value} and returns a new {@code
     * functor} with the transformed {@code value}.
     *
     * @param mapper function to perform the transformation of the contained
     *               {@code value}.
     * @param <R> Type of the returned {@code value}
     * @return a new {@code functor} with the transformed {@code value}.
     */
    <R> Functor<R> map(final Function<? super T,? extends R> mapper);

    /**
     * Provides access to the contained {@code value} within this {@link
     * Functor}.
     * <p>
     * This is useful to confirm/verify the current state of the contained
     * {@code value} within this {@link Functor} -- ideal for debugging
     * purposes.
     *
     * @param consumer function to allow access to the {@code value}.
     * @return the current {@code functor} for additional downstream
     * operations.
     * @throws NullPointerException if {@code consumer} is null.
     */
   default Functor<T> peek(final Consumer<? super T> consumer) {
       Objects.requireNonNull(consumer);
       T value;
       try {
           value = get();
       } catch (Exception e) {
           value = null;
       }
       consumer.accept(value);
       return this;
   }
}
