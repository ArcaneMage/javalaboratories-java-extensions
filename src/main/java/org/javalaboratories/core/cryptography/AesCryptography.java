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

import java.io.*;
import java.util.Base64;
import java.util.Objects;

/**
 * An object that implements this interface has the ability to both encrypt and
 * decrypt data.
 * <p>
 * It is considered as {@code symmetric} in that it uses the same key to both
 * encrypt and decrypt the data. The contract supports {@link String} and {@code
 * Stream} encryption and decryption allowing a wide variety of object types,
 * including {@code files}.
 */
public interface AesCryptography {

    /**
     * Decrypts {@link InputStream} that contains a stream of cipher data.
     *
     * @param key contains private key with which to decrypt the stream
     * @param cipherStream a stream of encoded cipher data to be decrypted.
     * @param outputStream a stream of decoded data.
     * @return a {@link StreamCryptographyResult} that encapsulate decoded
     * data.
     * @param <T> of output stream.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    <K extends SymmetricSecretKey,T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K key,
                                                                                                final InputStream cipherStream,
                                                                                                final T outputStream);

    /**
     * Encrypts the {@link InputStream} with the given {@code SymmetricSecretKey}.
     *
     * @param key with which to encrypt the {@link InputStream}.
     * @param inputStream a stream of data to be encrypted.
     * @param cipherStream a stream of cipher data.
     * @return a {@link StreamCryptographyResult} object encapsulating encrypted
     * data.
     * @param <T> type of output stream.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    <K extends SymmetricSecretKey, T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K key,
                                                                                                 final InputStream inputStream,
                                                                                                 final T cipherStream);

    /**
     * Decrypts string ciphers with the given {@link SymmetricSecretKey} object.
     *
     * @param key contains private key with which to decrypt the data.
     * @param ciphertext the ciphertext to be decrypted in Base64 format.
     * @return a {@link ByteCryptographyResult} encapsulating encryption
     * key and deciphered string.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    default <K extends SymmetricSecretKey> ByteCryptographyResult<K> decrypt(final K key, final String ciphertext) {
        String  ct = Objects.requireNonNull(ciphertext, "Expected encrypted cipher text");
        K k = Objects.requireNonNull(key, "Expected key object");
        try {
            ByteCryptographyResult<K> result = decrypt(k,Base64.getDecoder().decode(ct));

            return new ByteCryptographyResultImpl<>(key,result.getBytes(),new String(result.getBytes()));
        } catch (IllegalArgumentException e) {
            throw new CryptographyException("Failed to decrypt encoded ciphertext",e);
        }
    }

    /**
     * Decrypts string ciphers with the given {@link SymmetricSecretKey} object.
     *
     * @param key contains private key with which to decrypt the data.
     * @param bytes the cipher text to be decrypted.
     * @return a {@link ByteCryptographyResult} encapsulating encryption
     * key and deciphered string.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    default <K extends SymmetricSecretKey> ByteCryptographyResult<K> decrypt(final K key, final byte[] bytes) {
        K k = Objects.requireNonNull(key, "Expected key object");
        byte[] b = Objects.requireNonNull(bytes, "Expected encrypted bytes");
        StreamCryptographyResult<K, ByteArrayOutputStream> result =
                decrypt(k,new ByteArrayInputStream(b),new ByteArrayOutputStream());
        byte[] decryptedBytes = result.getStream().toByteArray();
        return new ByteCryptographyResultImpl<>(key,decryptedBytes,null);
    }

    /**
     * Encrypts {@link String} with a given {@code SymmetricSecretKey}.
     *
     * @param key a key to with which to the encrypted data.
     * @param string a string to be encrypted.
     * @return a {@link ByteCryptographyResult} encapsulating encryption
     * key and cipher text.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    default <K extends SymmetricSecretKey> ByteCryptographyResult<K> encrypt(final K key, final String string) {
        K k = Objects.requireNonNull(key, "Expected password");
        String s = Objects.requireNonNull(string, "Expected string to encrypt");
        try {
            return encrypt(k, s.getBytes());
        } catch (CryptographyException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

    /**
     * Encrypts {@link String} with a given {@code SymmetricSecretKey}.
     *
     * @param key a key to with which to the encrypted data.
     * @param bytes bytes to be encrypted.
     * @return a {@link ByteCryptographyResult} encapsulating encryption
     * key and cipher text.
     * @throws NullPointerException when parameters are null.
     * @see SymmetricSecretKey
     */
    default <K extends SymmetricSecretKey> ByteCryptographyResult<K> encrypt(final K key, final byte[] bytes) {
        K k = Objects.requireNonNull(key, "Expected password");
        byte[] b = Objects.requireNonNull(bytes, "Expected string to encrypt");

        StreamCryptographyResult<K,ByteArrayOutputStream> result =
                encrypt(k,new ByteArrayInputStream(b), new ByteArrayOutputStream());

        return new ByteCryptographyResultImpl<>(k,result.getStream().toByteArray(),null);
    }

    /**
     * Encrypts the {@link File} with a given {@code SymmetricSecretKey}.
     *
     * @param key a key of the encrypted file.
     * @param source source file with which to encrypt.
     * @param cipherFile encrypted file.
     * @return a {@link FileCryptographyResult} encapsulating encrypted key and
     * file.
     * @throws NullPointerException when parameters are null.
     */
    default <K extends SymmetricSecretKey> FileCryptographyResult<K> encrypt(final K key, final File source, final File cipherFile) {
        try (InputStream is = new FileInputStream(Objects.requireNonNull(source,"Expected source file"));
             OutputStream os = new FileOutputStream(Objects.requireNonNull(cipherFile,"Expected cipher file"))) {
            StreamCryptographyResult<K,OutputStream> result = encrypt(Objects.requireNonNull(key,
                    "Expected key object"), is, os);

            return new FileCryptographyResultImpl<>(result.getKey(),cipherFile);
        } catch (IOException e) {
            throw new CryptographyException("Failed to encrypt file",e);
        }
    }

    /**
     * Decrypts the {@link File} with a given {@link SymmetricSecretKey}.
     *
     * @param key a key of the encrypted file.
     * @param output decrypted file output.
     * @param cipherFile encrypted file.
     * @return a {@link FileCryptographyResult} encapsulating encrypted key and
     * output file.
     */
    default <K extends SymmetricSecretKey> FileCryptographyResult<K> decrypt(final K key, final File cipherFile, final File output) {
        try (InputStream is = new FileInputStream(Objects.requireNonNull(cipherFile,"Expected cipher file"));
             OutputStream os = new FileOutputStream(Objects.requireNonNull(output,"Expected output file"))) {
            StreamCryptographyResult<K,OutputStream> result = decrypt(Objects.requireNonNull(key,
                    "Expected key object"), is, os);

            return new FileCryptographyResultImpl<>(result.getKey(),output);
        } catch (IOException e) {
            throw new CryptographyException("Failed to decrypt file",e);
        }
    }
}
