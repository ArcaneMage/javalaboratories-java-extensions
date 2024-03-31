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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface RsaCryptography {

    <K extends PublicKey> StringCryptographyResult<K> encrypt(final K key, final String string);

    <K extends PublicKey, T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K key, final InputStream is, final T cipherStream);

    <K extends PrivateKey> StringCryptographyResult<K>  decrypt(final K key, final String ciphertext);

    <K extends PrivateKey, T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K key, final InputStream cipherStream, final T os);

    default <K extends PublicKey> FileCryptographyResult<K> encrypt(final K key, final File inputFile, File cipherFile) {
        return null;
    }

    default <K extends PrivateKey> FileCryptographyResult<K> decrypt(final K key, final File cipherFile, File outputFile) {
        return null;
    }
}
