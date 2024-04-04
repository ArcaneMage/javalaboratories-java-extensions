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

import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;
import org.javalaboratories.core.util.Bytes;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Objects;

/**
 * {@code DefaultRsaHybridCryptography} supports the RSA encryption of {@code
 * Strings}, {@code Streams} and {@code Files}.
 * <p>
 * Being RSA {@link PublicKey}, {@link PrivateKey} keys are required for
 * encryption and decryption respectively. The term {@code hybrid} means that
 * there are multiple encryption algorithms are required because RSA encryption
 * is not sufficient for encrypting/decrypting large amounts of data. The
 * approach taken for encryption:
 * <ol>
 *     <li>Create/generate a secret key, leveraging AES</li>
 *     <li>Encrypt the the data with AES using the secret key</li>
 *     <li>Encrypt the secret key with RSA public key</li>
 *     <li>Return the {@code ciphertext} with the encrypted key</li>
 * </ol>
 * To decrypt the {@code ciphertext}, the steps are reversed:
 * <ol>
 *     <li>Decrypt the secret key with the RSA private key</li>
 *     <li>Decrypt the {@code ciphertext} with the secret key with AES
 *     decryption</li>
 *     <li>Return deciphered text</li>
 * </ol>
 * The {@code ciphertext} generated by this interface is proprietary in that it
 * stores the encrypted cipher/session key ahead of the encrypted message together.
 * Essentially there are three blocks in the {@code ciphertext}:
 * <pre>
 *     {@code
 *         [session-key-length][rsa-encrypted-session-key][aes-encrypted-message]
 *     }
 * </pre>
 * The above format does away the need for an extra file/stream containing the
 * encrypted session key. This simplifies data transmission over the network.
 *
 * @see RsaHybridCryptography
 */
public final class DefaultRsaHybridCryptography implements RsaHybridCryptography {

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends PublicKey,T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K publicKey,
                                                                                              final InputStream inputStream,
                                                                                              final T cipherStream) {
        K pk = Objects.requireNonNull(publicKey,"Expected public key");
        try (InputStream is = Objects.requireNonNull(inputStream,"Expected input stream");
             T os = Objects.requireNonNull(cipherStream, "Expected cipher stream")) {
            SymmetricSecretKey secretKey = SymmetricSecretKey.newInstance();

            // Encrypt session key with public key
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,pk);
            byte[] sessionKeyBytes = cipher.doFinal(secretKey.getEncoded());

            // Write the RSA encrypted session key to the output stream first
            byte[] sessionKeyBytesSz = Bytes.toByteArray(sessionKeyBytes.length);
            os.write(sessionKeyBytesSz);
            os.write(sessionKeyBytes);

            // Now encrypt message with AES
            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StreamCryptographyResult<SymmetricSecretKey,T> aesResult = aes.encrypt(secretKey,is,os);

            return new StreamCryptographyResultImpl<>(pk,sessionKeyBytes,aesResult.getStream());
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process stream",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends PrivateKey,T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K privateKey,
                                                                                               final InputStream cipherStream,
                                                                                               T outputStream) {
        K pk = Objects.requireNonNull(privateKey,"Expected private key");
        try (InputStream is = Objects.requireNonNull(cipherStream,"Expected cipher stream");
             T os = Objects.requireNonNull(outputStream,"Expected out stream")) {

            // Read the RSA session key first, decrypt and derive AES secret key
            byte[] encryptedSessionKey = readSessionKeyFromStream(cipherStream);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,pk);
            byte[] sessionKeyBytes = cipher.doFinal(encryptedSessionKey);
            SymmetricSecretKey secretKey = SymmetricSecretKey.from(sessionKeyBytes);

            // Decrypt AES message with AES secret key
            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StreamCryptographyResult<SymmetricSecretKey,T> aesResult = aes.decrypt(secretKey,is,os);
            return new StreamCryptographyResultImpl<>(pk,sessionKeyBytes,aesResult.getStream());
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process stream",e);
        }
    }

    private byte[] readSessionKeyFromStream(InputStream stream) throws IOException {
        byte[] b = new byte[4]; // 32bit number encoded
        if (stream.read(b) == -1)
            throw new IOException("Failed to read session key size in stream");
        int sessionKeySize = Bytes.fromBytes(b);
        if (sessionKeySize < 128 || sessionKeySize > 1024)
            throw new IOException("Failed to read session key: corrupted");
        byte[] result = new byte[sessionKeySize];
        if (stream.read(result) == -1)
            throw new IOException("Failed to read encrypted session key in stream");
        return result;
    }
}
