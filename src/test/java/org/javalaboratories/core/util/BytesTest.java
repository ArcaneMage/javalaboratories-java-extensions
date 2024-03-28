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
package org.javalaboratories.core.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytesTest {

    private static final byte[] SOURCE_BYTES = {1,2,3,4,5,6,7,9,10,127};

    @Test
    public void testConcat() {
        byte[] first = {10,20,30};
        byte[] second = {40,50,60,70};

        byte[] result = Bytes.concat(first,second);

        assertTrue(Arrays.equals(new byte[]{10, 20, 30, 40, 50, 60, 70}, result));
    }

    @Test
    public void testCopy() {
        byte[] result = Bytes.copy(SOURCE_BYTES);

        assertTrue(Arrays.equals(SOURCE_BYTES, result));
    }

    @Test
    public void testTrimLeft() {
        byte[] result = Bytes.trimLeft(SOURCE_BYTES,3);

        assertTrue(Arrays.equals(new byte[]{4,5,6,7,9,10,127}, result));
    }

    @Test
    public void testTrimRight() {
        byte[] result = Bytes.trimRight(SOURCE_BYTES,3);

        assertTrue(Arrays.equals(new byte[]{1,2,3,4,5,6,7}, result));
    }
}
