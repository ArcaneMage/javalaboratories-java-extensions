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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XMapTest {

    private final Map<Integer,String> map0 = XMap.of();
    private final Map<Integer,String> map1 = XMap.of(1,"One");
    private final Map<Integer,String> map2 = XMap.of(1,"One",2,"Two");
    private final Map<Integer,String> map3 = XMap.of(1,"One",2,"Two",3,"Three");
    private final Map<Integer,String> map4 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four");
    private final Map<Integer,String> map5 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five");
    private final Map<Integer,String> map6 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six");
    private final Map<Integer,String> map7 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven");
    private final Map<Integer,String> map8 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven",8,"Eight");
    private final Map<Integer,String> map9 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven",8,"Eight",9,"Nine");
    private final Map<Integer,String> map10 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven",8,"Eight",9,"Nine",10,"Ten");
    private final Map<Integer,String> map11 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven",8,"Eight",9,"Nine",10,"Ten",11,"Eleven");
    private final Map<Integer,String> map12 = XMap.of(1,"One",2,"Two",3,"Three",4,"Four",5,"Five",6,"Six",7,"Seven",8,"Eight",9,"Nine",10,"Ten",11,"Eleven",12,"Twelve");

    @Test
    public void testConstruction_Pass() {
        assertEquals(0,map0.size());
        assertEquals(1,map1.size());
        assertEquals(2,map2.size());
        assertEquals(3,map3.size());
        assertEquals(4,map4.size());
        assertEquals(5,map5.size());
        assertEquals(6,map6.size());
        assertEquals(7,map7.size());
        assertEquals(8,map8.size());
        assertEquals(9,map9.size());
        assertEquals(10,map10.size());
        assertEquals(11,map11.size());
        assertEquals(12,map12.size());
    }

    @Test
    public void testConstruction_Entries_Pass() {
        Map<Integer,String> m0 = XMap.ofEntries(
                XMap.entry(1, "One"),
                XMap.entry(2, "Two")
        );
        assertEquals(2,m0.size());
        assertThrows(UnsupportedOperationException.class, () -> m0.put(999,"Nine hundred and ninety-nine"));
    }

    @Test
    public void testImmutability_Pass() {
        assertThrows(UnsupportedOperationException.class, () -> map0.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map1.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map2.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map3.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map4.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map5.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map6.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map7.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map8.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map9.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map10.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map11.put(999,"Nine hundred and ninety-nine"));
        assertThrows(UnsupportedOperationException.class, () -> map12.put(999,"Nine hundred and ninety-nine"));
    }

    @Test
    public void testCopyOf_Pass() {
        Map<Integer,String> m0 = null;
        Map<Integer,String> m1 = XMap.of(1,"one",2,null);
        Map<Integer,String> m2 = XMap.of(null,"one");
        Map<Integer,String> copy = XMap.copyOf(map1);

        assertEquals(map1,copy);
        assertThrows(NullPointerException.class,() -> XMap.copyOf(m0));
        assertThrows(NullPointerException.class,() -> XMap.copyOf(m1));
        assertThrows(NullPointerException.class,() -> XMap.copyOf(m2));
        assertThrows(UnsupportedOperationException.class, () -> copy.put(999,"Nine hundred and ninety-nine"));
    }
}
