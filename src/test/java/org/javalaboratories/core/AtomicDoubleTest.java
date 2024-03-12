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
package org.javalaboratories.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AtomicDoubleTest {

    @Test
    public void testConstructor_Pass() {
        AtomicDouble a = new AtomicDouble();
        AtomicDouble b = new AtomicDouble(5);

        assertNotNull(a);
        assertNotNull(b);
        assertEquals(0.0,a.doubleValue());
        assertEquals(5.0,b.doubleValue());
    }

    @Test
    public void testAccumulateAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        a.accumulateAndGet(5, (left,right) -> left + right + 5);

        assertEquals(15.0,a.doubleValue());
    }

    @Test
    public void testAddAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        double result = a.addAndGet(20);

        assertEquals(25.0,result);
    }

    @Test
    public void testDecrementAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.decrementAndGet();

        assertEquals(4.1,result);
    }

    @Test
    public void testGetAndAdd_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        double result = a.getAndAdd(20);

        assertEquals(5.0,result);
        assertEquals(25.0, a.doubleValue());
    }

    @Test
    public void testGetAndDecrement_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndDecrement();

        assertEquals(5.1,result);
        assertEquals(4.1, a.doubleValue());
    }

    @Test
    public void testGetAndIncrement_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndIncrement();

        assertEquals(5.1,result);
        assertEquals(6.1, a.doubleValue());
    }

    @Test
    public void testGetAndSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndSet(10.0);

        assertEquals(5.1,result);
        assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testGetAndUpdate_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndUpdate(n -> n + 5);

        assertEquals(5.1,result);
        assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testIncrementAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.incrementAndGet();

        assertEquals(6.1,result);
        assertEquals(6.1, a.doubleValue());
    }

    @Test
    public void tesLazySet_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        a.lazySet(10);

        assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        a.set(10);

        assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testToString_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        assertEquals("5.0", a.toString());
    }

    @Test
    public void testUpdateAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.updateAndGet(n -> n + 5);

        assertEquals(10.1,result);
        assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testWeakCompareAndSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        boolean result = a.weakCompareAndSet(5.1,10.1);

        assertTrue(result);
        assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testNumberInterface_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        assertEquals(5.1,a.doubleValue());
        assertEquals(5.1f,a.floatValue());
        assertEquals(5,a.intValue());
        assertEquals(5L,a.longValue());
    }
}
