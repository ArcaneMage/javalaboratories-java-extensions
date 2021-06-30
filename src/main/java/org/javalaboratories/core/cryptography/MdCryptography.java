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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class MdCryptography implements Cryptography {

    private MessageDigest messageDigest;

    public MdCryptography(MdAlgorithms mdAlgorithm) {
        try {
            messageDigest = MessageDigest.getInstance(mdAlgorithm.getAlgorithm());
        } catch(NoSuchAlgorithmException e) {
            // Not possible
        }
    }

    @Override
    public byte[] decrypt(byte[] data) {
        throw new UnsupportedOperationException("Message digest only supports one-way encryption");
    }

    @Override
    public byte[] encrypt(byte[] data) {
        Objects.requireNonNull(data,"data required");
        return messageDigest.digest(data);
    }
}
