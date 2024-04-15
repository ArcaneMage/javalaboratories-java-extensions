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
 * A container of two elements, which is a convenient pattern to return two
 * objects from a method.
 * <p>
 * This interface can be implemented for any scenario that requires to group
 * related elements together, but they do not have to be related in terms of
 * type.
 *
 * @param <A> Type of first element.
 * @param <B> Type of second element.
 */
public interface Pair<A,B> {

    /**
     * @return the value of the first element.
     */
     A _1();

    /**
     * @return the value of the second element.
     */
    B _2();

    /**
     * @return {@link Tuple} object that represents this {@link Pair}.
     * @see Tuple2
     */
    default Tuple2<A,B> toTuple() {
        return Tuple.of(_1(),_2());
    }
}
