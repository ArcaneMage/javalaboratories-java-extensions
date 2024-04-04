/*
 * Copyright 2024 Kevin Henry
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
package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.Maybe;

import java.security.Key;
import java.util.Objects;

public class CryptographyResultImpl<K extends Key> implements CryptographyResult<K> {
    private final K key;
    private final byte[] sessionKey;

    public CryptographyResultImpl(final K key) {
        this(key,null);
    }

    public CryptographyResultImpl(final K key, final byte[] sessionKey) {
        this.key = Objects.requireNonNull(key);
        this.sessionKey = sessionKey;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public Maybe<byte[]> getSessionKey() {
        return Maybe.ofNullable(sessionKey);
    }
}
