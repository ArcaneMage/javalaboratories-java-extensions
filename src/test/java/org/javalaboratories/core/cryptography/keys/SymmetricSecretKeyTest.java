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
package org.javalaboratories.core.cryptography.keys;

import org.javalaboratories.core.cryptography.CryptographyException;
import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey.SaltMode;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SymmetricSecretKeyTest {

    private static final String ENCRYPTED_KEY = "YM+BfBZ4my4MfvTKWo8LwLcyEL+cn2AjxNCNspc30nw=";
    private static final String INVALID_FILE = "aes-file-does-not-exist.tmp";
    private static final String PASSWORD = "F0xedFence75";
    private static final String PASSWORD2 = "CatTinR00f";

    private static final String ENCRYPTED_FILE_KEY = "aes-encrypted-file.key";

    @Test
    public void testFromPassword_Pass() {
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);
        SymmetricSecretKey key2 = SymmetricSecretKey.from(PASSWORD2);

        assertEquals(SymmetricSecretKey.from(PASSWORD), key);
        assertNotEquals(key,key2);
    }

    @Test
    public void testFromPassword_AutoSalt_Pass() {
        SymmetricSecretKey keySalted = SymmetricSecretKey.from(PASSWORD, SaltMode.AUTO_GENERATE);
        SymmetricSecretKey keyUnsalted = SymmetricSecretKey.from(PASSWORD);

        assertNotEquals(SymmetricSecretKey.from(PASSWORD),keySalted);
        assertEquals(SymmetricSecretKey.from(PASSWORD), keyUnsalted);
    }

    @Test
    public void testFromStream_Pass() {
        SymmetricSecretKey key = SymmetricSecretKey.from(new ByteArrayInputStream(ENCRYPTED_KEY.getBytes()));

        assertEquals(SymmetricSecretKey.from(PASSWORD),key);
    }

    @Test
    public void testFromStream_Fail() {
        assertThrows(CryptographyException.class, () -> SymmetricSecretKey.from(new ByteArrayInputStream("".getBytes())));
    }

    @Test
    public void testFromFile_Pass() throws URISyntaxException {
        ClassLoader classLoader = SymmetricSecretKeyTest.class.getClassLoader();
        File file = Paths.get(classLoader.getResource(ENCRYPTED_FILE_KEY).toURI()).toFile();

        SymmetricSecretKey key = SymmetricSecretKey.from(file);

        assertEquals(SymmetricSecretKey.from(PASSWORD),key);
    }

    @Test
    public void testFromFile_Fail() {
        assertThrows(CryptographyException.class, () -> SymmetricSecretKey.from(new File(INVALID_FILE)));
    }

    @Test
    public void testWriteStream_Pass() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);

        key.write(os);

        assertEquals(ENCRYPTED_KEY,os.toString());
    }

    @Test
    public void testWriteFile_Pass() throws IOException {
        SymmetricSecretKey key = SymmetricSecretKey.from(PASSWORD);
        File file = new File("aes-encrypted-key-file.tmp");

        key.write(file);

        assertTrue(file.exists());
        String content = Files.lines(file.toPath())
                .collect(Collectors.joining());

        assertEquals(44,content.length());
        assertTrue(file.delete());
    }
}
