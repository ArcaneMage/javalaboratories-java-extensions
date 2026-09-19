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
package org.javalaboratories.core.util;

import org.javalaboratories.core.util.resources.ByteResourceFile;
import org.javalaboratories.core.util.resources.StringResourceFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceFileTest {

    private static final String RESOURCE_FILE = "resource-file-test.txt";

    private static final String RESOURCE_FILE_CONTENTS = "This is a test resource file " +
            "that contains nothing particularly interesting, but it is ideal " +
            "for the ResourceFile test classes.";

    private static final byte[] RESOURCE_FILE_BYTE_CONTENTS = RESOURCE_FILE_CONTENTS.getBytes(StandardCharsets.UTF_8);

    @Test
    public void testStringResourceFile_Pass() throws IOException {
        String contents = StringResourceFile.read(RESOURCE_FILE);
        assertEquals(RESOURCE_FILE_CONTENTS,contents);
    }

    @Test
    public void testStringResourceFileWithClass_Pass() throws IOException {
        String contents = new StringResourceFile(ResourceFileTest.class,RESOURCE_FILE).read();
        assertEquals(RESOURCE_FILE_CONTENTS,contents);
    }

    @Test
    public void testStringResourceFileWithClassAndRoot_Pass() throws IOException {
        String contents = new StringResourceFile(ResourceFileTest.class, "/"+RESOURCE_FILE, true).read();
        assertEquals(RESOURCE_FILE_CONTENTS,contents);
    }

    @Test
    public void testByteResourceFile_Pass() throws IOException {
        byte[] contents = ByteResourceFile.read(RESOURCE_FILE);
        assertArrayEquals(RESOURCE_FILE_BYTE_CONTENTS, contents);
    }

    @Test
    public void testByteResourceFileWithClass_Pass() throws IOException {
        byte[] contents = new ByteResourceFile(ResourceFileTest.class, RESOURCE_FILE).read();
        assertArrayEquals(RESOURCE_FILE_BYTE_CONTENTS, contents);
    }

    @Test
    public void testByteResourceFileWithClassAndRoot_Pass() throws IOException {
        byte[] contents = new ByteResourceFile(ResourceFileTest.class, "/"+RESOURCE_FILE, true).read();
        assertArrayEquals(RESOURCE_FILE_BYTE_CONTENTS,contents);
    }

}
