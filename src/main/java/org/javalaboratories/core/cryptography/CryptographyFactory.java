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

import org.javalaboratories.core.cryptography.keys.SymmetricKey;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * This cryptographic factory creates AES, MD5 and other cryptographic objects.
 * <p>
 * It's an abstraction over the JCA/JCE, providing a consistent easily understood
 * interfaces. For example:
 * <pre>
 *     {@code
 *         AesCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *         CryptographyStringResult result =
 *             cryptography.encrypt(SymmetricKey.from(PASSWORD), STRING_LITERAL);
 *         String cipherText = result.getDataAsBase64();
 *
 *         Outputs -> "xGc/N5WQGeje8QHK68GPdUbho0YIX3mYj/Zqgt5wqTjvkAvOLMOp/RGMM8yRn2GBsFRZA=="
 *         ...
 *         ...
 *         AesCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *         CryptographyStringResult result =
 *             cryptography.decrypt(SymmetricKey.from(PASSWORD), cipherText);
 *         String string = result.getDataAsString.orElseThrow();
 *
 *         Outputs -> "The quick brown fox jumped over the fence"
 *     }
 * </pre>
 * In addition to {@code Strings}, support for {@code InputStream} and {@code
 * OutputStream} are also provided, as well as {@code File} encryption/decryption.
 *
 * @see AesCryptography
 * @see SymmetricKey
 */
public final class CryptographyFactory {

    /**
     * Provides an interface for {@code Asymmetric} encryption and decryption,
     * levering RSA standard.
     * <p>
     * Asymmetric cryptography means that there are multiple keys required to
     * encrypt/decrypt {@code ciphertext}, namely {@code PublicKey} and
     * {@code PrivateKey}.
     * <p>
     * Encrypted messages will not have a checksum and therefore cannot be
     * signed or verified. For that requirement, consider the use of the
     * factory method {@link CryptographyFactory#getSignableAsymmetricHybridCryptography(MessageDigestAlgorithms)}.
     *
     * @return {@link RsaHybridCryptography} object is returned, encapsulating
     * RSA encryption/decryption standard.
     */
    public static RsaHybridCryptography getAsymmetricHybridCryptography() {
        return new DefaultRsaHybridCryptography();
    }

    /**
     * Provides an interface for {@code Asymmetric} encryption and decryption,
     * levering RSA standard.
     * <p>
     * Asymmetric cryptography means that there are multiple keys required to
     * encrypt/decrypt {@code ciphertext}, namely {@code PublicKey} and
     * {@code PrivateKey}.
     * <p>
     * Encrypted messages will have a checksum associated and therefore can be
     * signed or verified. If this not a requirement, consider the use of the
     * factory method {@link CryptographyFactory#getAsymmetricHybridCryptography()}
     *
     * @param algorithm the algorithm with which to generate the message checksum.
     * @return {@link RsaHybridCryptography} object is returned, encapsulating
     * RSA encryption/decryption standard.
     */
    public static RsaHybridCryptography getSignableAsymmetricHybridCryptography(final MessageDigestAlgorithms algorithm) {
        return new DefaultRsaHybridCryptography(algorithm);
    }

    /**
     * Provides an interface for {@code Symmetric} encryption and decryption,
     * levering AES standard.
     * <p>
     * Symmetric cryptography means the same key is used for both encryption and
     * decryption.
     *
     * @return {@link AesCryptography} object is returned, encapsulating
     * AES encryption/decryption standard.
     */
    public static AesCryptography getSymmetricCryptography() {
        return new DefaultAesCryptography();
    }

    /**
     * Provides an interface for {@code Message Digest} encryption.
     * <p>
     * This form of encryption is one way, meaning that the resultant cipher
     * text or hash cannot be decrypted. It is primarily used for password
     * hashing and file checksum redundancy checks.
     *
     * @return {@link HashCryptography} implementation is returned, encapsulating
     * Message Digest hashing/encryption.
     */
    public static HashCryptography getHashCryptography() {
        return new DefaultHashCryptography();
    }

    /**
     * Provides an interface for {@code message} signing with the given {@link
     * PrivateKey}.
     *
     * @param key the private key with which to sign the encrypted message.
     *
     * @return {@link RsaMessageSigner} interface implementation.
     */
    public static RsaMessageSigner getMessageSigner(final PrivateKey key) {
        return new DefaultRsaMessageSigner(key);
    }

    /**
     * Provides an interface for {@code message} signing with the given {@link
     * PrivateKey} and {@link MessageDigestAlgorithms} algorithm.
     *
     * @param key the private key with which to sign the encrypted message.
     * @param algorithm the algorithm with which to sign the message.
     *
     * @return {@link RsaMessageSigner} interface implementation.
     */
    public static RsaMessageSigner getMessageSigner(final PrivateKey key, final MessageDigestAlgorithms algorithm) {
        return new DefaultRsaMessageSigner(key, algorithm);
    }

    /**
     * Provides an interface for {@code message} verification.
     * <p>
     * The verification does not require a {@link PublicKey} -- it is extracted
     * from the message structure.
     * <p>
     * The default algorithm used for signing is {@code SHA-256}.
     * @return {@link RsaMessageSigner} interface implementation.
     */
    public static RsaMessageVerifier getMessageVerifier() {
        return getMessageVerifier(MessageDigestAlgorithms.SHA256);
    }

    /**
     * Provides an interface for {@code message} verification with the given
     * {@code algorithm}
     * <p>
     * The verification does not require a {@link PublicKey} -- it is extracted
     * from the message structure.
     *
     * @param algorithm the algorithm with which to verify the message.
     * @return {@link RsaMessageSigner} interface implementation.
     */
    public static RsaMessageVerifier getMessageVerifier(final MessageDigestAlgorithms algorithm) {
        return new DefaultRsaMessageVerifier(algorithm);
    }

    private CryptographyFactory() {}
}
