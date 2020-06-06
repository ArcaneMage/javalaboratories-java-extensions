package org.javalaboratories.core.tuple;
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

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@link AbstractMatcher} uses this interface to execute required matcher
 * strategy.
 *
 * This interface has several implementations of {@link MatcherStrategy},
 * which the {@link AbstractMatcher} will invoke, depending on desired behaviour
 * of the calling class.
 * @param <T> type of {@link Tuple}, normally {@link Tuple}
 *
 * @see MatchableTuple
 * @see AbstractMatcher
 */
@FunctionalInterface
public interface MatcherStrategy<T extends Tuple> {

    boolean match(Supplier<TupleElementMatcher> supplier, T tuple);

    /**
     * Matcher strategy to match all matcher elements with tuple.
     * <p>
     * Each {@link MatchableTuple} element is compared in turn with each {@link Tuple}
     * element. A match is considered when all the {@link MatchableTuple} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link MatchableTuple} regular expression
     * for that current element position, if a regular expression exists for it.
     *
     * @param <T> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link MatchableTuple} elements match with
     * the {@link Tuple} counterparts.
     */
    static <T extends Tuple> MatcherStrategy<T> allOf() {
        return (supplier, tuple) -> {
            Objects.requireNonNull(supplier);
            Objects.requireNonNull(tuple);
            boolean result;
            int i = 0;
            Iterator<TupleElement> it = tuple.iterator();
            TupleElementMatcher matcher = supplier.get();
            result = matcher.getMatchable().depth() <= tuple.depth();
            while ( result && it.hasNext() && i < matcher.getMatchable().depth() ) {
                TupleElement element = it.next();
                result = matcher.match(element);
                i++;
            }
            return result;
        };
    }

    /**
     * Matchable strategy to match any of the matcher elements with a tuple.
     * <p>
     * Each {@link MatchableTuple} element is compared in turn with each {@link Tuple}
     * element. A match is considered when any of the {@link MatchableTuple} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link MatchableTuple} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * Excess {@link MatchableTuple} elements, elements that exceed {@link Tuple} elements,
     * are ignored.
     *
     * @param <T> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link MatchableTuple} elements match with
     * the {@link Tuple} counterparts.
     */
    static <T extends Tuple> MatcherStrategy<T> anyOf() {
        return (supplier, tuple) -> {
            Objects.requireNonNull(supplier);
            Objects.requireNonNull(tuple);
            boolean result = false;
            int i = 0;
            Iterator<TupleElement> it = tuple.iterator();
            TupleElementMatcher matcher = supplier.get();
            while ( !result && it.hasNext() && i < matcher.getMatchable().depth() ) {
                TupleElement element = it.next();
                result = matcher.match(element);
                i++;
            }
            return result;
        };
    }

    /**
     * Matcher strategy to match the matcher elements with a tuple, ignoring the
     * ordered nature of the tuple elements.
     * <p>
     * This strategy is not concerned with the ordered nature of the
     * {@link Tuple} elements. Therefore, matching {@code [1,2,3]} with
     * {@code [3,2,1]} will result in {@code True}. The comparison is more akin
     * to mathematical sets.
     *
     * @param <T> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link MatchableTuple} elements match with
     * the {@link Tuple} counterparts.
     */
    static <T extends Tuple> MatcherStrategy<T> setOf() {
        return (supplier, tuple) -> {
            Objects.requireNonNull(supplier);
            Objects.requireNonNull(tuple);
            TupleElementMatcher matcher = supplier.get();
            return matcher.match(tuple);
        };
    }
}
