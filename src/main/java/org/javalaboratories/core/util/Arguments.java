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
package org.javalaboratories.core.util;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Class of utility methods for validation of method parameters/arguments.
 * <p>
 * This is similar to the {@link Objects#requireNonNull(Object)}, but offers
 * convenient methods to test multiple arguments for validation as varargs.
 */
public final class Arguments {

    /**
     * Validates {@code arguments} for {@code null} values.
     *
     * @param arguments varargs of {@code arguments/parameters}.
     * @throws NullPointerException if any of the {@code arguments} are
     * {@code null}.
     */
    public static void requireNonNull(final Object... arguments) {
        requireNonNull(NullPointerException::new,arguments);
    }

    /**
     * Validates {@code arguments} for {@code null} values.
     *
     * @param arguments varargs of {@code arguments/parameters}.
     * @param message details message to report in {@code exception} object.
     * @throws NullPointerException if any of the {@code arguments} are
     * {@code null}.
     */
    public static void requireNonNull(final String message, final Object... arguments) {
        requireNonNull(() -> new NullPointerException(message), arguments);
    }

    /**
     * Validates {@code arguments} for {@code null} values then throws requested
     * exception of type E.
     *
     * @param arguments varargs of {@code arguments/parameters}.
     * @param supplier supplies exception with which to raise.
     * @param <E> Type of exception to throw.
     * @throws NullPointerException object type if {@code supplier} or
     * {@code arguments} is {@code null}
     * @throws E as requested by {@code supplier} parameter if any of the {@code
     * arguments} is {@code null}
     */
    public static <E extends Exception> void requireNonNull(final Supplier<? extends E> supplier, final Object... arguments) throws E {
        Objects.requireNonNull(supplier,"Expected supplier?");
        Objects.requireNonNull(arguments,"Expected arguments?");
        for (Object o : arguments) {
            if (o == null)
                throw supplier.get();
        }
    }
}
