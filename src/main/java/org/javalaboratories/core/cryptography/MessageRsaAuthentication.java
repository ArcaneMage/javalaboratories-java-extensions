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

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@EqualsAndHashCode
public class MessageRsaAuthentication {

    protected static final int STREAM_BUFFER_SIZE = 4096;

    protected static final String MESSAGE_NOT_SIGNABLE = "Encrypted data is not signable";
    protected static final String DEFAULT_SIGNING_ALGORITHM = "SHA256withRSA";
    protected static final String DEFAULT_KEY_FACTORY_ALGORITHM = "RSA";

    @Getter
    private final MessageDigestAlgorithms algorithm;

    @EqualsAndHashCode.Exclude
    private final RsaHybridCryptography signable;

    public MessageRsaAuthentication(final MessageDigestAlgorithms algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm);
        this.signable = CryptographyFactory.getSignableAsymmetricHybridCryptography(algorithm);
    }

    protected RsaHybridCryptography signable() {
        return this.signable;
    }

}
