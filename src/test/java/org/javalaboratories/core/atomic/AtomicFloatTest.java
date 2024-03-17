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
package org.javalaboratories.core.atomic;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AtomicFloatTest {

    @Test
    public void testConstructor_Pass() {
        AtomicFloat a = new AtomicFloat();
        AtomicFloat b = new AtomicFloat(5);

        assertNotNull(a);
        assertNotNull(b);
        assertEquals(0.0f,a.floatValue());
        assertEquals(5.0f,b.floatValue());
    }

    @Test
    public void testAccumulateAndGet_Pass() {
        AtomicFloat a = new AtomicFloat(5);

        a.accumulateAndGet(5, (left,right) -> left + right + 5);

        assertEquals(15.0f,a.floatValue());
    }

    @Test
    public void testAddAndGet_Pass() {
        AtomicFloat a = new AtomicFloat(5);

        float result = a.addAndGet(20);

        assertEquals(25.0f,result);
    }

    @Test
    public void testDecrementAndGet_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.decrementAndGet();

        assertEquals(4.1f,result);
    }

    @Test
    public void testGetAndAdd_Pass() {
        AtomicFloat a = new AtomicFloat(5);

        float result = a.getAndAdd(20);

        assertEquals(5.0f,result);
        assertEquals(25.0f, a.floatValue());
    }

    @Test
    public void testGetAndDecrement_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.getAndDecrement();

        assertEquals(5.1f,result);
        assertEquals(4.1f,a.floatValue());
    }

    @Test
    public void testGetAndIncrement_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.getAndIncrement();

        assertEquals(5.1f,result);
        assertEquals(6.1f,a.floatValue());
    }

    @Test
    public void testGetAndSet_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.getAndSet(10.0f);

        assertEquals(5.1f,result);
        assertEquals(10.0f,a.floatValue());
    }

    @Test
    public void testGetAndUpdate_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.getAndUpdate(n -> n + 5);

        assertEquals(5.1f,result);
        assertEquals(10.1f,a.floatValue());
    }

    @Test
    public void testIncrementAndGet_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.incrementAndGet();

        assertEquals(6.1f,result);
        assertEquals(6.1f,a.floatValue());
    }

    @Test
    public void tesLazySet_Pass() {
        AtomicFloat a = new AtomicFloat(5.0f);

        a.lazySet(10);

        assertEquals(10.0f,a.floatValue());
    }

    @Test
    public void testSet_Pass() {
        AtomicFloat a = new AtomicFloat(5.0f);

        a.set(10);

        assertEquals(10.0f,a.floatValue());
    }

    @Test
    public void testToString_Pass() {
        AtomicFloat a = new AtomicFloat(5.0f);

        assertEquals("5.0",a.toString());
    }

    @Test
    public void testUpdateAndGet_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        float result = a.updateAndGet(n -> n + 5);

        assertEquals(10.1f,result);
        assertEquals(10.1f,a.floatValue());
    }

    @Test
    public void testWeakCompareAndSet_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        boolean result = a.weakCompareAndSet(5.1f,10.1f);

        assertTrue(result);
        assertEquals(10.1f,a.floatValue());
    }

    @Test
    public void testNumberInterface_Pass() {
        AtomicFloat a = new AtomicFloat(5.1f);

        assertEquals(5.1,Math.round(a.doubleValue() * 10)/10.0);
        assertEquals(5.1f,a.floatValue());
        assertEquals(5,a.intValue());
        assertEquals(5L,a.longValue());
    }

    @Test
    public void testConcurrency_Pass() {
        AtomicFloat result = IntStream
                .range(0,4096)
                .parallel()
                .filter(n -> n % 2 == 0)
                .collect(AtomicFloat::new,(a,n) -> a.accumulateAndGet(n, Float::sum),(a,b) -> a.addAndGet(b.get()));
        assertEquals(4192256.0,result.get());
    }
}
