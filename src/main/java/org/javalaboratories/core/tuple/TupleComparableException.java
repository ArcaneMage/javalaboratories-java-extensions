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
 * This exception is thrown all tuple element types are not in the same order
 * all comparing.
 * <p>
 * Tuples element types must be in the same order for the collection sort to
 * function correctly. For example, tuple with types {@code [String,Integer,Date]} will
 * not compare well with tuple with types {@code [String,Date,Integer]} because
 * the types are not in the same order.
 */
public class TupleComparableException extends ClassCastException {
    public TupleComparableException(String s) {
        super(s);
    }
}
