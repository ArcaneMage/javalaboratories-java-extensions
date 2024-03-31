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

import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

public interface RsaHybridCryptography {

    <K extends PublicKey> StringCryptographyResult<K> encrypt(final K privateKey, final String string);

    <K extends PublicKey, T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K publicKey,
                                                                                        final InputStream is,
                                                                                        final T cipherStream);

    <K extends PrivateKey> StringCryptographyResult<K> decrypt(final K privateKey, final String cipherKey,
                                                               final String ciphertext);

    <K extends PrivateKey, T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K privateKey,
                                                                                         final String cipherKey,
                                                                                         final InputStream cipherStream,
                                                                                         final T os);

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
