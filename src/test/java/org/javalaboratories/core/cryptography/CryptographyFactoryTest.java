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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CryptographyFactoryTest {

    private static final String PASSWORD = "F0xedFence75";
    private static final String STRING_LITERAL = "The quick brown fox jumped over the fence";
    private static final String ENCRYPTED_STRING_KEY_DATA ="2luLVfxQ36Sm2zJduR78fVEXsJhc1XPJMB39HxYxfKY=:C9jHCw5JZ9QrCf9VmzzcSg==:9n5gO/ToBn9Uw0BiTbxoqUD2NjXEdKdvnHIzXrsoS8g=";
    private static final String ENCRYPTED_STRING_DATA ="NK7qCuERH2Jt0vwI2L30UOi/iEsgkEzJ5tCS+/+mQHkMsC5isE2dRoOJdGOSEHsq";

    private static final String ENCRYPTED_FILE = "/aes-encrypted-file.enc";
    private static final String ENCRYPTED_FILE_KEY = "/aes-encrypted-file.key";
    private static final String ENCRYPTED_FILE_KEY_DATA = "UqjkUwrKBA2MkMQk8D65NTxR2pUM/eC6tRyXXB8LJ+Q=:l3vnLUyav/6zkZ5mm+V8Cg==:WYXA2BKqMfgR2qu6pNwxrpFzWyFNt1fGcA6VN/WUzaQ=";
    private static final String ENCRYPTED_FILE_TEST = "aes-encrypted-file-test.enc";
    private static final String FILE_DATA = "This is a test file with encrypted data -- TOP SECRET!";

    @AfterEach
    public void tearDown() {
        new File(ENCRYPTED_FILE_TEST).delete();
    }

    @Test
    public void testEncryptionDecryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStringResult result = cryptography.encrypt(PASSWORD, STRING_LITERAL);
        String encrypted = result.getDataAsBase64();

        String decoded = cryptography.decrypt(result.getSecrets(),encrypted);
        assertEquals(STRING_LITERAL, decoded);
    }

    @Test
    public void testDecryption_Pass() {
        Secrets secrets = Secrets.from(ENCRYPTED_STRING_KEY_DATA);
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        String decoded = cryptography.decrypt(secrets, ENCRYPTED_STRING_DATA);

        assertEquals(STRING_LITERAL,decoded);
    }

    @Test
    public void testSecrets_Pass() {
        Secrets secrets = Secrets.from(ENCRYPTED_STRING_KEY_DATA);
        Secrets secretsFile = Secrets.fromStream(CryptographyFactoryTest.class.getResourceAsStream(ENCRYPTED_FILE_KEY));

        assertEquals(ENCRYPTED_STRING_KEY_DATA,secrets.export());
        assertEquals(ENCRYPTED_FILE_KEY_DATA,secretsFile.export());
    }

    @Test
    public void testFileDecryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        Secrets secrets = Secrets.fromStream(CryptographyFactoryTest.class.getResourceAsStream(ENCRYPTED_FILE_KEY));

        CryptographyStreamResult<ByteArrayOutputStream> streamResult = cryptography
                .decrypt(secrets, CryptographyFactoryTest.class.getResourceAsStream(ENCRYPTED_FILE), new ByteArrayOutputStream());

        assertEquals(FILE_DATA,streamResult.getStream().toString());
    }

    @Test
    public void testStreamEncryptionDecryption_Pass() {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStreamResult<ByteArrayOutputStream> streamResult = cryptography
                .encrypt(PASSWORD,new ByteArrayInputStream(STRING_LITERAL.getBytes()),new ByteArrayOutputStream());

        ByteArrayInputStream cipherStream = new ByteArrayInputStream(streamResult.getStream().toByteArray());
        CryptographyStreamResult<ByteArrayOutputStream> decodedResult = cryptography
                .decrypt(streamResult.getSecrets(),cipherStream,new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL,decodedResult.getStream().toString());
    }

    @Test
    public void testFileEncryptionDecryption_Pass() throws FileNotFoundException {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        CryptographyStreamResult<FileOutputStream> streamResult = cryptography
                .encrypt(PASSWORD,new ByteArrayInputStream(STRING_LITERAL.getBytes()),
                        new FileOutputStream(ENCRYPTED_FILE_TEST));

        CryptographyStreamResult<ByteArrayOutputStream> streamResult2 = cryptography
                .decrypt(streamResult.getSecrets(), new FileInputStream(ENCRYPTED_FILE_TEST),
                        new ByteArrayOutputStream());

        assertEquals(STRING_LITERAL, streamResult2.getStream().toString());
    }

    @Disabled
    public void testEncryptionFileCreation_Pass() throws IOException  {
        SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
        String s = "This is a test file with encrypted data -- TOP SECRET!";
        CryptographyStreamResult<FileOutputStream> streamResult = cryptography.encrypt(PASSWORD,new ByteArrayInputStream(s.getBytes()),
                new FileOutputStream("aes-encrypted-file.enc"));
        streamResult.getSecrets().exportToFile(new File("aes-encrypted-file.key"));
    }
}
