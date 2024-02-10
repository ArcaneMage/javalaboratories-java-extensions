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
 * a private/public keypair respectively. Moreover, encrypting the data will
 * generate an encrypted symmetric key with which the data is encrypted.
 * This key will require the {@link PrivateKey} to decrypt it to its original
 * form, then the data is subsequently decrypted with the symmetric key.
 *
 * For more details on how to create
 * these keypairs, see https://lightbend.github.io/ssl-config/CertificateGeneration.html.
 * An example of usage helps to clarify the expectation of this interface:
 * <pre>
 *     {@code
 *         // Encryption example
 *         AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
 *         CertificateFactory factory = CertificateFactory.getInstance("X.509");
 *         Certificate certificate = factory.generateCertificate(new FileInputStream(...));
 *
 *         CryptographyResult result = cryptography.encrypt(certificate,DATA.getBytes());
 *         ...
 *         ...
 *         // Decryption example
 *         AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
 *         PrivateKey key = privateKeyStore.getEncryptedKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD);
 *
 *         CryptographyResult result = cryptography.decrypt(key,encryptedSecretKey,encryptedData);
 *         String data = new String(result);
 *         assertEquals("The quick brown fox jumped over the fence",data);
 *         ...
 *         ...
 *     }
 * </pre>
 * It is recommended to use the {@link CryptographyFactory} to call upon
 * implementations of this interface. The reason being this library is likely
 * to provide different implementations to satisfy specific use cases.
 *
 * @see CryptographyResult
 * @see CryptographyFactory
 * @see SunRsaAsymmetricCryptography
 */
@Deprecated
public interface AsymmetricCryptography {

    /**
     * Decrypts {@code data} with provided {@link PrivateKey}.
     * <p>
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param key private key with which to decrypt the {@code data}
     * @param encryptedAesKey the secret-key.
     * @param data encrypted data to undergo decryption.
     * @return CryptographyResult encapsulating decrypted data bytes and
     * encrypted secret-key used.
     */
    CryptographyResult decrypt(PrivateKey key, EncryptedAesKey encryptedAesKey, byte[] data);

    /**
     * Encrypts {@code data} with provided {@link PrivateKey}.
     * <p>
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively.
     *
     * @param certificate public key with which to encrypt the {@code data}
     * @param data decrypted data to undergo encryption.
     * @return CryptographyResult encapsulating decrypted data bytes and new
     * encrypted secret-key.
     */
    CryptographyResult encrypt(Certificate certificate, byte[] data);


    /**
     * Decrypts {@code istream} and sends decrypted data to {@code ostream}.
     *
     * The {@code data} must be encrypted with related {@code certificate}. In
     * other words, the {@link PrivateKey} and {@link Certificate} are related,
     * they form a private/public keypair respectively. Additionally, the
     * secret-key is required, generated from original encryption.
     *
     * @param key private key with which to encrypt the {@code istream}
     * @param encryptedKey encrypted secret-key.
     * @param istream input stream of data to be decrypted.
     * @param ostream output stream of decrypted data.
     */
    void decrypt(PrivateKey key, EncryptedAesKey encryptedKey, InputStream istream, OutputStream ostream);

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
     * @return newly generated secret-key to be used by decryption process.
     */
    EncryptedAesKey encrypt(Certificate certificate, InputStream istream, OutputStream ostream);
}
