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

import org.javalaboratories.core.util.Arguments;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
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
public final class SunRsaAsymmetricCryptography extends SunCryptography implements AsymmetricCryptography {

    private static final String AES_ALGORITHM = "AES";
    // The algorithm name / Electronic Code Book (ECB) / data filling method
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException for null parameters.
     * @throws CryptographyException or invalid public key or block size errors
     */
    @Override
    public CryptographyResult decrypt(PrivateKey key, EncryptedAesKey encryptedKey, byte[] data) {
        Arguments.requireNonNull("Requires private key and data objects",key,encryptedKey,data);

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        decrypt(key, encryptedKey, new ByteArrayInputStream(data), ostream);
        byte[] result = ostream.toByteArray();
        return new CryptographyResult() {
            @Override
            public byte[] getData() {
                return  result;
            }
            @Override
            public EncryptedAesKey getEncryptedKey() {
                return encryptedKey;
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException for null parameters.
     * @throws CryptographyException or invalid public key or block size errors
     */
    @Override
    public CryptographyResult encrypt(Certificate certificate, byte[] data) {
        Arguments.requireNonNull("Requires certificate and handler objects",certificate,data);

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        EncryptedAesKey eobject = encrypt(certificate, new ByteArrayInputStream(data), ostream);
        byte[] result = ostream.toByteArray();
        return new CryptographyResult() {
            @Override
            public byte[] getData() {
                return result;
            }
            @Override
            public EncryptedAesKey getEncryptedKey() {
                return eobject;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrypt(PrivateKey key, EncryptedAesKey encryptedKey, InputStream istream, OutputStream ostream) {
        Arguments.requireNonNull("Requires private key, encryptedKey, istream and ostream parameters",key,encryptedKey,
                istream,ostream);
        try {
            // Decrypt AES encrypted symmetric key with RSA
            Cipher cipher = getCipher(RSA_ALGORITHM);
            cipher.init(Cipher.PRIVATE_KEY, key);
            byte[] decryptedKey = cipher.doFinal(encryptedKey.getEncoded());

            // Convert bytes to AES SecretKey
            SecretKey secretKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
            cipher = getCipher(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            InputStream cstream = new CipherInputStream(istream,cipher);

            write(cstream,ostream);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read/write streams",e);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptographyException("Block size or bad padding",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EncryptedAesKey encrypt(Certificate certificate, InputStream istream, OutputStream ostream) {
        Arguments.requireNonNull("Requires certificate, istream and ostream parameters",certificate,istream,ostream);
        byte[] encryptedKey;
        try {
            // Generate symmetric key (AES with 128bits)
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            SecretKey secretKey = generator.generateKey();

            // Encrypt data with secretKey symmetric key
            Cipher cipher = getCipher(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            OutputStream cstream = new CipherOutputStream(ostream,cipher);
            write(istream,cstream);

            // Encrypted symmetric key
            cipher = getCipher(RSA_ALGORITHM);
            PublicKey key = certificate.getPublicKey();
            cipher.init(Cipher.PUBLIC_KEY, key);
            encryptedKey = cipher.doFinal(secretKey.getEncoded());
        } catch (IOException e) {
            throw new CryptographyException("Failed to read/write streams",e);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptographyException("Padding or block size error", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographyException("Algorithm error", e);
        }
        return new EncryptedAesKey(encryptedKey);
    }
}
