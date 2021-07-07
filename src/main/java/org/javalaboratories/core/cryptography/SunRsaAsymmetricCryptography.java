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
import java.io.*;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
 * @see AsymmetricCryptography
 */
public final class SunRsaAsymmetricCryptography extends SunCryptography implements AsymmetricCryptography{

    // The algorithm name / Electronic Code Book (ECB)  / data filling method
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException for null parameters.
     * @throws CryptographyException or invalid public key or block size errors
     */
    @Override
    public byte[] decrypt(PrivateKey key, byte[] data) {
        Arguments.requireNonNull("Requires private key and data objects",key,data);
        byte[] result;
        try {
            Cipher cipher = getCipher(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,key);
            result = cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptographyException("Padding or block size error",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException for null parameters.
     * @throws CryptographyException or invalid public key or block size errors
     */
    @Override
    public byte[] encrypt(Certificate certificate, byte[] data) {
        Arguments.requireNonNull("Requires certificate and data objects",certificate,data);
        byte[] result;
        try {
            Cipher cipher = getCipher(RSA_ALGORITHM);
            PublicKey key = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            result = cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptographyException("Padding or block size error",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrypt(PrivateKey key, InputStream istream, OutputStream ostream) {
        Arguments.requireNonNull("Requires private key, istream and ostream parameters",key,istream,ostream);
        try {
            Cipher cipher = getCipher(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            InputStream cstream = new CipherInputStream(istream,cipher);
            write(cstream,ostream);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read/write streams",e);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encrypt(Certificate certificate, InputStream istream, OutputStream ostream) {
        Arguments.requireNonNull("Requires certificate, istream and ostream parameters",certificate,istream,ostream);
        try {
            Cipher cipher = getCipher(RSA_ALGORITHM);
            PublicKey key = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            OutputStream cstream = new CipherOutputStream(ostream,cipher);
            write(istream,cstream);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read/write streams",e);
        } catch (InvalidKeyException e) {
            throw new CryptographyException("Invalid key",e);
        }
    }
}
