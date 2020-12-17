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

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FloodgateTest extends AbstractResourceFloodStabilityTest {

    @BeforeEach
    public void setup() {
        unsafe = new UnsafeStatistics();
        safe = new SafeStatistics();
    }

    @Test
    public void testNew_Floodgate_Pass() {
        // Given
        Floodgate<Integer> floodgateAdd = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));
        Floodgate<Void> floodgatePrint = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.print());

        Floodgate<Integer> floodgateAdd2 = new Floodgate<>(UnsafeStatistics.class, 10, 5, () -> unsafe.add(10));
        Floodgate<Void> floodgatePrint2 = new Floodgate<>(UnsafeStatistics.class, 12, 6, () -> unsafe.print());

        // Then
        assertTrue(floodgateAdd.getTarget().getName().contains("UnsafeStatistics"));
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS, floodgateAdd.getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS, floodgateAdd.getIterations());
        assertEquals(States.CLOSED, floodgateAdd.getState());

        assertTrue(floodgatePrint.getTarget().getName().contains("UnsafeStatistics"));
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS, floodgatePrint.getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS, floodgatePrint.getIterations());
        assertEquals(States.CLOSED, floodgatePrint.getState());

        assertTrue(floodgateAdd.toString().contains("state=CLOSED,flood-workers=5,flood-iterations=5,flood-marshal=Internal"));

        assertEquals(10, floodgateAdd2.getThreads());
        assertEquals(5, floodgateAdd2.getIterations());

        assertEquals(12, floodgatePrint2.getThreads());
        assertEquals(6, floodgatePrint2.getIterations());
    }

    @Test
    public void testNew_FloodgateException_Fail() {
        // Then
        assertThrows(NullPointerException.class, () -> new Floodgate<>(null, () -> unsafe.add(10)));
        assertThrows(IllegalArgumentException.class, () -> new Floodgate<>(UnsafeStatistics.class, (Supplier<Integer>) null));
        assertThrows(IllegalArgumentException.class, () -> new Floodgate<>(UnsafeStatistics.class, -1, -1, () -> unsafe.add(10)));
    }

    @Test
    public void testOpen_State_Pass() {
        // Given
        Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));

        // When
        floodgate.open();

        // Then
        try {
            assertEquals(States.OPENED, floodgate.getState());
        } finally {
            // Force clean up
            floodgate.close(true);
        }
    }

    @Test
    public void testOpen_IllegalStateException_Fail() {
        // Given
        Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));

        // When
        floodgate.open();

        // Then
        try {
            assertThrows(IllegalStateException.class, floodgate::open);
        } finally {
            // Clean up resources
            floodgate.close(true);
        }
    }

    @Test
    public void testFlood_TargetResource_Pass() {
        // Given
        Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));

        // When
        floodgate.open();
        List<Integer> results = floodgate.flood();

        // Then
        assertEquals(States.FLOODED, floodgate.getState());
        assertEquals(5, results.size());

        logger.info("UnsafeStatics state={}", unsafe);
    }

    @Test
    public void testFlood_RestartingFlood_Fail() {
        // Given
        Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));

        // When
        floodgate.open();
        List<Integer> results = floodgate.flood();

        // Then
        assertThrows(IllegalStateException.class, floodgate::flood);
        assertEquals(5, results.size());
    }

    @Test
    public void testFlood_TargetResourceException_Fail() {
        // Given
        Floodgate<Float> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.div(-5));

        // When
        floodgate.open();
        List<Float> results = floodgate.flood();

        // Then
        assertThrows(IllegalStateException.class, floodgate::flood);
        assertEquals(5, results.size());

        logger.info("UnsafeStatics state={}", unsafe);
    }

    @Test
    public void testFlood_TargetResourceTimeouts_Fail() {
        // Given
        LogCaptor fgCaptor1 = LogCaptor.forClass(Floodgate.class);
            // Floodgate inherits from ConcurrentResourceFloodTester, so capture these logs too
        LogCaptor fgCaptor2 = LogCaptor.forClass(ConcurrentResourceFloodStability.class);

        Floodgate<Void> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.longRunningIO());

        // When
        floodgate.open();

        // Then
        List<Void> results = floodgate.flood(10, TimeUnit.MILLISECONDS);
        assertEquals(States.FLOODED,floodgate.getState());
        assertEquals(0,results.size());

        assertTrue(fgCaptor1.getErrorLogs().stream()
                .anyMatch(l -> l.contains("Insufficient wait timeout specified, not all flood workers have completed their work")));
        assertTrue(fgCaptor2.getErrorLogs().stream()
                .anyMatch(l -> l.contains("Flood workers still active, but SHUTDOWN_TIMEOUT 5 seconds exceeded -- forcing shutdown")));
    }
}
