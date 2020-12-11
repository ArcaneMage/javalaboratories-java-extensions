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
package org.javalaboratories.core.concurrency.test;

import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javalaboratories.core.concurrency.test.MultithreadedFloodTester.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FloodgateTest {

    private static final Logger logger = LoggerFactory.getLogger(FloodgateTest.class);

    /**
     * Mutable object -- not thread-safe.
     */
    @Getter
    @ToString
    private static class UnsafeStatistics {
        int total;
        int requests;
        float average;

        public int add(final int value) {
            total = total + value;
            requests = requests + 1;
            average = total / (float) requests;
            return total;
        }

        public void print() {
            logger.info("print() - total={}, requests={}, average={}",total,requests,average);
        }
    }

    /**
     * Mutable object -- thread-safe
     */
    private static class SafeStatistics extends UnsafeStatistics {
        public int add(final int value) {
            if (value < 0)
                throw new IllegalArgumentException();
            synchronized (this) {
                return super.add(value);
            }
        }
    }

    UnsafeStatistics unsafe;
    SafeStatistics safe;

    @BeforeEach
    public void setup() {
        unsafe = new UnsafeStatistics();
        safe = new SafeStatistics();
    }

    @Test
    public void testNew_Floodgate_Pass() {
        Floodgate<Integer> floodgateAdd = new Floodgate<>(UnsafeStatistics.class,() -> unsafe.add(10));
        Floodgate<Void> floodgatePrint = new Floodgate<>(UnsafeStatistics.class,() -> unsafe.print());

        Floodgate<Integer> floodgateAdd2 = new Floodgate<>(UnsafeStatistics.class,10,5,() -> unsafe.add(10));
        Floodgate<Void> floodgatePrint2 = new Floodgate<>(UnsafeStatistics.class,12,6,() -> unsafe.print());


        assertTrue(floodgateAdd.getName().contains("UnsafeStatistics"));
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,floodgateAdd.getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,floodgateAdd.getIterations());
        assertEquals(States.CLOSED, floodgateAdd.getState());

        assertTrue(floodgatePrint.getName().contains("UnsafeStatistics"));
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,floodgatePrint.getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,floodgatePrint.getIterations());
        assertEquals(States.CLOSED, floodgatePrint.getState());

        assertEquals(10,floodgateAdd2.getThreads());
        assertEquals(5,floodgateAdd2.getIterations());

        assertEquals(12,floodgatePrint2.getThreads());
        assertEquals(6,floodgatePrint2.getIterations());
    }

}
