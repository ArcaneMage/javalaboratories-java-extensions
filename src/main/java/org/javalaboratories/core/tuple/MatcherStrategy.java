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

/**
 * {@link AbstractMatcher} uses this interface to execute required matcher
 * strategy.
 *
 * This interface will have several implementations of {@link MatcherStrategy},
 * which the {@link AbstractMatcher} will invoke, depending on desired behaviour
 * the calling class.
 * @param <Q> type of {@link Matcher}
 * @param <R> type of {@link TupleContainer}, normally {@link Tuple}
 *
 * @see Matcher
 * @see AbstractMatcher
 */
@FunctionalInterface
public interface MatcherStrategy<Q extends Matcher, R extends TupleContainer> {

    BiPredicate<Object,Object> DEFAULT_OBJECT_MATCHING = (matcherElement,element) -> matcherElement == null && element == null ||
            matcherElement != null && matcherElement.equals(element);

    BiPredicate<Pattern,String> DEFAULT_PATTERN_MATCHING = (pattern,element) -> pattern != null && pattern.matcher(element).matches();

    boolean match(Q matcher, R tuple);

    /**
     * Matcher strategy to match all matcher elements with tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link TupleContainer}
     * element. A match is considered when all the {@link Matcher} elements match
     * with the {@link TupleContainer} counterparts. If the {@link TupleContainer}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * This method will provide default implementations for both object and pattern
     * matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link TupleContainer} object, generally a {@link Tuple}
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> all() {
        return all(DEFAULT_OBJECT_MATCHING,DEFAULT_PATTERN_MATCHING);
    }

    /**
     * Matcher strategy to match all matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link TupleContainer}
     * element. A match is considered when all the {@link Matcher} elements match
     * with the {@link TupleContainer} counterparts. If the {@link TupleContainer}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * Two matching predicates are required, one for object matching and one for
     * pattern matching.
     * @param objectMatching predicate function for object matching
     * @param patternMatching predicate function for pattern matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link TupleContainer} object, generally a {@link Tuple}
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> all(BiPredicate<Object,Object> objectMatching,
                                                                                 BiPredicate<Pattern,String> patternMatching) {
        return (matcher, tuple) -> {
            boolean result = true;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( result && it.hasNext() && i < matcher.depth() ) {
                Object element = it.next();
                result = MatcherHelper.match(matcher,element,i+1,objectMatching,patternMatching);
                i++;
            }
            return result;
        };
    }

    /**
     * Matcher strategy to match any of the matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link TupleContainer}
     * element. A match is considered when any of the {@link Matcher} elements match
     * with the {@link TupleContainer} counterparts. If the {@link TupleContainer}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * This method will provide default implementations for both object and pattern
     * matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link TupleContainer} object, generally a {@link Tuple}
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> any() {
        return any(DEFAULT_OBJECT_MATCHING,DEFAULT_PATTERN_MATCHING);
    }

    /**
     * Matcher strategy to match any of the matcher elements with a tuple.
     * <p>
     * Each {@link Matcher} element is compared in turn with each {@link TupleContainer}
     * element. A match is considered when any of the {@link Matcher} elements match
     * with the {@link TupleContainer} counterparts. If the {@link TupleContainer}
     * element is a string, it is compared with the {@link Matcher} regular expression
     * for that current element position, if a regular expression exists for it.
     * <p>
     * Two matching predicates are required, one for object matching and one for
     * pattern matching.
     * @param objectMatching predicate function for object matching
     * @param patternMatching predicate function for pattern matching.
     * @param <Q> type of {@link Matcher} object
     * @param <R> type of {@link TupleContainer} object, generally a {@link Tuple}
     * @return {@code True} is return if all {@link Matcher} elements match with
     * the {@link Tuple} counterparts.
     */
    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> any(BiPredicate<Object,Object> objectMatching,
                                                                                 BiPredicate<Pattern,String> patternMatching) {
        return (matcher, tuple) -> {
            boolean result = false;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( !result && it.hasNext() && i < matcher.depth() ) {
                Object element = it.next();
                result = MatcherHelper.match(matcher,element,i+1,objectMatching,patternMatching);
                i++;
            }
            return result;
        };
    }
}
