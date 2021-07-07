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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * This object has the ability to encrypt data with asymmetric keys
 * (public/private keys).
 * <p>
 * A {@link PrivateKey} is required to decrypt the data; it's normally is
 * sourced from a file or {@link java.security.KeyStore}. However, to encrypt
 * the data, {@link Certificate} object, preferably X509, is required. The
 * {@link PrivateKey} and {@link Certificate} are related in that they form
 * a private/public keypair respectively. For more details on how to create
 * these keypairs, see https://lightbend.github.io/ssl-config/CertificateGeneration.html.
 * An example of usage helps to clarify the expectation of this interface:
 * <pre>
 *     {@code
 *         // Encryption example
 *         AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
 *         CertificateFactory factory = CertificateFactory.getInstance("X.509");
 *         Certificate certificate = factory.generateCertificate(new FileInputStream(...));
 *
 *         byte[] result = cryptography.encrypt(certificate,DATA.getBytes());
 *         ...
 *         ...
 *         // Decryption example
 *         AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
 *         KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
 *         PrivateKey key;
 *         try {
 *             store.load(new FileInputStream(KEYSTORE_FILE), "changeit".toCharArray());
 *             key = (PrivateKey) store.getKey("javalaboratories-org","65533714".toCharArray());
 *         } catch (IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
 *             throw new IllegalStateException("Failed to read keystore",e);
 *         }
 *         byte[] result = cryptography.decrypt(key,Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_DATA.getBytes()));
 *         String data = new String(result);
 *
 *         assertEquals("The quick brown fox jumped over the fence",data);
 *         ...
 *         ...
 *     }
 * </pre>
 * It is recommended to use the {@link CryptographyFactory} to call upon
 * implementations of this interface. The reason being this library is likely
 * to provide different implementations to satisfy specific use cases.
 *
 * @see CryptographyFactory
 * @see SunRsaAsymmetricCryptography
 */
public interface AsymmetricCryptography {

    /**
     * Decrypts {@code data} with provided {@link PrivateKey}.
     * <p>
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param key private key with which to decrypt the {@code data}
     * @param data encrypted data to undergo decryption.
     * @return decrypted data bytes.
     */
    byte[] decrypt(final PrivateKey key, final byte[] data);

    /**
     * Encrypts {@code data} with provided {@link PrivateKey}.
     * <p>
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param certificate public key with which to encrypt the {@code data}
     * @param data decrypted data to undergo encryption.
     * @return decrypted data bytes.
     */
    byte[] encrypt(final Certificate certificate, final byte[] data);


    /**
     * Decrypts {@code istream} and sends decrypted data to {@code ostream}.
     *
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param key private key with which to encrypt the {@code istream}
     * @param istream input stream of data to be decrypted.
     * @param ostream output stream of decrypted data.
     */
    void decrypt(final PrivateKey key, final InputStream istream, OutputStream ostream);

    /**
     * Encrypts {@code istream} and sends encrypted data to {@code ostream}.
     *
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param certificate public key with which to encrypt the {@code istream}
     * @param istream input stream of data to be encrypted.
     * @param ostream output stream of encrypted data.
     */
    void encrypt(final Certificate certificate, final InputStream istream, OutputStream ostream);
}
