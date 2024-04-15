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
package org.javalaboratories.core.concurrency.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States.FLOODED;
import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentResourceFloodStabilityTest extends AbstractResourceFloodStabilityTest {

    public static class SingleThreadedFloodgate<T> extends AbstractConcurrentResourceFloodStability<T> {

        private final Supplier<T> resource;

        public <U> SingleThreadedFloodgate(Class<U> clazz, int iterations, Supplier<T> resource) {
            super(clazz,1,iterations);
            this.resource = resource;
        }

        @Override
        protected Supplier<T> getResource() {
            return resource;
        }
    }

    @BeforeEach
    public void setup() {
        unsafe = new UnsafeStatistics();
        safe = new SafeStatistics();
    }

    @Test
    public void testNew_Floodgate_Pass() {
        // Given
        SingleThreadedFloodgate<Integer> floodgate = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));

        // Then
        assertEquals(1,floodgate.getThreads());
        assertEquals(5,floodgate.getIterations());
    }

    @Test
    public void testEqual_Floodgate_Pass() {
        // Given
        SingleThreadedFloodgate<Integer> floodgate1 = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));
        SingleThreadedFloodgate<Integer> floodgate2 = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));

        // Then
        assertNotEquals(floodgate1, floodgate2);
    }

    @Test
    public void testGetService_Floodgate_Pass() {
        // Given
        SingleThreadedFloodgate<Integer> floodgate = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));

        // Then
        assertNull(floodgate.getService());
        try {
            floodgate.open();
            assertNotNull(floodgate.getService());
        } finally {
            floodgate.close();
        }
    }

    @Test
    public void testFlood_Floodgate_Pass() {
        // Given
        SingleThreadedFloodgate<Integer> floodgate = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));

        // When
        floodgate.open();
        List<Integer> result = floodgate.flood(50, TimeUnit.MILLISECONDS);

        // Then
        assertEquals(1,floodgate.getThreads());
        assertEquals(5,floodgate.getIterations());
        assertEquals(FLOODED,floodgate.getState());
        assertEquals(1,result.size());
        assertEquals(50,unsafe.getTotal());
    }

    @Test
    public void testToString_Floodgate_Pass() {
        // Given
        SingleThreadedFloodgate<Integer> floodgate = new SingleThreadedFloodgate<>(UnsafeStatistics.class,5,() -> unsafe.add(10));

        // Then
        assertTrue(floodgate.toString().contains("state=CLOSED,flood-workers=1,flood-iterations=5"));
    }
}
