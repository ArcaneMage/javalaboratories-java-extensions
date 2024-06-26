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

import org.javalaboratories.core.cryptography.keys.SymmetricKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AesSymmetricCryptographyTest {

    private static final String PASSWORD = "F0xedFence75";
    private static final String TEXT = "The quick brown fox jumped over the fence";
    private static final String ENCRYPTED_STRING_DATA = "xGc/N5WQGeje8QHK68GPdUbho0YIX3mYj/Zqt4YcH5zOD6COPDqdRgt5wqTjvkAvOLMOp/RGMM8yRn2GBsFRZA==";
    private static final String TAMPERED_ENCRYPTED_STRING_DATA = "xGc/N5WQGeje8QHK68GPdUbho0YIX3mYj/Zqt4YcH5zOD6COPDqdRgt5wqTjvkAvOLMOp/RGMM8yRn2GBsFRYA==";
    private static final String BAD_ENCRYPTED_STRING_DATA = "7883This is a badly encrypted nonsense==";

    private static final String ENCRYPTED_FILE = "aes-encrypted-file.enc";
    private static final String ENCRYPTED_FILE_KEY = "aes-encrypted-file.key";
    private static final String UNENCRYPTED_FILE = "aes-unencrypted-file.txt";
    private static final String ENCRYPTED_FILE_TEST = "aes-encrypted-test-file.tmp";
    private static final String UNENCRYPTED_FILE_TEST = "aes-unencrypted-test-file.tmp";
    private static final String FILE_DATA = "This is a test file with encrypted data -- TOP SECRET!";

    private static final String INVALID_FILE = "aes-encrypted-file-does-not-exist.tmp";

    private final InputStream mockInputStream = mock(InputStream.class);
    private final InputStream mockEndOfInputStream = mock(InputStream.class);
    private final OutputStream mockOutputStream = mock(OutputStream.class);

    private File encryptedFile;
    private File encryptedFileKey;
    private File unencryptedFile;
    private File encryptedFileTest;
    private File unencryptedFileTest;

    private AesCryptography cryptography;

    @BeforeEach
    public void setup() throws Exception {
       ClassLoader classLoader = AesSymmetricCryptographyTest.class.getClassLoader();
       encryptedFile = Paths.get(classLoader.getResource(ENCRYPTED_FILE).toURI()).toFile();
       encryptedFileKey = Paths.get(classLoader.getResource(ENCRYPTED_FILE_KEY).toURI()).toFile();
       unencryptedFile = Paths.get(classLoader.getResource(UNENCRYPTED_FILE).toURI()).toFile();

       encryptedFileTest = Paths.get(classLoader.getResource(ENCRYPTED_FILE_TEST).toURI()).toFile();
       unencryptedFileTest = Paths.get(classLoader.getResource(UNENCRYPTED_FILE_TEST).toURI()).toFile();

       cryptography = CryptographyFactory.getSymmetricCryptography();

       when(mockInputStream.read(any())).thenThrow(IOException.class);
       doThrow(IOException.class).when(mockOutputStream).write(any());
       when(mockEndOfInputStream.read(any())).thenReturn(-1);
    }

    @Test
    public void testStringEncryption_Pass() {
        ByteCryptographyResult<SymmetricKey> result = cryptography.encrypt(SymmetricKey.from(PASSWORD), TEXT);
        String encrypted = result.getBytesAsBase64();

        ByteCryptographyResult<SymmetricKey> stringResult = cryptography.decrypt(result.getKey(),encrypted);
        assertEquals(TEXT, stringResult.getString().orElseThrow());
    }

    @Test
    public void testStringEncryption_withGeneralSecurityException_Fail() {
        try (MockedStatic<Cipher> cipher = Mockito.mockStatic(Cipher.class)) {
            cipher.when(() -> Cipher.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> cryptography.encrypt(SymmetricKey.from(PASSWORD), TEXT));
        }
    }

    @Test
    public void testStringDecryption_Pass() {
        SymmetricKey key = SymmetricKey.from(PASSWORD);
        ByteCryptographyResult<SymmetricKey> result = cryptography.decrypt(key,ENCRYPTED_STRING_DATA);

        assertEquals(TEXT,result.getString().orElseThrow());
    }

    @Test
    public void testStringDecryption_Fail() {
        SymmetricKey key = SymmetricKey.from(PASSWORD);

        assertThrows(CryptographyException.class, () -> cryptography.decrypt(key,TAMPERED_ENCRYPTED_STRING_DATA));
        assertThrows(CryptographyException.class, () -> cryptography.decrypt(key,BAD_ENCRYPTED_STRING_DATA));
    }

    @Test
    public void testFileDecryption_Pass() throws IOException {
        SymmetricKey key = SymmetricKey.from(encryptedFileKey);

        FileCryptographyResult<SymmetricKey> result = cryptography
                .decrypt(key,encryptedFile, unencryptedFileTest);

        String s = Files.lines(result.getFile().toPath())
                        .collect(Collectors.joining());

        assertEquals(FILE_DATA,s);
    }

    @Test
    public void testFileDecryption_Fail() {
        SymmetricKey key = SymmetricKey.from(encryptedFileKey);

        assertThrows(CryptographyException.class,() -> cryptography
                .decrypt(key,new File(INVALID_FILE),unencryptedFileTest));
    }

    @Test
    public void testFileEncryption_Pass() throws IOException {
        FileCryptographyResult<SymmetricKey> result = cryptography
                .encrypt(SymmetricKey.from(PASSWORD),unencryptedFile, encryptedFileTest);

        StreamCryptographyResult<SymmetricKey,ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getKey(),new FileInputStream(encryptedFileTest),new ByteArrayOutputStream());

        assertEquals(FILE_DATA,result2.getStream().toString());
    }


    @Test
    public void testStreamEncryption_Pass() {
        StreamCryptographyResult<SymmetricKey,ByteArrayOutputStream> result = cryptography
                .encrypt(SymmetricKey.from(PASSWORD),new ByteArrayInputStream(TEXT.getBytes()),new ByteArrayOutputStream());

        StreamCryptographyResult<SymmetricKey,ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getKey(),new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result2.getStream().toString());
    }

    @Test
    public void testStreamEncryption_withGeneralSecurityException_Fail() {
        try (MockedStatic<Cipher> cipher = Mockito.mockStatic(Cipher.class)) {
            cipher.when(() -> Cipher.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> cryptography.encrypt(SymmetricKey.from(PASSWORD),
                    new ByteArrayInputStream(TEXT.getBytes()),new ByteArrayOutputStream()));
        }
    }

    @Test
    public void testStreamEncryption_withIOException_Fail() {
        assertThrows(CryptographyException.class,() -> cryptography.encrypt(SymmetricKey.from(PASSWORD),
                mockInputStream,mockOutputStream));
    }

    @Test
    public void testStreamDecryption_Pass() {
        SymmetricKey key = SymmetricKey.from(PASSWORD);

        StreamCryptographyResult<SymmetricKey,ByteArrayOutputStream> result = cryptography
                .decrypt(key,new ByteArrayInputStream(Base64.getDecoder().decode(ENCRYPTED_STRING_DATA)),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Fail() {
        SymmetricKey key = SymmetricKey.from(PASSWORD);

        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(key,new ByteArrayInputStream(TAMPERED_ENCRYPTED_STRING_DATA.getBytes()),new ByteArrayOutputStream()));
        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(key,new ByteArrayInputStream(BAD_ENCRYPTED_STRING_DATA.getBytes()),new ByteArrayOutputStream()));
    }

    @Test
    public void testStreamDecryption_withIOException_Fail() {
        assertThrows(CryptographyException.class,() -> cryptography.decrypt(SymmetricKey.from(PASSWORD),
                mockInputStream,mockOutputStream));
    }

    @Test
    public void testStreamDecryption_withEndOfStream_Fail() {
        assertThrows(CryptographyException.class,() -> cryptography.decrypt(SymmetricKey.from(PASSWORD),
                mockEndOfInputStream,mockOutputStream));
    }
}
