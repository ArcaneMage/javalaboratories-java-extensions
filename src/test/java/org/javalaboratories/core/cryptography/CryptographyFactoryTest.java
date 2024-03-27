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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptographyFactoryTest {

    private static final String PASSWORD = "F0xedFence75";
    private static final String STRING_LITERAL = "The quick brown fox jumped over the fence";
    private static final String ENCRYPTED_STRING_KEY_DATA = "xTu2qcvSVqfx5YuMc7jePQ55ne1g4AjdnpgXdalsZSY=:HV4Ab5AhpiFoTLdj0XVQZA==";
    private static final String ENCRYPTED_STRING_DATA = "HV4Ab5AhpiFoTLdj0XVQZM2ABXjJR5TEo9IBL9ySz6odA02jpT+RLEbfza3BPMTaMRIXcOgsoqgVXFyuSLEwKg==";

    private static final String ENCRYPTED_FILE = "aes-encrypted-file.enc";
    private static final String ENCRYPTED_FILE_KEY = "aes-encrypted-file.key";
    private static final String UNENCRYPTED_FILE = "aes-unencrypted-file.txt";
    private static final String ENCRYPTED_FILE_KEY_DATA = "zJbPtats1KXzfJ5j6lISPxRstiP2D6ZnHmO0a9kkdY8=:tV7YKY6dsRa7Be7g1+MuFg==";
    private static final String ENCRYPTED_FILE_TEST = "aes-encrypted-test-file.tmp";
    private static final String UNENCRYPTED_FILE_TEST = "aes-unencrypted-test-file.tmp";
    private static final String FILE_DATA = "This is a test file with encrypted data -- TOP SECRET!";

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
        CryptographyStringResult result = cryptography.encrypt(PASSWORD, STRING_LITERAL);
        String encrypted = result.getDataAsBase64();

        String decoded = cryptography.decrypt(result.getSecrets(),encrypted);
        assertEquals(STRING_LITERAL, decoded);
    }

    @Test
    public void testStringDecryption_Pass() {
        Secrets secrets = Secrets.from(ENCRYPTED_STRING_KEY_DATA);
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        String decoded = cryptography.decrypt(secrets, ENCRYPTED_STRING_DATA);

        assertEquals(STRING_LITERAL,decoded);
    }

    @Test
    public void testSecrets_Pass() {
        Secrets secrets = Secrets.from(ENCRYPTED_STRING_KEY_DATA);
        Secrets secretsFile = Secrets.fromStream(classLoader.getResourceAsStream(ENCRYPTED_FILE_KEY));

        assertEquals(ENCRYPTED_STRING_KEY_DATA,secrets.export());
        assertEquals(ENCRYPTED_FILE_KEY_DATA,secretsFile.export());
    }

    @Test
    public void testFileDecryption_Pass() throws IOException {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        Secrets secrets = Secrets.fromFile(encryptedFileKey);

        CryptographyFileResult result = cryptography
                .decrypt(secrets,encryptedFile, unencryptedFileTest);

        String s = Files.lines(result.getFile().toPath())
                        .collect(Collectors.joining());

        assertEquals(FILE_DATA,s);
    }

    @Test
    public void testFileEncryption_Pass() throws IOException {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyFileResult result = cryptography
                .encrypt(PASSWORD,unencryptedFile, encryptedFileTest);

        CryptographyStreamResult<ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getSecrets(),new FileInputStream(encryptedFileTest),new ByteArrayOutputStream());

        assertEquals(FILE_DATA,result2.getStream().toString());
    }

    @Test
    public void testStreamEncryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStreamResult<ByteArrayOutputStream> result = cryptography
                .encrypt(PASSWORD,new ByteArrayInputStream(STRING_LITERAL.getBytes()),new ByteArrayOutputStream());

        CryptographyStreamResult<ByteArrayOutputStream> result2 = cryptography
                .decrypt(result.getSecrets(),new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL,result2.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        Secrets secrets = Secrets.from(ENCRYPTED_STRING_KEY_DATA);

        CryptographyStreamResult<ByteArrayOutputStream> result = cryptography
                .decrypt(secrets,new ByteArrayInputStream(Base64.getDecoder().decode(ENCRYPTED_STRING_DATA)),
                        new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL,result.getStream().toString());
    }

    @Test
    public void testEncryptionFileCreation_Pass() throws IOException  {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        String s = "This is a test file with encrypted data -- TOP SECRET!";
        CryptographyStreamResult<FileOutputStream> streamResult = cryptography.encrypt(PASSWORD,new ByteArrayInputStream(s.getBytes()),
                new FileOutputStream("aes-encrypted-file.enc"));
        streamResult.getSecrets().exportToFile(new File("aes-encrypted-file.key"));
    }
}
