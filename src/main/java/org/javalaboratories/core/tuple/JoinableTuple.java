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
 * Tuples, which implement this interface, with the exception of {@link Matcher}
 * objects, are considered to be "joinable".
 * <p>
 * That is to mean they have the ability to be concatenated to form a new
 * tuple.
 */
public interface JoinableTuple<T extends Tuple> {
    /**
     * Joins this tuple with {@code that} tuple object.
     *
     * @param that tuple to join with this object.
     * @param <R> type of this tuple.
     * @return instance of newly joined tuple.
     */
    <R extends Tuple> R join(final T that);
}
