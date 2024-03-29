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

import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;

/**
 * This cryptographic factory creates AES, MD5 and other cryptographic objects.
 * <p>
 * It's an abstraction over the JCA/JCE, providing a consistent easily understood
 * interfaces. For example:
 * <pre>
 *     {@code
 *         SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *         CryptographyStringResult result =
 *             cryptography.encrypt(SymmetricSecretKey.from(PASSWORD), STRING_LITERAL);
 *         String cipherText = result.getDataAsBase64();
 *
 *         Outputs -> "xGc/N5WQGeje8QHK68GPdUbho0YIX3mYj/Zqgt5wqTjvkAvOLMOp/RGMM8yRn2GBsFRZA=="
 *         ...
 *         ...
 *         SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *         CryptographyStringResult result =
 *             cryptography.decrypt(SymmetricSecretKey.from(PASSWORD), cipherText);
 *         String string = result.getDataAsString.orElseThrow();
 *
 *         Outputs -> "The quick brown fox jumped over the fence"
 *     }
 * </pre>
 * In addition to {@code Strings}, support for {@code InputStream} and {@code
 * OutputStream} are also provided, as well as {@code File} encryption/decryption.
 *
 * @see SymmetricCryptography
 * @see SymmetricSecretKey
 */
public final class CryptographyFactory {

    /**
     * Provides an interface for {@code Symmetric} encryption and decryption,
     * levering AES standard.
     * <p>
     * Symmetric cryptography means the same key is used for both encryption and
     * decryption.
     *
     * @return {@link SymmetricCryptography} object is returned, encapsulating
     * AES encryption/decryption standard.
     */
    public static SymmetricCryptography getSymmetricCryptography() {
        return new DefaultAesCryptography();
    }

    /**
     * Provides an interface for {@code Message Digest} encryption.
     * <p>
     * This form of encryption is one way, meaning that the resultant cipher
     * text or hash cannot be decrypted. It is primarily used for password
     * hashing and file checksum redundancy checks.
     *
     * @return {@link HashCryptography} implementation is returned, encapsulating
     * Message Digest hashing/encryption.
     */
    public static HashCryptography getHashCryptography() {
        return new DefaultHashCryptography();
    }

    private CryptographyFactory() {}
}
