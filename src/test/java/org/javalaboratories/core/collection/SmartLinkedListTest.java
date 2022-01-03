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
package org.javalaboratories.core.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class SmartLinkedListTest {
    private SmartLinkedList<Integer> list1;
    private SmartLinkedList<Integer> list2;
    private SmartLinkedList<Integer> list3;

    @BeforeEach
    public void setup() {
        list1 = new SmartLinkedList<>(4,5,6);
        list2 = new SmartLinkedList<>(4,5,6,7,8);
        list3 = new SmartLinkedList<>();
    }

    @Test
    public void testAddFirst_Pass() {
        SmartLinkedList<Integer> llist1 = new SmartLinkedList<>();
        llist1.addFirst(3);

        list1.addFirst(3);

        assertEquals(3,list1.get(0));
        assertEquals(3,llist1.get(0));
        assertEquals(4,list1.depth());
    }

    @Test
    public void testClear_Pass() {
        list2.clear();

        assertTrue(list2.isEmpty());
        assertEquals(0,list2.depth());
    }

    @Test
    public void testGet_Pass() {
        int value1 = list1.get(0); // 4
        int value2 = list1.get(1); // 5
        int value3 = list1.get(2); // 6

        int value4 = list2.get(0); // 4
        int value5 = list2.get(1); // 5
        int value6 = list2.get(2); // 6
        int value7 = list2.get(3); // 7

        assertEquals(4, value1);
        assertEquals(5, value2);
        assertEquals(6, value3);

        assertEquals(4, value4);
        assertEquals(5, value5);
        assertEquals(6, value6);
        assertEquals(7, value7);
    }

    @Test
    public void testClone_Pass() {
        @SuppressWarnings("unchecked")
        SmartLinkedList<Integer> copy = (SmartLinkedList<Integer>) list1.clone();

        assertNotSame(copy, list1);
        assertEquals(3, copy.depth());
        assertEquals(4,copy.get(0));
        assertEquals(5,copy.get(1));
        assertEquals(6,copy.get(2));
    }

    @Test
    public void testEquals_Pass() {
        @SuppressWarnings("unchecked")
        SmartLinkedList<Integer> copy = (SmartLinkedList<Integer>) list1.clone();

        assertEquals(copy,list1);
    }

    @Test
    public void testHashCode_Pass() {
        @SuppressWarnings("unchecked")
        SmartLinkedList<Integer> copy = (SmartLinkedList<Integer>) list1.clone();

        assertEquals(copy,list1);
        assertEquals(copy.hashCode(),list1.hashCode());
    }

    @Test
    public void testIndexOf_Pass() {

        Iterator<Integer> iter = list1.iterator();

        assertEquals(1,list1.indexOf(5));
        assertEquals(2,list1.indexOf(6));
        assertEquals(-1,list1.indexOf(99));
    }

    @Test
    public void testIsEmpty_Pass() {
        assertFalse(list1.isEmpty());
        assertTrue(list3.isEmpty());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("[4,5,6]",list1.toString());
        assertEquals("[4,5,6,7,8]",list2.toString());
    }


    @Test
    public void testToArray_Pass() {
        Integer[] integers = list1.toArray();

        assertEquals(3,integers.length);
        assertEquals(4, integers[0]);
        assertEquals(5, integers[1]);
        assertEquals(6, integers[2]);
    }

    @Test
    public void testToList_Pass() {
        List<Integer> list = list1.toList();

        assertEquals(3,list.size());
        assertEquals(4,list.get(0));
        assertEquals(5,list.get(1));
        assertEquals(6,list.get(2));
    }

    @Test
    public void testToMap_Pass() {
        Map<String,Integer> map = list1.toMap(index -> Integer.toString(index));

        assertEquals(3, map.size());
        assertEquals(4, map.get("0"));
        assertEquals(5, map.get("1"));
        assertEquals(6, map.get("2"));
    }

    @Test
    public void testRemove_Pass() {
        boolean result = list2.remove((Integer)99);
        assertEquals("[4,5,6,7,8]",list2.toString());
        assertFalse(result);

        result = list2.remove((Integer)4);
        assertEquals("[5,6,7,8]",list2.toString());
        assertTrue(result);

        result = list2.remove((Integer)7);
        assertEquals("[5,6,8]",list2.toString());
        assertTrue(result);

        result = list2.remove((Integer)8);
        assertEquals("[5,6]",list2.toString());
        assertTrue(result);

        result = list2.remove((Integer)6);
        assertEquals("[5]",list2.toString());
        assertTrue(result);

        result = list2.remove((Integer)5);
        assertEquals("[]",list2.toString());
        assertTrue(result);

        result = list2.remove((Integer)99);
        assertEquals("[]",list2.toString());
        assertFalse(result);

        result = list2.remove((Integer)99);
        assertEquals("[]",list2.toString());
        assertFalse(result);
    }

    @Test
    public void testRemoveIndex_Pass() {
        assertThrows(IndexOutOfBoundsException.class, () -> list2.remove(99));
        assertEquals("[4,5,6,7,8]",list2.toString());

        int element = list2.remove(4);
        assertEquals(8, element);
        assertEquals("[4,5,6,7]",list2.toString());

        element = list2.remove(0);
        assertEquals(4, element);
        assertEquals("[5,6,7]",list2.toString());

        element = list2.remove(1);
        assertEquals(6, element);
        assertEquals("[5,7]",list2.toString());

        element = list2.remove(1);
        assertEquals(7, element);
        assertEquals("[5]",list2.toString());

        element = list2.remove(0);
        assertEquals(5, element);
        assertEquals("[]",list2.toString());
    }

    @Test
    public void testRemoveFirst_Pass() {
        int element = list1.removeFirst();
        assertEquals(4, element);
        assertEquals("[5,6]",list1.toString());

        element = list1.removeFirst();
        assertEquals(5, element);
        assertEquals("[6]",list1.toString());

        element = list1.removeFirst();
        assertEquals(6, element);
        assertEquals("[]",list1.toString());

        assertThrows(NoSuchElementException.class,() -> list1.removeFirst());
    }

    @Test
    public void testRemoveLast_Pass() {
        int element = list1.removeLast();
        assertEquals(6, element);
        assertEquals("[4,5]",list1.toString());

        element = list1.removeLast();
        assertEquals(5, element);
        assertEquals("[4]",list1.toString());

        element = list1.removeLast();
        assertEquals(4, element);
        assertEquals("[]",list1.toString());

        assertThrows(NoSuchElementException.class,() -> list1.removeFirst());
    }

    @Test
    public void testSerialization_Pass() throws IOException, ClassNotFoundException  {
        // Serialize
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(ostream);

        out.writeObject(list1);
        out.close();
        ostream.close();

        // Deserialization
        byte[] bytes = ostream.toByteArray();

        ByteArrayInputStream istream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(istream);
        SmartLinkedList<Integer> list = (SmartLinkedList<Integer>) in.readObject();

        assertEquals(3,list.depth());
        assertEquals("[4,5,6]",list.toString());

        in.close();
        istream.close();
    }
}
