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

/**
 * A class that implements this interface has the ability to both encrypt and
 * decrypt data.
 *
 * Some implementations can only support one-way encryption, and such
 * implementations must make it explicitly clear in the documentation and
 * raise an {@link UnsupportedOperationException} where applicable.
 *
 * @see CryptographyFactory
 * @see AesCryptography
 */
public interface Cryptography {

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * decrypted bytes in {@code byte[]} array.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @return an array of decrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    byte[] decrypt(final byte[] data);

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * encrypted bytes in {@code byte[]} array.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @return an array of encrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    byte[] encrypt(final byte[] data);
}
