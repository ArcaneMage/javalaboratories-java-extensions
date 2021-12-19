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

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetXTest {
    private final Set<Integer> set1 = SetX.of(10,20,30);
    private final Set<Integer> set2 = SetX.of();
    private final Set<Integer> set3 = SetX.of(40);

    @Test
    public void testConstruction_Pass() {
        assertEquals(3, set1.size());
        assertEquals(0, set2.size());
        assertEquals(1, set3.size());

        Integer[] array = set1.toArray(new Integer[3]);
        Arrays.sort(array);
        assertEquals(10, array[0]);
        assertEquals(20, array[1]);
        assertEquals(30, array[2]);
    }

    @Test
    public void testImmutability_Pass() {
        assertThrows(UnsupportedOperationException.class,() -> set1.add(50));
    }
}
