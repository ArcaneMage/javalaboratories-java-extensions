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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A class that implements this interface has the ability to generate has values
 * leveraging the {@code message digest} algorithm.
 * <p>
 * The interface supports the hashing of the following types: {@link String},
 * {@link InputStream} and {@link File}. Supported algorithms include {@code
 * MD5}, 128 bits; {@code SHA-1}, 160 bits; {@code SHA-256}, 256 bits; and
 * {@code SHA-512}, 512 bits.
 * <p>
 * Note that {@code MD5} algorithm is supplied for backward compatibility only.
 * It is considered to be weak and vulnerable to attacks, consider using the SHA
 * algorithms.
 */
public interface HashCryptography {

    /**
     * Generates a hash for the given {@code String}, leveraging the supplied
     * algorithm.
     * <p>
     * Use the {@link MessageDigestAlgorithms} for desired {@code digest} length,
     * which can up to 512 bits.
     *
     * @param s the string to hash
     * @param algorithms the algorithm enum.
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    MessageDigestResult hash(final String s, final MessageDigestAlgorithms algorithms);

    /**
     * Generates a hash for the given {@code InputStream}, leveraging the supplied
     * algorithm.
     * <p>
     * Use the {@link MessageDigestAlgorithms} for desired {@code digest} length,
     * which can up to 512 bits.
     *
     * @param is the input stream to hash
     * @param algorithms the algorithm enum.
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    MessageDigestResult hash(final InputStream is, MessageDigestAlgorithms algorithms);

    /**
     * Generates a hash for the given {@code String}, leveraging the supplied
     * algorithm, which is {@code SHA-1}.
     * <p>
     * The default algorithm used for hashing is {@code SHA-1}, so no algorithm
     * is required.
     *
     * @param s the string to hash
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    default MessageDigestResult hash(final String s) {
        return hash(s,MessageDigestAlgorithms.SHA1);
    }

    /**
     * Generates a hash for the given {@code InputStream}, leveraging the supplied
     * algorithm, which is {@code SHA-1}.
     * <p>
     * The default algorithm used for hashing is {@code SHA-1}, so no algorithm
     * is required.
     *
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    default MessageDigestResult hash(final InputStream is) {
        return hash(is,MessageDigestAlgorithms.SHA1);
    }

    /**
     * Generates a hash for the given {@code File}, leveraging the supplied
     * algorithm, which is {@code SHA-1}.
     * <p>
     * The default algorithm used for hashing is {@code SHA-1}, so no algorithm
     * is required.
     *
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    default MessageDigestResult hash(final File file) {
        return hash(file,MessageDigestAlgorithms.SHA1);
    }

    /**
     * Generates a hash for the given {@code File}, leveraging the supplied
     * algorithm.
     * <p>
     * Use the {@link MessageDigestAlgorithms} for desired {@code digest} length,
     * which can up to 512 bits.
     *
     * @param file the input stream to hash
     * @param algorithms the algorithm enum.
     * @return a {@link MessageDigestResult}
     * @throws NullPointerException when parameters are null.
     */
    default MessageDigestResult hash(final File file, MessageDigestAlgorithms algorithms) {
        File f = Objects.requireNonNull(file, "Expected file object");
        try (FileInputStream fis = new FileInputStream(f)) {
            return hash(fis,algorithms);
        } catch (IOException e) {
            throw new CryptographyException("Failed to generate hash for file",e);
        }
    }
}
