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
import org.javalaboratories.core.cryptography.RsaHybridCryptographyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RsaKeysTest {

    private static final String PRIVATE_KEY_FILE = "rsa-private-key-pkcs8.pem";
    private static final String PUBLIC_KEY_FILE = "rsa-public-key.pem";

    private File privateKeyFile;
    private File publicKeyFile;

    private final InputStream mockInputStream = mock(InputStream.class);

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        privateKeyFile = Paths.get(classLoader.getResource(PRIVATE_KEY_FILE).toURI()).toFile();
        publicKeyFile = Paths.get(classLoader.getResource(PUBLIC_KEY_FILE).toURI()).toFile();

        when(mockInputStream.read(any())).thenThrow(IOException.class);
    }
    
    @Test
    public void testGetPrivateKeyFromFile_Pass() {
        PrivateKey privateKey = RsaKeys.getPrivateKeyFrom(privateKeyFile);
        
        assertNotNull(privateKey);
        assertEquals("RSA",privateKey.getAlgorithm());
    }

    @Test
    public void testGetPrivateKeyFromStream_Pass() throws IOException {
        PrivateKey privateKey = RsaKeys.getPrivateKeyFrom(new FileInputStream(privateKeyFile));

        assertNotNull(privateKey);
        assertEquals("RSA",privateKey.getAlgorithm());
    }

    @Test
    public void testGetPrivateKeyFromStream_CryptographyException_Fail() {
        assertThrows(CryptographyException.class, () -> RsaKeys.getPrivateKeyFrom(mockInputStream));
    }

    @Test
    public void testGetPrivateKeyFromStream_GeneralSecurityException_Fail() {
        try (MockedStatic<KeyFactory> keyFactory = Mockito.mockStatic(KeyFactory.class)) {

            keyFactory.when(() -> KeyFactory.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class, () -> RsaKeys.getPrivateKeyFrom(new FileInputStream(privateKeyFile)));
        }
    }

    @Test
    public void testGetPublicKeyFromFile_Pass() {
        PublicKey publicKey = RsaKeys.getPublicKeyFrom(publicKeyFile);

        assertNotNull(publicKey);
        assertEquals("RSA",publicKey.getAlgorithm());
    }

    @Test
    public void testGetPublicKeyFromStream_Pass() throws IOException {
        PublicKey publicKey = RsaKeys.getPublicKeyFrom(new FileInputStream(publicKeyFile));

        assertNotNull(publicKey);
        assertEquals("RSA",publicKey.getAlgorithm());
    }

    @Test
    public void testGetPublicKeyFromStream_CryptographyException_Fail() {
        assertThrows(CryptographyException.class, () -> RsaKeys.getPublicKeyFrom(mockInputStream));
    }

    @Test
    public void testGetPublicKeyFromStream_GeneralSecurityException_Fail() {
        try (MockedStatic<KeyFactory> keyFactory = Mockito.mockStatic(KeyFactory.class)) {

            keyFactory.when(() -> KeyFactory.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class, () -> RsaKeys.getPublicKeyFrom(new FileInputStream(publicKeyFile)));
        }
    }
}
