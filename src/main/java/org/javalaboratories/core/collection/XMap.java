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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This interface introduces features that only found in Java 9 and above. It
 * is a drop-in replacement and an extension to the {@link Map} interface
 * found in Java 8.
 * <p>
 * All factory methods in this interface return an unmodifiable Maps.
 *
 * @param <K> type of Key
 * @param <V> type of Value
 */
public interface XMap<K,V> extends Map<K,V> {

    static <K,V> Map<K,V> copyOf(final Map<? extends K,? extends V> map) {
        Objects.requireNonNull(map);
        HashMap<K,V> result = new HashMap<>();
        map.forEach((k,v) -> result.put(Objects.requireNonNull(k),Objects.requireNonNull(v)));
        return Collections.unmodifiableMap(result);
    }

    static <K,V> Map.Entry<K,V> entry(final K k, final V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k,v);
    }

    static <K,V> Map<K,V> of() {
        return Collections.emptyMap();
    }

    static <K,V> Map<K,V> of(final K k, final V v) {
        Map<K,V> m = new HashMap<>();
        m.put(k,v);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        m.put(k8,v8);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        m.put(k8,v8);
        m.put(k9,v9);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        m.put(k8,v8);
        m.put(k9,v9);
        m.put(k10,v10);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        m.put(k8,v8);
        m.put(k9,v9);
        m.put(k10,v10);
        m.put(k11,v11);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(final K k1, final V v1, final K k2, final V v2, final K k3, final V v3, final K k4, final V v4, final K k5, final V v5, final K k6, final V v6, final K k7, final V v7, final K k8, final V v8, final K k9, final V v9, final K k10, final V v10, final K k11, final V v11, final K k12, final V v12) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        m.put(k7,v7);
        m.put(k8,v8);
        m.put(k9,v9);
        m.put(k10,v10);
        m.put(k11,v11);
        m.put(k12,v12);
        return Collections.unmodifiableMap(m);
    }

    @SafeVarargs
    static <K,V> Map<K,V> ofEntries(final Map.Entry<? extends K,? extends V>... entries) {
        Objects.requireNonNull(entries);
        return Collections.unmodifiableMap(Stream.of(entries)
                .collect(HashMap::new,(a,b) -> a.put(Objects.requireNonNull(b.getKey()),Objects.requireNonNull(b.getValue())), (a,b) -> {}));
    }
}