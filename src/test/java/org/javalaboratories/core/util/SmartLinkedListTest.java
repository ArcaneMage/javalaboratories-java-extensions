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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        boolean result = list2.remove(99);
        assertEquals("[4,5,6,7,8]",list2.toString());
        assertFalse(result);

        result = list2.remove(4);
        assertEquals("[5,6,7,8]",list2.toString());
        assertTrue(result);

        result = list2.remove(7);
        assertEquals("[5,6,8]",list2.toString());
        assertTrue(result);

        result = list2.remove(8);
        assertEquals("[5,6]",list2.toString());
        assertTrue(result);

        result = list2.remove(6);
        assertEquals("[5]",list2.toString());
        assertTrue(result);

        result = list2.remove(5);
        assertEquals("[]",list2.toString());
        assertTrue(result);

        result = list2.remove(99);
        assertEquals("[]",list2.toString());
        assertFalse(result);

        result = list2.remove(99);
        assertEquals("[]",list2.toString());
        assertFalse(result);
    }
}
