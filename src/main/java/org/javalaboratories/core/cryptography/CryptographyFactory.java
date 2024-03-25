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

/**
 * This cryptographic factory creates AES, MD5 and other cryptographic objects.
 * <p>
 * It's an abstraction over the JCE, providing a consistent easily understood
 * interfaces. For example:
 * <pre>
 *     {@code
 *         SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *         CryptographyStringResult result = cryptography.encrypt(PASSWORD, STRING_LITERAL);
 *         String encrypted = result.getDataAsBase64();
 *
 *         Outputs -> "d9WYwk6LrIzw8zsNWnijsw=="
 *     }
 * </pre>
 */
public final class CryptographyFactory {

    public static SymmetricCryptography getSymmetricCryptography() {
        return new AesCryptography();
    }

    private CryptographyFactory() {}
}
