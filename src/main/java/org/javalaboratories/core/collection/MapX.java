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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface MapX<K,V> extends Map<K,V> {

    static <K,V> Map<K,V> of() {
        return Collections.emptyMap();
    }

    static <K,V> Map<K,V> of(K k, V v) {
        Map<K,V> m = new HashMap<>();
        m.put(k,v);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K,V> m = new HashMap<>();
        m.put(k1,v1);
        m.put(k2,v2);
        m.put(k3,v3);
        m.put(k4,v4);
        m.put(k5,v5);
        m.put(k6,v6);
        return Collections.unmodifiableMap(m);
    }

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
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

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
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

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
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

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
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

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11) {
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

    static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12) {
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
}