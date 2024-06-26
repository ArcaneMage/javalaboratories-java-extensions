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
package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.cryptography.keys.SymmetricKey;

import java.security.Key;
import java.util.Base64;

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
public interface ByteCryptographyResult<K extends Key> extends CryptographyResult<K>, SessionMessage, SignableMessage {

    /**
     * @return the encrypted/decrypted data as bytes, depending on {@link
     * AesCryptography} {@code String} operation.
     */
    byte[] getBytes();

    /**
     * @return the encrypted/decrypted data as a Base64 string.
     */
    default String getBytesAsBase64() {
        return Base64.getEncoder().encodeToString(getBytes());
    }

    /**
     * @return the decrypted data as a string. The string is only available in
     * decrypted form, and therefore will not be accessible for encrypted
     * objects.
     */
    Maybe<String> getString();
}
