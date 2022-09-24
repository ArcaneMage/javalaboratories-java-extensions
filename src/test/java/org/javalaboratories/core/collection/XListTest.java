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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XListTest {

    private final List<Integer> list1 = XList.of(10,20,30);
    private final List<Integer> list2 = XList.of();
    private final List<Integer> list3 = XList.of(40);

    @Test
    public void testConstruction_Pass() {
        assertEquals(3, list1.size());
        assertEquals(0, list2.size());
        assertEquals(1, list3.size());

        assertEquals(10, list1.get(0));
        assertEquals(20, list1.get(1));
        assertEquals(30, list1.get(2));
    }

    @Test
    public void testImmutability_Pass() {
        assertThrows(UnsupportedOperationException.class,() -> list1.add(50));
    }

    @Test
    public void testCopyOf_Pass() {
        List<Integer> l0 = null;
        List<Integer> copy = XList.copyOf(list1);

        assertEquals(list1,copy);
        assertThrows(NullPointerException.class,() -> XList.copyOf(l0));
        assertThrows(UnsupportedOperationException.class,() -> copy.add(999));
    }
}
