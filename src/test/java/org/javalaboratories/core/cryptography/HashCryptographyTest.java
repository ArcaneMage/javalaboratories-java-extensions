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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HashCryptographyTest {

    private HashCryptography cryptography;

    private static final String TEXT = "The quick brown fox jumped over the fence";
    private static final String BASE64_128BITS = "szXZywC4mbxlE+zbshhwhw==";
    private static final String HEX_128BITS = "B335D9CB00B899BC6513ECDBB2187087";

    private static final String BASE64_160BITS = "1ccuMJ2E5F6VQxTSQpOYQzO5Pb4=";
    private static final String HEX_160BITS = "D5C72E309D84E45E954314D24293984333B93DBE";

    private static final String BASE64_256BITS = "pUUZqlAmlZ87Mc6/zsLnal8FhncpWdKiIF0Tcq4Tbhc=";
    private static final String HEX_256BITS = "A54519AA5026959F3B31CEBFCEC2E76A5F0586772959D2A2205D1372AE136E17";

    private static final String BASE64_512BITS = "tlROj+WnzT6DLromCTHXjBhlrLQRYO5aZH+ARfjUJ8+AnaH8QX+H+/MHOaSeZ1P6jjWD2QFGdNQxgwQQX8wv8g==";
    private static final String HEX_512BITS = "B6544E8FE5A7CD3E832EBA260931D78C1865ACB41160EE5A647F8045F8D427CF809DA1FC417F87FBF30739A49E6753FA8E3583D9014674D4318304105FCC2FF2";

    private final InputStream mockInputStream = mock(InputStream.class);

    @BeforeEach
    public void setup() throws IOException {
        cryptography = CryptographyFactory.getHashCryptography();

        when(mockInputStream.read(any())).thenThrow(IOException.class);
    }

    @Test
    public void testStringMd5Hashing_Pass() {
        MessageDigestResult result = cryptography.hash(TEXT,MessageDigestAlgorithms.MD5);

        assertEquals(16,result.getHash().length);
        assertEquals(BASE64_128BITS,result.getHashAsBase64());
        assertEquals(HEX_128BITS,result.getHashAsHex());
    }

    @Test
    public void testString160bitsHashing_Pass() {
        MessageDigestResult result = cryptography.hash(TEXT);

        assertEquals(20,result.getHash().length);
        assertEquals(BASE64_160BITS,result.getHashAsBase64());
        assertEquals(HEX_160BITS,result.getHashAsHex());
    }

    @Test
    public void testString256bitsHashing_Pass() {
        MessageDigestResult result = cryptography.hash(TEXT,MessageDigestAlgorithms.SHA256);

        assertEquals(32,result.getHash().length);
        assertEquals(BASE64_256BITS,result.getHashAsBase64());
        assertEquals(HEX_256BITS,result.getHashAsHex());
    }

    @Test
    public void testString512bitsHashing_Pass() {
        MessageDigestResult result = cryptography.hash(TEXT,MessageDigestAlgorithms.SHA512);

        assertEquals(64,result.getHash().length);
        assertEquals(BASE64_512BITS,result.getHashAsBase64());
        assertEquals(HEX_512BITS,result.getHashAsHex());
    }

    @Test
    public void testStringHashing_withNoSuchAlgorithmException_Fail() {
        try (MockedStatic<MessageDigest> md = Mockito.mockStatic(MessageDigest.class)) {
            md.when(() -> MessageDigest.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> cryptography.hash(TEXT));
        }
    }

    @Test
    public void testStreamMd5Hashing_Pass() {
        MessageDigestResult result = cryptography.hash(new ByteArrayInputStream(TEXT.getBytes()),MessageDigestAlgorithms.MD5);

        assertEquals(16,result.getHash().length);
        assertEquals(BASE64_128BITS,result.getHashAsBase64());
        assertEquals(HEX_128BITS,result.getHashAsHex());
    }

    @Test
    public void testStream160bitsHashing_Pass() {
        MessageDigestResult result = cryptography.hash(new ByteArrayInputStream(TEXT.getBytes()));

        assertEquals(20,result.getHash().length);
        assertEquals(BASE64_160BITS,result.getHashAsBase64());
        assertEquals(HEX_160BITS,result.getHashAsHex());
    }

    @Test
    public void testStreamHashing_withIOException_Fail()  {
        assertThrows(CryptographyException.class, () -> cryptography.hash(mockInputStream));
    }

    @Test
    public void testStreamHashing_withNoSuchAlgorithmException_Fail() {
        try (MockedStatic<MessageDigest> md = Mockito.mockStatic(MessageDigest.class)) {
            md.when(() -> MessageDigest.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> cryptography.hash(new ByteArrayInputStream(TEXT.getBytes())));
        }
    }

    @Test
    public void testStream256bitsHashing_Pass() {
        MessageDigestResult result = cryptography.hash(new ByteArrayInputStream(TEXT.getBytes()),
                MessageDigestAlgorithms.SHA256);

        assertEquals(32,result.getHash().length);
        assertEquals(BASE64_256BITS,result.getHashAsBase64());
        assertEquals(HEX_256BITS,result.getHashAsHex());
    }

    @Test
    public void testFileMd5Hashing_Pass() throws URISyntaxException {
        ClassLoader classLoader = HashCryptographyTest.class.getClassLoader();
        MessageDigestResult result = cryptography.hash(Paths.get(classLoader.getResource("hash-unencrypted-file.txt")
                .toURI()).toFile(),MessageDigestAlgorithms.MD5);

        assertEquals(16,result.getHash().length);
        assertEquals(BASE64_128BITS,result.getHashAsBase64());
        assertEquals(HEX_128BITS,result.getHashAsHex());
    }

    @Test
    public void testFile160bitsHashing_Pass() throws URISyntaxException {
        ClassLoader classLoader = HashCryptographyTest.class.getClassLoader();
        MessageDigestResult result = cryptography.hash(Paths.get(classLoader.getResource("hash-unencrypted-file.txt")
                .toURI()).toFile());
        assertEquals(20,result.getHash().length);
        assertEquals(BASE64_160BITS,result.getHashAsBase64());
        assertEquals(HEX_160BITS,result.getHashAsHex());
    }

    @Test
    public void testFile256bitsHashing_Pass() throws URISyntaxException {
        ClassLoader classLoader = HashCryptographyTest.class.getClassLoader();
        MessageDigestResult result = cryptography.hash(Paths.get(classLoader.getResource("hash-unencrypted-file.txt")
                .toURI()).toFile(),MessageDigestAlgorithms.SHA256);

        assertEquals(32,result.getHash().length);
        assertEquals(BASE64_256BITS,result.getHashAsBase64());
        assertEquals(HEX_256BITS,result.getHashAsHex());    }
}
