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
 * A container of an immutable value.
 * <p>
 * Many containers, generally {@code functors} and {@code monads} are immutable
 * containers and implement this interface.
 * <p>
 * Container value(s) are "safely" extractable via the {@link #fold(Object,
 * Function)} or the {@link #getOrElse(Object)} method.
 *
 * @param <T> Type of value in the container.
 */
public interface Context<T> {

    /**
     * Safely extracts the contained value.
     * <p>
     * If the contained {@code value} exists or available, it is returned
     * otherwise {@code other} is returned instead.
     *
     * @param other value to return if contained {@code value} is not available.
     * @return contained {@code value} or {@code other} if {@code value} is
     * unavailable.
     */
    T getOrElse(final T other);

    /**
     * Retrieves {@code value} from this container.
     * <p>
     * If the {@code value} is unretrievable (is empty) then the {@code identity}
     * or initial value is returned, otherwise the {@code function} performs a
     * transformation/reduction on the container {@code value}.
     *
     * @param identity default/initial value to return if {@code value} is
     *                 unavailable.
     * @param function function to operate on container to extract/reduce {@code
     * value }
     * @param <U> Type of returned value.
     * @return value extracted/transformed from container.
     */
    default <U> U fold(final U identity, final Function<? super T, ? extends U> function) {
        Objects.requireNonNull(function);
        U result;
        try {
            T value = get();
            result = Objects.requireNonNull(value == null ? identity : function.apply(value));
        } catch (Exception e) {
            result = identity;
        }
        return result;
    }

    /**
     * Returns {@code value} from this container.
     * <p>
     * However, if the {@code value} is unavailable, then {@code null} is
     * returned. Managing the possible empty values can be achieved with the
     * alternate methods {@link #fold} and {@link #getOrElse}.
     *
     * @return contained value.
     */
    default T get() {
        return getOrElse(null);
    }
}
