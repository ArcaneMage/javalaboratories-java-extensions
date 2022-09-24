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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * {@link XList} is a cache that implements the {@code Least Recently Used}
 * policy. The least recently used cache {@code entry} will be evicted from
 * the {@link XList}. This {@code XList} has a fixed {@code capacity} that is set
 * in the constructor and cannot be altered.
 *
 * @param <T> type of element
 */
public interface XList<T> extends List<T> {

    static <T> List<T> copyOf(final Collection<? extends T> coll) {
        return Collections.unmodifiableList(Objects.requireNonNull(coll).stream()
                .collect(ArrayList::new,(a,b) -> a.add(Objects.requireNonNull(b)),(a,b) -> {}));
    }

    @SafeVarargs
    static <T> List<T> of(final T... elements) {
        return Collections.unmodifiableList(Stream.of(elements)
                .collect(ArrayList::new,(a,b) -> a.add(Objects.requireNonNull(b)),(a,b) -> {}));
    }
}
