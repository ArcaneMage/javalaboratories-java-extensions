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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void testToByteArray() {
        byte[] bytes = Bytes.toByteArray(0xAABBCCDD);
        byte[] bytes2 = Bytes.toByteArray(0xFFFFFFFF);
        byte[] bytes3 = Bytes.toByteArray(0x00000002);

        assertTrue(Arrays.equals(new byte[]{(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD},bytes));
        assertTrue(Arrays.equals(new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF},bytes2));
        assertTrue(Arrays.equals(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02},bytes3));
    }

    @Test
    public void testFromBytes() {
        byte[] bytes = new byte[]{(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD};
        byte[] bytes2 = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        byte[] bytes3 = new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02};

        int value = Bytes.fromBytes(bytes);
        int value2 = Bytes.fromBytes(bytes2);
        int value3 = Bytes.fromBytes(bytes3);

        assertEquals(0xAABBCCDD,value);
        assertEquals(0xFFFFFFFF,value2);
        assertEquals(0x00000002,value3);
    }
}
