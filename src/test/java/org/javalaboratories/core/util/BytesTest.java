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

import static org.junit.jupiter.api.Assertions.*;

public class BytesTest {

    private static final byte[] SOURCE_BYTES = {1,2,3,4,5,6,7,9,10,127};

    @Test
    public void testStaticConcat() {
        byte[] first = {10,20,30};
        byte[] second = {40,50,60,70};

        byte[] result = Bytes.concat(first,second);

        assertArrayEquals(new byte[]{10, 20, 30, 40, 50, 60, 70}, result);
    }

    @Test
    public void testStaticConcat_IndexOutOfBoundsException_Fail() {
        byte[] first = {10,20,30};
        byte[] second = {40,50,60,70};

        assertThrows(IndexOutOfBoundsException.class, () -> Bytes.concat(first,second,5));
    }

    @Test
    public void testStaticCopy() {
        byte[] result = Bytes.copy(SOURCE_BYTES);

        assertArrayEquals(SOURCE_BYTES, result);
    }

    @Test
    public void testStaticCopy_IndexOutOfBoundsException_Fail() {
        assertThrows(IndexOutOfBoundsException.class, () -> Bytes.copy(SOURCE_BYTES,-1));
    }

    @Test
    public void testStaticTrimLeft() {
        byte[] result = Bytes.trimLeft(SOURCE_BYTES,3);

        assertArrayEquals(new byte[]{4, 5, 6, 7, 9, 10, 127}, result);
    }

