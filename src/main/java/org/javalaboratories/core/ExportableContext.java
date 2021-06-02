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
package org.javalaboratories.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Contexts that implement this interface has the ability to transform into
 * an immutable collection.
 * <p>
 * In all cases, {@code null} contexts will always create a collection with that
 * value encapsulated in the immutable collection with the exception of the
 * {@link Maybe}.
 *
 * @param <T> type of underlying value in context.
 */
public interface ExportableContext<T> {

    /**
     * Transforms context {@code value} into a {@link List}, if possible.
     *
     * @return a list object with {@code value}.
     */
    List<T> toList();

    /**
     * Transform context {@code value} into a {@link Map}, if possible.
     *
     * @param keyMapper supply key for the context {@code value}.
     * @param <K> type of key.
     * @return a map object with the {@code value}
     */
    <K> Map<K,T> toMap(final Function<? super T, ? extends K> keyMapper);

    /**
     * Transforms context {@code value} into a {@link Set}, if possible.
     *
     * @return a set of context {@code value}.
     */
    Set<T> toSet();

}
