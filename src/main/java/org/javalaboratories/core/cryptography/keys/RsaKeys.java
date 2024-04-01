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

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RsaKeys {

    private static final String ALGORITHM = "RSA";

    public static PrivateKey getPrivateKeyFrom(final File file) {
        try {
            return getPrivateKeyFrom(new FileInputStream(Objects.requireNonNull(file,"Expected file object")));
        } catch (IOException e) {
            throw new CryptographyException("Failed to read private key file",e);
        }
    }

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

    public static PublicKey getPublicKeyFrom(final File file) {
        try {
            return getPublicKeyFrom(new FileInputStream(Objects.requireNonNull(file,"Expected file object")));
        } catch (IOException e) {
            throw new CryptographyException("Failed to read public key file",e);
        }
    }

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
        return reader.lines()
            .map(l -> l.contains("BEGIN") || l.contains("END") ? "": l)
            .map(l -> l.replace(System.lineSeparator(),""))
            .collect(Collectors.collectingAndThen(Collectors.joining(),
                    s -> Base64.getDecoder().decode(s)));
    }

    private RsaKeys() {}
}
