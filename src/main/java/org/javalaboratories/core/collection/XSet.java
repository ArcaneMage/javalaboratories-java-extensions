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
package org.javalaboratories.core.collection;

import java.util.*;
import java.util.stream.Stream;

/**
 * {@link XSet} is a cache that implements the {@code Least Recently Used}
 * policy. The least recently used cache {@code entry} will be evicted from
 * the {@link XSet}. This {@code XSet} has a fixed {@code capacity} that is set
 * in the constructor and cannot be altered.
 *
 * @param <T> type of element
 */
public interface XSet<T> extends Set<T> {

    static <T> Set<T> copyOf(final Collection<? extends T> coll) {
        return Collections.unmodifiableSet(Objects.requireNonNull(coll).stream()
                .collect(HashSet::new,(a,b) -> a.add(Objects.requireNonNull(b)),(a, b) -> {}));
    }

    @SafeVarargs
    static <T> Set<T> of(T... elements) {
        return Collections.unmodifiableSet(Stream.of(Objects.requireNonNull(elements))
                .collect(HashSet::new,(a,b) -> a.add(Objects.requireNonNull(b)), (a, b) -> {}));
    }
}