    @Test
    public void testStaticTrimRight() {
        byte[] result = Bytes.trimRight(SOURCE_BYTES,3);

        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7}, result);
    }

    @Test
    public void testStaticMoveBlock() {
        byte[] result = Bytes.copy(SOURCE_BYTES);
        byte[] moved = Bytes.moveBlock(result,2,5,7);
        byte[] restored = Bytes.moveBlock(moved,7,10,2);

        assertArrayEquals(new byte[]{1, 2, 6, 7, 9, 10, 127, 3, 4, 5}, moved);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 9, 10, 127}, restored);
        assertArrayEquals(result, restored);
    }

    @Test
    public void testStaticCopyBlock() {
        byte[] result = Bytes.copy(SOURCE_BYTES);
        byte[] copied = Bytes.copyBlock(result,2,5,5);

        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 3, 4, 5, 10, 127}, copied);
    }

    @Test
    public void testStaticRemoveBlock() {
        byte[] result = Bytes.copy(SOURCE_BYTES);
        byte[] deleted = Bytes.removeBlock(result,2,5);

        assertArrayEquals(new byte[]{1, 2, 6, 7, 9, 10, 127}, deleted);
    }

    @Test
    public void testStaticToBytes() {
        byte[] bytes = Bytes.toBytes(0xAABBCCDD);
        byte[] bytes2 = Bytes.toBytes(0xFFFFFFFF);
        byte[] bytes3 = Bytes.toBytes(0x00000002);

        assertArrayEquals(new byte[]{(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD}, bytes);
        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, bytes2);
        assertArrayEquals(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02}, bytes3);
    }

    @Test
    public void testStaticValueOf() {
        byte[] bytes = new byte[]{(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD};
        byte[] bytes2 = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        byte[] bytes3 = new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02};

        int value = Bytes.valueOf(bytes);
        int value2 = Bytes.valueOf(bytes2);
        int value3 = Bytes.valueOf(bytes3);

        assertEquals(0xAABBCCDD,value);
        assertEquals(0xFFFFFFFF,value2);
        assertEquals(0x00000002,value3);
    }

    @Test
    public void testStaticSubBytes() {
        byte[] bytes = new byte[]{0,1,2,3,4,5,6,7,8};
        byte[] sub1 = Bytes.subBytes(bytes,1,3);
        byte[] sub2 = Bytes.subBytes(bytes,5,9);

        assertArrayEquals(new byte[]{1, 2}, sub1);
        assertArrayEquals(new byte[]{5, 6, 7, 8}, sub2);
    }

    @Test
    public void testBytesObjectConstructor() {
        Bytes bytes = new Bytes(SOURCE_BYTES);

        assertEquals("[1,2,3,4,5,6,7,9,10,127]",bytes.toString());
        assertEquals(10,bytes.length());
    }

    @Test
    public void testBytesObjectConstructorWithVarArgs() {
        Bytes bytes = new Bytes((byte)1,(byte)2,(byte)3,(byte)4,(byte)5,(byte)6,(byte)7,(byte)9,(byte)10,(byte)127);

        assertEquals("[1,2,3,4,5,6,7,9,10,127]",bytes.toString());
        assertEquals(10,bytes.length());
    }

    @Test
    public void testBytesObjectCopyConstructor() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes copied = new Bytes(bytes);

        assertEquals("[1,2,3,4,5,6,7,9,10,127]",bytes.toString());
        assertEquals("[1,2,3,4,5,6,7,9,10,127]",copied.toString());
        assertEquals(10,bytes.length());

        assertEquals(bytes,copied);
    }

    @Test
    public void testBytesObjectAdd() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        bytes.add((byte) 128);
        bytes.add((byte) 129);

        assertEquals("[1,2,3,4,5,6,7,9,10,127,128,129]",bytes.toString(true));
        assertEquals(12,bytes.length());
    }

    @Test
    public void testBytesObjectAddVarArgs() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        bytes.add((byte) 128, (byte) 129);

        assertEquals("[1,2,3,4,5,6,7,9,10,127,128,129]",bytes.toString(true));
        assertEquals(12,bytes.length());
    }

    @Test
    public void testBytesObjectAt() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        bytes.add((byte) 128);
        bytes.add((byte) 129);
        byte value = bytes.at(11);
        int uvalue = bytes.at(11, true);
        int svalue = bytes.at(11, false);

        assertEquals("[1,2,3,4,5,6,7,9,10,127,128,129]",bytes.toString(true));
        assertEquals(12,bytes.length());

        assertEquals(-127,value);
        assertEquals(129,uvalue);
        assertEquals(-127,svalue);
    }

    @Test
    public void testBytesObjectAt_IndexOutOfBoundException_Fail() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        bytes.add((byte)128);
        bytes.add((byte)129);

        assertThrows(IndexOutOfBoundsException.class,() -> bytes.at(-1));
        assertThrows(IndexOutOfBoundsException.class,() -> bytes.at(12));
    }

    @Test
    public void testBytesObjectConcat() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes ext = new Bytes(new byte[]{(byte)128, (byte)129});

        Bytes result = bytes.concat(ext);

        byte value = result.at(11);
        int uvalue = result.at(11, true);
        int svalue = result.at(11, false);

        assertEquals("[1,2,3,4,5,6,7,9,10,127,128,129]",result.toString(true));
        assertEquals(12,result.length());

        assertEquals(-127,value);
        assertEquals(129,uvalue);
        assertEquals(-127,svalue);
    }

    @Test
    public void testBytesObjectCopy() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes copied = bytes.copy();

        byte value = copied.at(9);
        int uvalue = copied.at(9, true);
        int svalue = copied.at(9, false);

        assertEquals("[1,2,3,4,5,6,7,9,10,127]",copied.toString());
        assertEquals(10,copied.length());

        assertEquals(127,value);
        assertEquals(127,uvalue);
        assertEquals(127,svalue);

        assertEquals(bytes, copied);
    }

    @Test
    public void testBytesObjectLeftTrim() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes trimmed = bytes.trimLeft(3);

        byte value = trimmed.at(6);
        int uvalue = trimmed.at(6, true);
        int svalue = trimmed.at(6, false);

        assertEquals("[4,5,6,7,9,10,127]",trimmed.toString());
        assertEquals(7,trimmed.length());

        assertEquals(127,value);
        assertEquals(127,uvalue);
        assertEquals(127,svalue);
    }

    @Test
    public void testBytesObjectLeftTrim_IndexOutOfBoundException_Fail() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        assertThrows(IndexOutOfBoundsException.class, () -> bytes.trimLeft(11));
    }


    @Test
    public void testBytesObjectRightTrim() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes trimmed = bytes.trimRight(3);

        byte value = trimmed.at(6);
        int uvalue = trimmed.at(6, true);
        int svalue = trimmed.at(6, false);

        assertEquals("[1,2,3,4,5,6,7]",trimmed.toString());
        assertEquals(7,trimmed.length());

        assertEquals(7,value);
        assertEquals(7,uvalue);
        assertEquals(7,svalue);
    }

    @Test
    public void testBytesObjectMoveBlock() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes moved = bytes.moveBlock(2,5,7);

        assertArrayEquals(new byte[]{1, 2, 6, 7, 9, 10, 127, 3, 4, 5}, moved.toArray());
    }

    @Test
    public void testBytesObjectCopyBlock() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes moved = bytes.copyBlock(2,5,5);

        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 3, 4, 5, 10, 127}, moved.toArray());
    }

    @Test
    public void testBytesObjectRemoveBlock() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        Bytes deleted = bytes.removeBlock(2,5);

        assertArrayEquals(new byte[]{1, 2, 6, 7, 9, 10, 127}, deleted.toArray());
    }

    @Test
    public void testBytesObjectRightTrim_IndexOutOfBoundException_Fail() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        assertThrows(IndexOutOfBoundsException.class, () -> bytes.trimRight(11));
    }

    @Test
    public void testBytesObjectSubBytes() {
        Bytes bytes = new Bytes(new byte[] {0,1,2,3,4,5,6,7,8});
        Bytes sub1 = bytes.subBytes(1,3);
        Bytes sub2 = bytes.subBytes(5,9);

        assertEquals(new Bytes(new byte[]{1,2}), sub1);
        assertEquals(new Bytes(new byte[]{5,6,7,8}), sub2);
    }

    @Test
    public void testBytesObjectSubBytes_IndexOutOfBoundsException_Fail() {
        Bytes bytes = new Bytes(new byte[] {0,1,2,3,4,5,6,7,8});

        assertThrows(IndexOutOfBoundsException.class, () -> bytes.subBytes(-1,9));
        assertThrows(IndexOutOfBoundsException.class, () -> bytes.subBytes(1,10));
    }

    @Test
    public void testBytesObjectValueOf() {
        Bytes number = new Bytes(new byte[]{(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD});

        assertEquals(0xAABBCCDD,number.valueOf(0));
    }

    @Test
    public void testBytesObjectValueOf_IndexOutOfBoundsException_Fail() {
        Bytes number = new Bytes(new byte[]{(byte)0xAA,(byte)0xBB,(byte)0xCC,(byte)0xDD});

        assertThrows(IndexOutOfBoundsException.class, () -> number.valueOf(1));
    }

    @Test
    public void testBytesObjectToArray() {
        Bytes bytes = new Bytes(SOURCE_BYTES);
        byte[] array = bytes.toArray();

        assertArrayEquals(new byte[]{1,2,3,4,5,6,7,9,10,127},array);
        assertEquals(10,array.length);
    }
}
