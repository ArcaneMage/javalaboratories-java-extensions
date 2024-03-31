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

import org.apache.commons.codec.binary.Hex;

import java.util.Base64;

/**
 * An implementation of this object returned after the execution of a hash
 * function in the {@link HashCryptography} object.
 * <p>
 * For convenience, it comes supplied with useful transformation properties:
 * {@code asBase64} amd {@code asHex}.
 */
public interface MessageDigestResult {

    /**
     * Returns the computed hash.
     * <p>
     * The number of bytes returned depends on the algorithm used to compute
     * the hash. For example, MD5 will generate 128 bits, 16 bytes.
     *
     * @return the hash in bytes.
     */
    byte[] getHash();

    /**
     * Returns the computed hash as Base64 format.
     *
     * @return the hash in Base64 form.
     */
    default String getHashAsBase64() {
        return Base64.getEncoder().encodeToString(getHash());
    }

    /**
     * Returns the computed hash as hexadecimal format.
     *
     * @return the hash in hexadecimal form.
     */
    default String getHashAsHex() {
        return Hex.encodeHexString(getHash()).toUpperCase();
    }
}
