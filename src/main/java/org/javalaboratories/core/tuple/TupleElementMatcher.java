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
package org.javalaboratories.core.tuple;

/**
 * Matches an element of a tuple or elements within a tuple against a
 * {@link MatchableTuple} object.
 * <p>
 * This object will attempt to match each element in the {@link MatchableTuple},
 * applying {@link java.util.regex.Pattern}, if applicable -- only {@link String}
 * object types invoke pattern matching, and only if a pattern is available.
 * All other types invoke the equal method.
 * <p>
 * {@link AbstractMatcher} creates a default implementation of this interface.
 * @see AbstractMatcher
 * @see MatchableTuple
 * @see DefaultTupleElementMatcher
 */
public interface TupleElementMatcher {
    /**
     * Match {@code element} at logical {@code position} with {@link MatchableTuple}
     * element.
     *
     * @param element element to match, ultimately came from {@link Tuple}
     * @return {@code True} for a match; {@code False} for a non-match.
     */
    boolean match(TupleElement element);

    /**
     * Each element in the {@link MatchableTuple} is tested against all the
     * {@link Tuple} elements.
     *
     * @param tuple the {@link Tuple} to match.
     * @param <T> type of {@link Tuple}
     * @return {@code True} for a match; {@code False} for a non-match.
     */
    <T extends Tuple> boolean match(T tuple);

    /**
     * Returns underlying {@link MatchableTuple} object whose elements and patterns
     * are used in the matching process.
     * @return an implementation of {@link MatchableTuple}
     */
    MatchableTuple getMatchable();
}
