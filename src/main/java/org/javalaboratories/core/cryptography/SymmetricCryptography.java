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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class that implements this interface has the ability to both encrypt and
 * decrypt data.
 * <p>
 * The implementation only supports symmetric keys in that the same key is used
 * to both encrypt and decrypt. Some methods do not require keys with which to
 * perform the operation, but if the underlying implementation does not
 * support/require keys, they must make this explicitly clear in the
 * documentation and throw an {@link UnsupportedOperationException} exception.
 *
 * @see CryptographyFactory
 * @see SunAesSymmetricCryptography
 */
public interface SymmetricCryptography {
    /**
     * This method reads the {@code byte[]} array and returns an array of
     * decrypted bytes in {@code byte[]} array, using the symmetric {@code
     * key}.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @param key encryption secret key
     * @return an array of decrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    byte[] decrypt(final String key, final byte[] data);

    /**
     * Reads an {@link InputStream} decrypts and outputs the encrypted data to
     * the {@link OutputStream}.
     *
     * @param istream stream to be encrypted
     * @param ostream stream of encrypted data.
     * @param key encryption secret key
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    void decrypt(final String key, final InputStream istream, final OutputStream ostream);

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * encrypted bytes in {@code byte[]} array, using the symmetric {@code
     * key}.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @param key encryption secret key
     * @return an array of encrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    byte[] encrypt(final String key, final byte[] data);

    /**
     * Reads an {@link InputStream} encrypts and outputs the encrypted data to
     * the {@link OutputStream}.
     *
     * @param istream stream to be encrypted
     * @param ostream stream of encrypted data.
     * @param key encryption secret key
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    void encrypt(final String key, final InputStream istream, final OutputStream ostream);
}
