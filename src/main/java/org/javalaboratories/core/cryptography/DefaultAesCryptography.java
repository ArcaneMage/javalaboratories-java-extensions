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

import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Objects;

import static javax.crypto.Cipher.ENCRYPT_MODE;

public final class DefaultAesCryptography implements AesCryptography {

    private static final int HEADER_SIZE = 16;
    private static final int IV_BYTES = 16;
    private static final int STREAM_BUFFER_SIZE = 512;
    
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * Package-private default constructor.
     * <p>
     * This method is only to be called from the {@link CryptographyFactory}, it
     * must be called directly.
     */
    DefaultAesCryptography() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public <K extends SymmetricSecretKey,T extends OutputStream> StreamCryptographyResult<K,T> decrypt(final K key,
                                                                                                       final InputStream cipherStream,
                                                                                                       final T outputStream) {
        K k = Objects.requireNonNull(key,"Expected key object");
        try {
            // Read IV Header
            IvParameterSpec iv = readIvHeader(cipherStream);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,k,iv);
            write(cipher,cipherStream,outputStream);
            return new StreamCryptographyResultImpl<>(k,outputStream);
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
    public <K extends SymmetricSecretKey,T extends OutputStream> StreamCryptographyResult<K,T> encrypt(final K key,
                                                                                                       final InputStream inputStream,
                                                                                                       final T cipherStream) {
        K k = Objects.requireNonNull(key, "Expected key object");
        try {
            IvParameterSpec iv = generateIvParameterSpec();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(ENCRYPT_MODE,k,iv);

            // Write Prefix IV Header
            Objects.requireNonNull(cipherStream)
                    .write(iv.getIV());

            write(cipher,inputStream,cipherStream);
            return new StreamCryptographyResultImpl<>(k,cipherStream);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        } catch (IOException e) {
            throw new CryptographyException("Failed to process streams",e);
        }
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
            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
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
