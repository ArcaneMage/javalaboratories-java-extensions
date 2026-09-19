/*
 * Copyright 2024 Kevin Henry
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Strings {
    /**
     * This method will merge the contents of an array into a single string.
     * <p>
     * This is the simplest overloaded function that takes care of element type amd
     * object transformation automatically. Just supply {@code source},
     * {@code delimiter} and {@code limit}, and the rest is done for you. If
     * transformation is required, then use {@link Strings#coalesce(Object[],
     * IntFunction, Function, String, boolean, int)} function that offers the most
     * flexibility.
     *
     * @param source source of the array to be coalesced
     * @param delimiter dictates the delimiter to be used in the resultant string.
     *                  The default "," is used when the {@code delimiter} is {@code
     *                  null}.
     * @param brackets true means enclose the resultant string in brackets; false means
     *                 no brackets supplied.
     * @param limit dictates whether resultant string limited. If the resultant
     *              string exceeds the length, then {@code ...} suffix is applied.
     * @return coalesced string
     * @param <T> type of elements
     * @see Strings#coalesce(Object[], IntFunction, Function, String, boolean, int)
     */
    public static <T> String coalesce(final T[] source, final String delimiter, final boolean brackets, final int limit) {
        return coalesce(source, i -> source[i], Function.identity(), delimiter, brackets, limit);
    }

    /**
     * This method will merge the contents of an array into a single string.
     * <p>
     * During the process, the elements may be transformed to an alternative type,
     * and/or object with the {@code mapIndex} and {@code mapElement} functions.
     * The resultant coalesced string may be limited in length as well as delimited,
     * as dictated by both {@code limited} and {@code delimited} parameters
     * respectively.
     *
     * @param source source of the array to be coalesced
     * @param mapIndex can be used to transform the element type
     * @param mapElement can be used to transform the element
     * @param delimiter dictates the delimiter to be used in the resultant string.
     *                  The default "," is used when the {@code delimiter} is {@code
     *                  null}.
     * @param brackets true means enclose the resultant string in brackets; false means
     *                 no brackets supplied.
     * @param limit dictates whether resultant string limited. If the resultant
     *              string exceeds the length, then {@code ...} suffix is applied.
     * @return coalesced string
     * @param <T> type of elements
     * @param <U> return type of mapped index
     * @param <R> return type of mapped element
     */
    public static <T,U,R> String coalesce(final T[] source, final IntFunction<? extends U> mapIndex,
                                          final Function<? super U, ? extends R> mapElement, final String delimiter,
                                          final boolean brackets, final int limit) {
        final int LIMIT = 32;
        T[] s = Objects.requireNonNull(source, "Source cannot be null");
        int length = s.length;
        int l = limit;
        String d = Objects.requireNonNullElse(delimiter,",");

        if (l < LIMIT) l = LIMIT;
        String leftBracket = brackets ? "[" : "";
        String rightBracket = brackets ? "]" : "";
        return IntStream.range(0, length)
                .limit(l)
                .mapToObj(Objects.requireNonNull(mapIndex, "Function mapIndex cannot be null"))
                .map(Objects.requireNonNull(mapElement, "Function mapElement cannot be null"))
                .map(Object::toString)
                .collect(Collectors.joining(d,leftBracket,length < l ? rightBracket : "...%s".formatted(rightBracket)));
    }

    private Strings() {}
}