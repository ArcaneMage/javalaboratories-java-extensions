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
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import static org.javalaboratories.core.tuple.Matcher.DEFAULT_OBJECT_MATCHING;
import static org.javalaboratories.core.tuple.Matcher.DEFAULT_PATTERN_MATCHING;

/**
 * {@link AbstractMatcher} uses this interface to execute required matcher
 * strategy.
 *
 * This interface has several implementations of {@link MatcherStrategy},
 * which the {@link AbstractMatcher} will invoke, depending on desired behaviour
 * of the calling class.
 * @param <Q> type of {@link Matcher}
 * @param <R> type of {@link Tuple}, normally {@link Tuple}
 *
 * @see Matcher
 * @see AbstractMatcher
 */
@FunctionalInterface
public interface MatcherStrategy<Q extends Matcher, R extends Tuple> {

    boolean match(Q matcher, R tuple);

    /**
     * Matcher strategy to match all matcher elements with tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link Tuple}
     * element. A match is considered when all the {@link Matcher} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * This method provides default implementations for both object and pattern
     * matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> all() {
        return all(DEFAULT_OBJECT_MATCHING,DEFAULT_PATTERN_MATCHING);
    }

    /**
     * Matcher strategy to match all matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link Tuple}
     * element. A match is considered when all the {@link Matcher} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * Two matching predicates are required, one for object matching and one for
     * pattern matching.
     * @param objectMatching predicate function for object matching
     * @param patternMatching predicate function for pattern matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> all(BiPredicate<Object,Object> objectMatching,
                                                                         BiPredicate<Pattern,String> patternMatching) {
        return (matcher, tuple) -> {
            boolean result = true;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( result && it.hasNext() && i < matcher.depth() ) {
                Object element = it.next();
                Object matcherElement = matcher.value(i+1);
                Pattern matcherPattern = matcher.getPattern(i+1).orElse(null);
                result = MatcherHelper.matchElement(element,matcherElement,matcherPattern,objectMatching,patternMatching);
                i++;
            }
            return result;
        };
    }

    /**
     * Matcher strategy to match any of the matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link Tuple}
     * element. A match is considered when any of the {@link Matcher} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * This method provides default implementations for both object and pattern
     * matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> any() {
        return any(DEFAULT_OBJECT_MATCHING,DEFAULT_PATTERN_MATCHING);
    }

    /**
     * Matcher strategy to match any of the matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link Tuple}
     * element. A match is considered when any of the {@link Matcher} elements match
     * with the {@link Tuple} counterparts. If the {@link Tuple}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * Two matching predicates are required, one for object matching and one for
     * pattern matching.
     * @param objectMatching predicate function for object matching
     * @param patternMatching predicate function for pattern matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> any(BiPredicate<Object,Object> objectMatching,
                                                                         BiPredicate<Pattern,String> patternMatching) {
        return (matcher, tuple) -> {
            boolean result = false;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( !result && it.hasNext() && i < matcher.depth() ) {
                Object element = it.next();
                Object matcherElement = matcher.value(i+1);
                Pattern matcherPattern = matcher.getPattern(i+1).orElse(null);
                result = MatcherHelper.matchElement(element,matcherElement,matcherPattern,objectMatching,patternMatching);
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
     * <p>
     * This method provides default implementations for both object and pattern
     * matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> set() {
        return set(DEFAULT_OBJECT_MATCHING,DEFAULT_PATTERN_MATCHING);
    }

    /**
     * Matcher strategy to match the matcher elements with a tuple, ignoring the
     * ordered nature of the tuple elements.
     * <p>
     * This strategy is not concerned with the ordered nature of the
     * {@link Tuple} elements, but whether the {@link Tuple} contains the
     * {@link Matcher} elements. Therefore, matching {@code [1,2,3]} with tuple
     * {@code [3,2,1]} will result in {@code True}. The comparison is more akin
     * to mathematical sets.
     * <p>
     * Two matching predicates are required, one for object matching and one for
     * pattern matching.
     * @param objectMatching predicate function for object matching
     * @param patternMatching predicate function for pattern matching
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link Tuple} object.
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher, R extends Tuple> MatcherStrategy<Q,R> set(BiPredicate<Object,Object> objectMatching,
                                                                         BiPredicate<Pattern,String> patternMatching) {
        return (matcher, tuple) -> {
            boolean result = true;
            for ( int j = 0; j < matcher.depth() && result; j++ ) {
                Object matcherElement = matcher.value(j+1);
                Pattern matcherPattern = matcher.getPattern(j+1).orElse(null);
                boolean exists = false;
                Iterator<Object> it = tuple.iterator();
                while ( it.hasNext() && !exists) {
                    Object element = it.next();
                    exists = MatcherHelper.matchElement(element, matcherElement, matcherPattern, objectMatching, patternMatching);
                }
                result = exists;
            }
            return result;
        };
    }
}
