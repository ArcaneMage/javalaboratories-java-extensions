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

import org.javalaboratories.core.cryptography.keys.Secrets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public final class AesCryptography implements SymmetricCryptography {

    private static final int FILE_BUFFER_SIZE = 512;
    private static final int IV_BYTES = 16;
    private static final int SALT_BYTES = 32;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA256";

    /**
     * Package-private default constructor.
     * <p>
     * This method is only to be called from the {@link CryptographyFactory}, it
     * must be called directly.
     */
    AesCryptography() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(final Secrets secrets, final String cipherText) {
        String  ct = Objects.requireNonNull(cipherText, "Expected encrypted cipher text");
        Secrets s = Objects.requireNonNull(secrets, "Expected secrets object");

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,s.key(),s.ivParameterSpec());
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(ct));
            return new String(bytes);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decrypt cipher text");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends OutputStream> CryptographyStreamResult<T> decrypt(final Secrets secrets, final InputStream cipherStream,
                                                                        final T outputStream) {
        Secrets s = Objects.requireNonNull(secrets,"Expected secrets object");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,s.key(),s.ivParameterSpec());
            write(cipher,cipherStream,outputStream);
            return createStreamResult(secrets,outputStream);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decrypt cipher text stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process streams",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CryptographyStringResult encrypt(final String password, final String string) {
        String p = Objects.requireNonNull(password, "Expected password");
        String s = Objects.requireNonNull(string, "Expected string to encrypt");

        Secrets secrets = createSecretsFromPassword(p);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(ENCRYPT_MODE, secrets.key(), secrets.ivParameterSpec());
            byte[] bytes = cipher.doFinal(s.getBytes());
            return createStringResult(secrets,bytes);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends OutputStream> CryptographyStreamResult<T> encrypt(final String password, final InputStream inputStream,
                                                                        final T cipherStream) {
        String p = Objects.requireNonNull(password, "Expected password");

        try {
            Secrets secrets = createSecretsFromPassword(p);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(ENCRYPT_MODE, secrets.key(), secrets.ivParameterSpec());
            write(cipher,inputStream,cipherStream);
            return createStreamResult(secrets,cipherStream);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process streams",e);
        }
    }

    private <T extends OutputStream> CryptographyStreamResult<T> createStreamResult(Secrets secrets,T stream) {
        return new CryptographyStreamResult<>() {
            @Override
            public T getStream() {
                return stream;
            }
            @Override
            public Secrets getSecrets() {
                return secrets;
            }
        };
    }

    private CryptographyStringResult createStringResult(final Secrets secrets, byte[] bytes) {
        return new CryptographyStringResult() {
            @Override
            public Secrets getSecrets() {
                return secrets;
            }
            @Override
            public byte[] getData() {
                return bytes;
            }
            @Override
            public String getDataAsBase64() {
                return Base64.getEncoder().encodeToString(getData());
            }
        };
    }

    private Secrets createSecretsFromPassword(final String password) {
        String salt = getSecureRandomBytes(SALT_BYTES).asBase64();
        IvParameterSpec iv = new IvParameterSpec(getSecureRandomBytes(IV_BYTES).bytes());
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            KeySpec spec = new PBEKeySpec(password.toCharArray(),salt.getBytes(),65536,256);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return new Secrets(key,iv);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptographyException("Failed to create secrets from password",e);
        }
    }

    private void write(final Cipher cipher, final InputStream inputStream, final OutputStream outputStream)
            throws IOException, GeneralSecurityException {
        try (InputStream is = Objects.requireNonNull(inputStream,"Expected input stream");
             OutputStream os = Objects.requireNonNull(outputStream,"Expected output stream")) {
            byte[] buffer = new byte[FILE_BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, length);
                if (output != null)
                    os.write(output);
            }
            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null)
                os.write(finalBytes);
        }
    }

    private static SecureRandomBytes getSecureRandomBytes(final int byteSize) {
        SecureRandom r = new SecureRandom();
        byte[] bytes = new byte[byteSize];
        r.nextBytes(bytes);
        return new SecureRandomBytes(bytes,Base64.getEncoder().encodeToString(bytes));
    }

    private record SecureRandomBytes(byte[] bytes, String asBase64) {}
}
