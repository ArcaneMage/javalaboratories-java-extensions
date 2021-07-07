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
 *          Cryptography cryptography = CryptographyFactory.getSunCryptography();
 *          byte[] result = cryptography.encrypt("Hello World".getBytes());
 *          ...
 *          System.out.println(Base64.encodeBase64String(result))
 *          ...
 *          Outputs -> "d9WYwk6LrIzw8zsNWnijsw=="
 *     }
 * </pre>
 */
public final class CryptographyFactory {

    /**
     * Default constructor made private to inhibit instantiation.
     */
    private CryptographyFactory() {}

    /**
     * Returns {@link Cryptography} object that does not require {@code keys}
     * to enable encryption and decryption.
     *
     * @return Cryptography interface implementation that supports Advance
     * Encryption Standard.
     */
    public static Cryptography getSunCryptography() {
        return getSunCryptography(AesKeyLengths.BITS_128);
    }

    /**
     * Returns {@link Cryptography} object that does not require {@code keys}
     * to enable encryption and decryption.
     *
     * @param keyLength length of bits required for key.
     * @return Cryptography interface implementation that supports Advance
     * Encryption Standard.
     */
    public static Cryptography getSunCryptography(final AesKeyLengths keyLength) {
        return new SunAesSymmetricCryptography(keyLength);
    }

    /**
     * Returns {@link SymmetricCryptography} object that requires {@code
     * keys} to enable encryption and decryption.
     *
     * @return SymmetricCryptography interface implementation that supports
     * Advance Encryption Standard.
     */
    public static SymmetricCryptography getSunSymmetricCryptography() {
        return getSunSymmetricCryptography(AesKeyLengths.BITS_128);
    }

    /**
     * Returns {@link SymmetricCryptography} object that requires {@code
     * keys} to enable encryption and decryption.
     *
     * @param keyLength length of bits required for key.
     * @return SymmetricCryptography interface implementation that supports
     * Advance Encryption Standard.
     */
    public static SymmetricCryptography getSunSymmetricCryptography(final AesKeyLengths keyLength) {
        return new SunAesSymmetricCryptography(keyLength);
    }

    /**
     * Returns {@link Cryptography} object that does not require {@code keys}
     * to enable encryption and decryption.
     *
     * @return Cryptography interface implementation that supports Message
     * Digest hashing.
     */
    public static Cryptography getSunMdCryptography() {
        return getSunMdCryptography(MdAlgorithms.MD5);
    }

    /**
     * Returns {@link Cryptography} object that does not require {@code keys}
     * to enable encryption and decryption.
     *
     * @param algorithm algorithm to apply to perform message digest hashing.
     * @return Cryptography interface implementation that supports Message
     * Digest hashing.
     */
    public static Cryptography getSunMdCryptography(MdAlgorithms algorithm) {
        return new MdCryptography(algorithm);
    }

    /**
     * Returns {@link AsymmetricCryptography} object that require {@code
     * keypairs} to enable encryption and decryption.
     *
     * @return Cryptography interface implementation that supports Message
     * Digest hashing.
     */
    public static AsymmetricCryptography getSunAsymmetricCryptography() {
        return new SunRsaAsymmetricCryptography();
    }
}
