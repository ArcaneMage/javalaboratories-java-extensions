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

import org.javalaboratories.core.cryptography.keys.Secrets;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An object that implements this interface has the ability to both encrypt and
 * decrypt data.
 * <p>
 * It is considered as {@code symmetric} in that it uses the same key to both
 * encrypt and decrypt the data. The contract supports {@link String} and {@code
 * Stream} encryption and decryption allowing a wide variety of object types,
 * including {@code files}.
 */
public interface SymmetricCryptography {

    /**
     * Decrypts string ciphers with the given {@link Secrets} object.
     *
     * @param secrets contains private key, salt and initial vector values.
     * @param cipherText the cipher text to be decrypted.
     * @return decoded string.
     */
    String decrypt(Secrets secrets, String cipherText);

    /**
     * Decrypts {@link InputStream} that contains a stream of cipher data.
     *
     * @param secrets contains private key, salt and initial vector values.
     * @param cipherStream a stream of encoded cipher data to be decrypted.
     * @param outputStream a stream of decoded data.
     * @return a {@link CryptographyStreamResult} that encapsulate decoded
     * data.
     * @param <T> of output stream.
     */
    <T extends OutputStream> CryptographyStreamResult<T> decrypt(Secrets secrets, InputStream cipherStream,
                                                                 T outputStream);

    /**
     * Encrypts {@link String} with a given {@code password}.
     *
     * @param password a password of the encrypted data.
     * @param string a string to be encrypted.
     * @return a {@link CryptographyStringResult} encapsulating encryption
     * key and cipher text.
     */
    CryptographyStringResult encrypt(String password, String string);

    /**
     * Encrypts the {@link InputStream} with the given {@code password}.
     *
     * @param password with which to encrypt the {@link InputStream}.
     * @param inputStream a stream of data to be encrypted.
     * @param cipherStream a stream of cipher data.
     * @return a {@link CryptographyStreamResult} object encapsulating encrypted
     * data.
     * @param <T> type of output stream.
     */
    <T extends OutputStream> CryptographyStreamResult<T> encrypt(String password, InputStream inputStream,
                                                                 T cipherStream);
}
