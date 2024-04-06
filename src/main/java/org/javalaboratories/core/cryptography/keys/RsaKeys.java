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
package org.javalaboratories.core.cryptography.keys;

import org.javalaboratories.core.cryptography.CryptographyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This utility class provides useful tools with which to read RSA keys from
 * file or stream.
 * <p>
 * Both public and private key file/stream formats are expected to be in PEM
 * or CER form.
 */
public final class RsaKeys {

    private static final String ALGORITHM = "RSA";

    /**
     * Reads private RSA key from the given file.
     *
     * @param file the RSA key file
     * @return an instance of the PrivateKey
     */
    public static PrivateKey getPrivateKeyFrom(final File file) {
        try {
            return getPrivateKeyFrom(new FileInputStream(Objects.requireNonNull(file,"Expected file object")));
        } catch (IOException e) {
            throw new CryptographyException("Failed to read private key file",e);
        }
    }

    /**
     * Reads private RSA key from the given file.
     *
     * @param inputStream the RSA key stream -- expected format is PEM/CER.
     * @return an instance of the PrivateKey.
     */
    public static PrivateKey getPrivateKeyFrom(final InputStream inputStream) {
        InputStream is = Objects.requireNonNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            byte[] bytes = keyDataToBytes(reader);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            return factory.generatePrivate(spec);
        } catch(IOException e) {
            throw new CryptographyException("Failed to read input stream",e);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to create private key",e);
        }
    }

    /**
     * Reads public RSA key from the given file.
     *
     * @param file the RSA key file
     * @return an instance of the PublicKey.
     */
    public static PublicKey getPublicKeyFrom(final File file) {
        try {
            return getPublicKeyFrom(new FileInputStream(Objects.requireNonNull(file,"Expected file object")));
        } catch (IOException e) {
            throw new CryptographyException("Failed to read public key file",e);
        }
    }

    /**
     * Reads public RSA key from the given file.
     *
     * @param inputStream the RSA key stream -- expected format is PEM/CER.
     * @return an instance of the PublicKey.
     */
    public static PublicKey getPublicKeyFrom(final InputStream inputStream) {
        InputStream is = Objects.requireNonNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            byte[] bytes = keyDataToBytes(reader);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            return factory.generatePublic(spec);
        } catch(IOException e) {
            throw new CryptographyException("Failed to read input stream",e);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to create private key",e);
        }
    }

    private static byte[] keyDataToBytes(final BufferedReader reader) {
        try {
            return reader.lines()
                    .map(l -> l.contains("BEGIN") || l.contains("END") ? "" : l)
                    .map(l -> l.replace(System.lineSeparator(), ""))
                    .collect(Collectors.collectingAndThen(Collectors.joining(),
                            s -> Base64.getDecoder().decode(s)));
        } catch (UncheckedIOException e) {
            throw new CryptographyException("Failed to read key data in stream",e);
        }
    }

    private RsaKeys() {}
}
