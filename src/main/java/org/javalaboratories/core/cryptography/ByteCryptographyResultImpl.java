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
import org.javalaboratories.core.cryptography.keys.SymmetricKey;

import java.security.Key;
import java.util.Objects;

/**
 * {@code CryptographyResult} object is returned from performing cryptographic
 * operations with the {@link AesCryptography} and {@link RsaHybridCryptography}
 * objects.
 * <p>
 * It encapsulates the {@link SymmetricKey} and {@code String} that are
 * associated with the {@code cipher text}.
 * <p>
 * This object is returned from performing cryptographic operations with {@code
 * String} objects.
 */
public final class ByteCryptographyResultImpl<K extends Key> extends SignableSessionCryptographyResultImpl<K>
        implements ByteCryptographyResult<K> {

    private final byte[] bytes;
    private final String string;

    public ByteCryptographyResultImpl(final K key, byte[] bytes, String string) {
        this(key,null,null,bytes,string);
    }

    public ByteCryptographyResultImpl(final K key, final byte[] sessionKey, final byte[] messageHash, final byte[] bytes,
                                      final String string) {
        super(key,sessionKey,messageHash);
        this.bytes = Objects.requireNonNull(bytes);
        this.string = string;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maybe<String> getString() {
        return Maybe.ofNullable(string);
    }
}
