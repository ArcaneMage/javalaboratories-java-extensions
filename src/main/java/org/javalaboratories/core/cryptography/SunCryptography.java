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

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * All Sun Microsystems based cryptographic classes inherit from this base
 * class.
 */
public abstract class SunCryptography {

    /**
     * Returns a {@link Cipher} class, but only if Sun Microsystems provided it.
     *
     * @param algorithm type of Cipher requested.
     * @return Sun-based Cipher object.
     */
    Cipher getCipher(final String algorithm) {
        Objects.requireNonNull(algorithm);
        Cipher result;
        try {
            result = Cipher.getInstance(algorithm);
            String name = result.getProvider().getName();
            if (!name.contains("Sun"))
                throw new CryptographyException("Not a Sun provider");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CryptographyException("Bad algorithm encountered",e);
        }
        return result;
    }
}
