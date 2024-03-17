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

/**
 * All tuples implement this interface
 *
 * @see Tuple
 * @see Matcher
 */
public interface TupleBase {
    int MAX_DEPTH = 16;

    /**
     * Returns the depth of this tuple.
     * <p>
     * The depth refers to the maximum number of objects/elements that can be
     * accommodated in the tuple container. It can be referred to as the size
     * of the tuple.
     *
     * @return depth of this tuple.
     */
    int depth();

    /**
     * Returns non-zero based position of an element within this tuple container.
     *
     * @param object element with which to search.
     * @return zero-based position of the object, 0 to indicate not found.
     */
    int positionOf(Object object);

    /**
     * Returns object value at position.
     * <p>
     * All concrete tuple implementations, for example {@link Tuple2}, have direct
     * random access to values via the {@code Tuple2.valueX()}, where X represents
     * the position of the element. Although this method performs the same
     * operation, it provides access to elements all the concrete type of the
     * tuple is unknown. However, it is not as efficient as {@code valueX} methods
     * because it has to traverse the data structure in order locate the value.
     * <p>
     * Where possible use the {@code valueX} method of the tuple, otherwise resort
     * to this method if the concrete tuple type is unknown.
     * @param position of the element in the tuple, the
     *                 {@link IllegalArgumentException} is thrown if
     *                 {@code position} is less than 1 or greater than (@code depth)
     *                 of the tuple.
     * @param <T> type of object returned.
     * @return Element object cast to operate type. Method will attempt to cast the
     * element type.
     */
    <T> T value(int position);
}
