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

import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;
import org.javalaboratories.core.util.Bytes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public final class DefaultAesSymmetricCryptography implements SymmetricCryptography {

    private static final int FILE_BUFFER_SIZE = 512;
    private static final int HEADER_SIZE = 16;
    private static final int IV_BYTES = 16;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Package-private default constructor.
     * <p>
     * This method is only to be called from the {@link CryptographyFactory}, it
     * must be called directly.
     */
    DefaultAesSymmetricCryptography() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public CryptographyStringResult decrypt(final SymmetricSecretKey key, final String cipherText) {
        String  ct = Objects.requireNonNull(cipherText, "Expected encrypted cipher text");
        SymmetricSecretKey k = Objects.requireNonNull(key, "Expected key object");
        try {
            byte[] ctBytes = Base64.getDecoder().decode(ct);

            // Read IV Header
            IvParameterSpec iv = new IvParameterSpec(ctBytes,0,HEADER_SIZE);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,k,iv);
            byte[] bytes = cipher.doFinal(Bytes.trimLeft(ctBytes,HEADER_SIZE));
            return createStringResult(key,bytes,new String(bytes));
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decrypt cipher text");
        } catch (IllegalArgumentException e) {
            throw new CryptographyException("Failed to decrypt encoded cipher text");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends OutputStream> CryptographyStreamResult<T> decrypt(final SymmetricSecretKey key, final InputStream cipherStream,
                                                                        final T outputStream) {
        SymmetricSecretKey k = Objects.requireNonNull(key,"Expected key object");
        try {
            // Read IV Header
            IvParameterSpec iv = readIvHeader(cipherStream);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,k,iv);
            write(cipher,cipherStream,outputStream);
            return createStreamResult(k,outputStream);
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
    public CryptographyStringResult encrypt(final SymmetricSecretKey key, final String string) {
        SymmetricSecretKey k = Objects.requireNonNull(key, "Expected password");
        String s = Objects.requireNonNull(string, "Expected string to encrypt");
        try {
            IvParameterSpec iv = generateIvParameterSpec();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(ENCRYPT_MODE,k,iv);
            byte[] bytes = cipher.doFinal(s.getBytes());

            // Prefix IV Header
            bytes = Bytes.concat(iv.getIV(),bytes);

            return createStringResult(k,bytes,null);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends OutputStream> CryptographyStreamResult<T> encrypt(final SymmetricSecretKey key, final InputStream inputStream,
                                                                        final T cipherStream) {
        SymmetricSecretKey k = Objects.requireNonNull(key, "Expected key object");
        try {
            IvParameterSpec iv = generateIvParameterSpec();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(ENCRYPT_MODE,k,iv);

            // Write Prefix IV Header
            Objects.requireNonNull(cipherStream)
                    .write(iv.getIV());

            write(cipher,inputStream,cipherStream);
            return createStreamResult(k,cipherStream);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process streams",e);
        }
    }

    private <T extends OutputStream> CryptographyStreamResult<T> createStreamResult(final SymmetricSecretKey key,
                                                                                    final T stream) {
        return new CryptographyStreamResult<>() {
            @Override
            public T getStream() {
                return stream;
            }
            @Override
            public SymmetricSecretKey getKey() {
                return key;
            }
        };
    }

    private CryptographyStringResult createStringResult(final SymmetricSecretKey key, byte[] bytes, String text) {
        return new CryptographyStringResult() {
            @Override
            public SymmetricSecretKey getKey() {
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
        };
    }

    private static IvParameterSpec generateIvParameterSpec() {
        SecureRandom r = new SecureRandom();
        byte[] bytes = new byte[IV_BYTES];
        r.nextBytes(bytes);
        return new IvParameterSpec(bytes);
    }

    private IvParameterSpec readIvHeader(final InputStream cipherStream) throws IOException {
        byte[] bytes = new byte[HEADER_SIZE];
        if (Objects.requireNonNull(cipherStream).read(bytes) == -1)
            throw new IOException("Failed to read Header -- end of stream encountered");
        return new IvParameterSpec(bytes);
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
}
