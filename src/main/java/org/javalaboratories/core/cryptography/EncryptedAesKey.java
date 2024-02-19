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

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import java.io.Serial;

/**
 * Represents the secret-key generated with asymmetric cryptography.
 */
@Value
@AllArgsConstructor
public class EncryptedAesKey implements SecretKey {
    @Serial
    private static final long serialVersionUID = 3551816644582994789L;

    byte[] key;

    /**
     * @return secret-key in encrypted form but in Base64 encoding.
     */
    public String getBase64Key() {
        return Base64.encodeBase64String(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAlgorithm() {
        return "AES";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat() {
        return "RAW";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getEncoded() {
        return this.key.clone();
    }

    /**
     * @return secret-key in encrypted form.
     */
    public byte[] getKey() {
        return getEncoded();
    }
}