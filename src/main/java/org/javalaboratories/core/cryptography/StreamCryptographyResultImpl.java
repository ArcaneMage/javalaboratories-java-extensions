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

import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;

import java.io.OutputStream;
import java.security.Key;
import java.util.Objects;

/**
 * {@code CryptographyResult} object is returned from performing cryptographic
 * operations with the {@link AesCryptography} and {@link RsaHybridCryptography}
 * objects.
 * <p>
 * It encapsulates the {@link SymmetricSecretKey} and {@code stream} that are
 * associated with the {@code cipher text}.
 * <p>
 * This object is returned from performing cryptographic operations with {@code
 * streams}
 *
 * @param <T> type of output stream.
 */
public final class StreamCryptographyResultImpl<K extends Key, T extends OutputStream> extends SessionCryptographyResultImpl<K>
        implements StreamCryptographyResult<K,T> {

    private final T outputStream;

    public StreamCryptographyResultImpl(final K key, final T outputStream) {
        this(key,null,outputStream);
    }

    public StreamCryptographyResultImpl(final K key, final byte[] sessionKey, final T outputStream) {
        super(key, sessionKey);
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getStream() {
        return outputStream;
    }
}
