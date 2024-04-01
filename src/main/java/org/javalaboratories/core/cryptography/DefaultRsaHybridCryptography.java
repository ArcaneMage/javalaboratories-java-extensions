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

import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
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
 * Unlike the symmetric decryption package, RSA decryption will require an
 * additional parameter, the {@code cipherKey} that is the encrypted AES key.
 *
 * @see RsaHybridCryptography
 */
public final class DefaultRsaHybridCryptography implements RsaHybridCryptography {

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends PublicKey> StringCryptographyResult<K> encrypt(final K publicKey, final String string) {
        K pk = Objects.requireNonNull(publicKey,"Expected public key");
        String s = Objects.requireNonNull(string,"Expected string to encrypt");
        try {
            SymmetricSecretKey secretKey = SymmetricSecretKey.newInstance();
            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StringCryptographyResult<SymmetricSecretKey> aesResult = aes.encrypt(secretKey,s);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,pk);
            byte[] cipherKeyBytes = cipher.doFinal(secretKey.getEncoded());
            return createStringResult(pk,cipherKeyBytes,aesResult.getData(),null);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

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
            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();

            StreamCryptographyResult<SymmetricSecretKey,T> aesResult = aes.encrypt(secretKey,is,os);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,pk);
            byte[] cipherKeyBytes = cipher.doFinal(secretKey.getEncoded());
            return createStreamResult(pk,cipherKeyBytes,aesResult.getStream());
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
    public <K extends PrivateKey> StringCryptographyResult<K> decrypt(final K privateKey, final String cipherKey, final String ciphertext) {
        K pk = Objects.requireNonNull(privateKey,"Expected private key");
        String ck = Objects.requireNonNull(cipherKey,"Expected hybrid cipher key");
        String s = Objects.requireNonNull(ciphertext,"Expected string to decrypt");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,pk);
            byte[] cipherKeyBytes = cipher.doFinal(Base64.getDecoder().decode(ck));
            SymmetricSecretKey secretKey = SymmetricSecretKey.from(cipherKeyBytes);

            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StringCryptographyResult<SymmetricSecretKey> result = aes.decrypt(secretKey,s);
            return createStringResult(pk,null,result.getData(),new String(result.getData()));
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends PrivateKey,T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K privateKey,
                                                                                               final String cipherKey,
                                                                                               final InputStream cipherStream,
                                                                                               T outputStream) {
        K pk = Objects.requireNonNull(privateKey,"Expected private key");
        String ck = Objects.requireNonNull(cipherKey,"Expected hybrid cipher key");
        try (InputStream is = Objects.requireNonNull(cipherStream,"Expected cipher stream");
             T os = Objects.requireNonNull(outputStream,"Expected out stream")) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,pk);
            byte[] cipherKeyBytes = cipher.doFinal(Base64.getDecoder().decode(ck));
            SymmetricSecretKey secretKey = SymmetricSecretKey.from(cipherKeyBytes);

            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StreamCryptographyResult<SymmetricSecretKey,T> aesResult = aes.decrypt(secretKey,is,os);
            return createStreamResult(pk,cipherKeyBytes,aesResult.getStream());
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process stream",e);
        }
    }

    private <K extends Key> StringCryptographyResult<K> createStringResult(final K key, final byte[] cipherKey,
                                                                           final byte[] bytes, final String text) {
        return new StringCryptographyResult<>() {
            @Override
            public K getKey() {
                return key;
            }
            @Override
            public byte[] getData() {
                return bytes;
            }
            @Override
            public Maybe<String> getDataAsString() {
                return Maybe.ofNullable(text);
            }
            @Override
            public Maybe<byte[]> getCipherKey() {
                return Maybe.of(cipherKey);
            }
        };
    }

    private <K extends Key, T extends OutputStream> StreamCryptographyResult<K,T> createStreamResult(final K key,
                                                                                                     final byte[]cipherKey,
                                                                                                     final T stream) {
        return new StreamCryptographyResult<>() {
            @Override
            public T getStream() {
                return stream;
            }
            @Override
            public K getKey() {
                return key;
            }
            @Override
            public Maybe<byte[]> getCipherKey() {
                return Maybe.of(cipherKey);
            }
        };
    }
}
