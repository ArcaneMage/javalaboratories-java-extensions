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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class AtomicDoubleTest {

    @Test
    public void testConstructor_Pass() {
        AtomicDouble a = new AtomicDouble();
        AtomicDouble b = new AtomicDouble(5);

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertEquals(0.0,a.doubleValue());
        Assertions.assertEquals(5.0,b.doubleValue());
    }

    @Test
    public void testAccumulateAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        a.accumulateAndGet(5, (left,right) -> left + right + 5);

        Assertions.assertEquals(15.0,a.doubleValue());
    }

    @Test
    public void testAddAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        double result = a.addAndGet(20);

        Assertions.assertEquals(25.0,result);
    }

    @Test
    public void testDecrementAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.decrementAndGet();

        Assertions.assertEquals(4.1,result);
    }

    @Test
    public void testGetAndAdd_Pass() {
        AtomicDouble a = new AtomicDouble(5);

        double result = a.getAndAdd(20);

        Assertions.assertEquals(5.0,result);
        Assertions.assertEquals(25.0, a.doubleValue());
    }

    @Test
    public void testGetAndDecrement_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndDecrement();

        Assertions.assertEquals(5.1,result);
        Assertions.assertEquals(4.1, a.doubleValue());
    }

    @Test
    public void testGetAndIncrement_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndIncrement();

        Assertions.assertEquals(5.1,result);
        Assertions.assertEquals(6.1, a.doubleValue());
    }

    @Test
    public void testGetAndSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndSet(10.0);

        Assertions.assertEquals(5.1,result);
        Assertions.assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testGetAndUpdate_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.getAndUpdate(n -> n + 5);

        Assertions.assertEquals(5.1,result);
        Assertions.assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testIncrementAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.incrementAndGet();

        Assertions.assertEquals(6.1,result);
        Assertions.assertEquals(6.1, a.doubleValue());
    }

    @Test
    public void tesLazySet_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        a.lazySet(10);

        Assertions.assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        a.set(10);

        Assertions.assertEquals(10.0, a.doubleValue());
    }

    @Test
    public void testToString_Pass() {
        AtomicDouble a = new AtomicDouble(5.0);

        Assertions.assertEquals("5.0", a.toString());
    }

    @Test
    public void testUpdateAndGet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        double result = a.updateAndGet(n -> n + 5);

        Assertions.assertEquals(10.1,result);
        Assertions.assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testWeakCompareAndSet_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        boolean result = a.weakCompareAndSet(5.1,10.1);

        Assertions.assertTrue(result);
        Assertions.assertEquals(10.1, a.doubleValue());
    }

    @Test
    public void testNumberInterface_Pass() {
        AtomicDouble a = new AtomicDouble(5.1);

        Assertions.assertEquals(5.1,a.doubleValue());
        Assertions.assertEquals(5.1f,a.floatValue());
        Assertions.assertEquals(5,a.intValue());
        Assertions.assertEquals(5L,a.longValue());
    }

    @Test
    public void testConcurrency_Pass() {
        AtomicDouble result = IntStream
                .range(0,4096)
                .parallel()
                .filter(n -> n % 2 == 0)
                .collect(AtomicDouble::new,(a,n) -> a.accumulateAndGet(n, Double::sum),(a,b) -> a.addAndGet(b.get()));
        Assertions.assertEquals(4192256.0,result.get());
    }
}
