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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptographyFactoryTest {

    private static final String PASSWORD = "F0xedFence75";
    private static final String STRING_LITERAL = "The quick brown fox jumped over the fence";
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

    private File encryptedFile;
    private File encryptedFileKey;
    private File unencryptedFile;
    private File encryptedFileTest;
    private File unencryptedFileTest;
    private ClassLoader classLoader;

    @BeforeEach
    public void setup() throws Exception {
       classLoader = CryptographyFactoryTest.class.getClassLoader();
       encryptedFile = Paths.get(classLoader.getResource(ENCRYPTED_FILE).toURI()).toFile();
       encryptedFileKey = Paths.get(classLoader.getResource(ENCRYPTED_FILE_KEY).toURI()).toFile();
       unencryptedFile = Paths.get(classLoader.getResource(UNENCRYPTED_FILE).toURI()).toFile();

       encryptedFileTest = Paths.get(classLoader.getResource(ENCRYPTED_FILE_TEST).toURI()).toFile();
       unencryptedFileTest = Paths.get(classLoader.getResource(UNENCRYPTED_FILE_TEST).toURI()).toFile();
    }

    @Test
    public void testStringEncryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStringResult result = cryptography.encrypt(SymmetricSecretKey.from(PASSWORD), STRING_LITERAL);
        String encrypted = result.getDataAsBase64();

        CryptographyStringResult stringResult = cryptography.decrypt(result.getKey(),encrypted);
        assertEquals(STRING_LITERAL, stringResult.getDataAsString().orElseThrow());
    }


    @Test
    public void testStringDecryption_Pass() {
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStringResult result = cryptography.decrypt(key,ENCRYPTED_STRING_DATA);

        assertEquals(STRING_LITERAL,result.getDataAsString().orElseThrow());
    }

    @Test
    public void testStringDecryption_Fail() {
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();

        assertThrows(CryptographyException.class, () -> cryptography.decrypt(key,TAMPERED_ENCRYPTED_STRING_DATA));
        assertThrows(CryptographyException.class, () -> cryptography.decrypt(key,BAD_ENCRYPTED_STRING_DATA));
    }

    @Test
    public void testFileDecryption_Pass() throws IOException {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        SymmetricSecretKey key = SymmetricSecretKey.from(encryptedFileKey);

        CryptographyFileResult result = cryptography
                .decrypt(key,encryptedFile, unencryptedFileTest);

        String s = Files.lines(result.getFile().toPath())
                        .collect(Collectors.joining());

        assertEquals(FILE_DATA,s);
    }

    @Test
    public void testFileDecryption_Fail() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        SymmetricSecretKey key = SymmetricSecretKey.from(encryptedFileKey);

        assertThrows(CryptographyException.class,() -> cryptography
                .decrypt(key,new File(INVALID_FILE),unencryptedFileTest));
    }

    @Test
    public void testFileEncryption_Pass() throws IOException {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyFileResult result = cryptography
                .encrypt(SymmetricSecretKey.from(PASSWORD),unencryptedFile, encryptedFileTest);

        CryptographyStreamResult<ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getKey(),new FileInputStream(encryptedFileTest),new ByteArrayOutputStream());

        assertEquals(FILE_DATA,result2.getStream().toString());
    }


    @Test
    public void testStreamEncryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStreamResult<ByteArrayOutputStream> result = cryptography
                .encrypt(SymmetricSecretKey.from(PASSWORD),new ByteArrayInputStream(STRING_LITERAL.getBytes()),new ByteArrayOutputStream());

        CryptographyStreamResult<ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getKey(),new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL,result2.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);

        CryptographyStreamResult<ByteArrayOutputStream> result = cryptography
                .decrypt(key,new ByteArrayInputStream(Base64.getDecoder().decode(ENCRYPTED_STRING_DATA)),
                        new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL,result.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Fail() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);

        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(key,new ByteArrayInputStream(TAMPERED_ENCRYPTED_STRING_DATA.getBytes()),new ByteArrayOutputStream()));
        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(key,new ByteArrayInputStream(BAD_ENCRYPTED_STRING_DATA.getBytes()),new ByteArrayOutputStream()));
    }
}
