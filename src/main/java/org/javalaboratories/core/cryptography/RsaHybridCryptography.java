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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

/**
 * {@code RsaHybridCryptography} supports the RSA encryption of S{@code Strings},
 * {@code Streams} and {@code Files}.
 * <p>
 * Being RSA {@link PublicKey}, {@link PrivateKey} keys are required for
 * encryption and decryption respectively. The term {@code hybrid} means that
 * there are multiple encryption algorithms are required because RSA encryption
 * is not sufficient for encrypting/decrypting large amounts of data. The
 * approach taken for encryption:
 * <ol>
 *     <li>Create/generate a secret key, leveraging AES</li>
 *     <li>Encrypt the the data with AES using the secret key</li>
 *     <li>Encrypt the secret key with RSA public key</li>
 *     <li>Return the {@code ciphertext} with the encrypted key</li>
 * </ol>
 * To decrypt the {@code ciphertext}, the steps are reversed:
 * <ol>
 *     <li>Decrypt the secret key with the RSA private key</li>
 *     <li>Decrypt the {@code ciphertext} with the secret key with AES
 *     decryption</li>
 *     <li>Return deciphered text</li>
 * </ol>
 * Unlike the symmetric decryption package, RSA decryption will require an
 * additional parameter, the {@code cipherKey} that is the encrypted AES key.
 *
 * @see DefaultRsaHybridCryptography
 */
public interface RsaHybridCryptography {

    /**
     * Encrypts the string using the RSA algorithm with the given {@code
     * publicKey}.
     *
     * @param publicKey the key with which to encrypt the string.
     * @param string the string to be encrypted.
     * @return a {@link StringCryptographyResult} encapsulated ciphertext.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    <K extends PublicKey> StringCryptographyResult<K> encrypt(final K publicKey, final String string);

    /**
     * Encrypts the {@code InputStream} using RSA algorithm with the given {@code
     * publicKey}.
     *
     * @param publicKey the key with which to encrypt the string.
     * @param inputStream the stream to be encrypted.
     * @return a {@link StreamCryptographyResult} encapsulated ciphertext.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    <K extends PublicKey, T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K publicKey,
                                                                                        final InputStream inputStream,
                                                                                        final T cipherStream);

    /**
     * Decrypts the {@code String} using RSA algorithm with the given {@code
     * privateKey}.
     *
     * @param privateKey the key with which to decrypt the string.
     * @param cipherKey the RSA encrypted cipher key in {@code Base64} form.
     * @param ciphertext the {@code String} to be decrypted.
     *
     * @return a {@link StringCryptographyResult} encapsulated deciphered text.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    <K extends PrivateKey> StringCryptographyResult<K> decrypt(final K privateKey, final String cipherKey,
                                                               final String ciphertext);


    /**
     * Decrypts the {@code InputStream} using RSA algorithm with the given {@code
     * privateKey}.
     *
     * @param privateKey the key with which to decrypt the {@code cipherStream}.
     * @param cipherKey the RSA encrypted cipher key in {@code Base64} form.
     * @param cipherStream the {@code InputStream} to be decrypted, the {@code ciphertext}.
     * @param outputStream the output of the deciphered text/data.
     *
     * @return a {@link StreamCryptographyResult} encapsulated deciphered text.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    <K extends PrivateKey, T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K privateKey,
                                                                                         final String cipherKey,
                                                                                         final InputStream cipherStream,
                                                                                         final T outputStream);

    /**
     * Encrypts the {@code File} using RSA algorithm with the given {@code publicKey}.
     *
     * @param publicKey the key with which to encrypt the {@code source}.
     * @param source the source file to be RSA encrypted.
     * @param cipherFile the output file of the {@code ciphertext}.
     *
     * @return a {@link FileCryptographyResult} encapsulated ciphertext.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    default <K extends PublicKey> FileCryptographyResult<K> encrypt(final K publicKey, final File source, File cipherFile) {
        K pk = Objects.requireNonNull(publicKey,"Expected public key");
        try (InputStream is = new FileInputStream(Objects.requireNonNull(source,"Expected source file"));
             OutputStream os = new FileOutputStream(Objects.requireNonNull(cipherFile,"Expected cipher file"))) {
            StreamCryptographyResult<K,OutputStream> result = encrypt(Objects.requireNonNull(pk, "Expected key object"), is, os);
            return new FileCryptographyResult<>() {
                @Override
                public File getFile() {
                    return cipherFile;
                }
                @Override
                public K getKey() {
                    return result.getKey();
                }
                @Override
                public Maybe<byte[]> getCipherKey() {
                   return result.getCipherKey();
                }
            };
        } catch (IOException e) {
            throw new CryptographyException("Failed to encrypt file",e);
        }
    }

    /**
     * Decrypts the {@code File} using RSA algorithm with the given {@code privateKey}.
     *
     * @param privateKey the key with which to decrypt the {@code source}.
     * @param cipherKey the RSA encrypted cipher key in {@code Base64} form.
     * @param cipherFile the input file of the {@code ciphertext}.
     * @param output the output of the deciphered text/data.
     *
     * @return a {@link FileCryptographyResult} encapsulated ciphertext.
     * @param <K> the type of key.
     * @throws CryptographyException cryptography failure.
     * @throws NullPointerException whenever parameters are null.
     */
    default <K extends PrivateKey> FileCryptographyResult<K> decrypt(final K privateKey, final String cipherKey,
                                                                     final File cipherFile, File output) {
        K pk = Objects.requireNonNull(privateKey,"Expected private key");
        try (InputStream is = new FileInputStream(Objects.requireNonNull(cipherFile,"Expected cipher file"));
             OutputStream os = new FileOutputStream(Objects.requireNonNull(output,"Expected output file"))) {
            StreamCryptographyResult<K,OutputStream> result = decrypt(Objects.requireNonNull(pk,
                    "Expected key object"), cipherKey, is, os);
            return new FileCryptographyResult<>() {
                @Override
                public File getFile() {
                    return output;
                }
                @Override
                public K getKey() {
                    return result.getKey();
                }
                @Override
                public Maybe<byte[]> getCipherKey() {
                   return result.getCipherKey();
                }
            };
        } catch (IOException e) {
            throw new CryptographyException("Failed to decrypt file",e);
        }
    }
}
