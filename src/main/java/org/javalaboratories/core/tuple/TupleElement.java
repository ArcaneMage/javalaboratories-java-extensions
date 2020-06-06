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
 * An element of a tuple.
 * <p>
 * Contains information pertaining to an element of a tuple.
 * <p>
 * Not all tuples have the ability to return {@link TupleElement} objects, for
 * example {@link MatchableTuple} objects. Some classes will find this information
 * useful to perform their operations.
 */
public interface TupleElement {

    /**
     * Returns value of the element.
     * <p>
     * @param <T> type of element.
     * @return value of element. The method will attempt to cast to the recipient
     * object.
     */
    <T> T value();

    /**
     * {@link TupleElementMatcher} objects use this property to decide whether to
     * apply pattern matching.
     * @return {@code True} if the the element is a {@link String} object type.
     */
    boolean isString();

    /**
     * Returns the {@link Tuple} object that this {@link TupleElement} object
     * belongs to.
     * @param <T> type of {@link TupleContainer} object.
     * @return instance of tuple the element pertains to.
     */
    <T extends TupleContainer> T owner();

    /**
     * @return the logical position of this {@link TupleElement} in the
     * {@link Tuple}.
     */
    int position();
}
