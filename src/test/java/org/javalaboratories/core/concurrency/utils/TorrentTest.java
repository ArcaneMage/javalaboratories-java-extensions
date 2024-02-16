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

import org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TorrentTest extends AbstractResourceFloodStabilityTest {

    private UnsafeStatistics expected;

    @BeforeEach
    public void setup() {
        unsafe = new UnsafeStatistics();
        safe = new SafeStatistics();
        expected = new UnsafeStatistics(250,25,10.0f);
    }

    @Test
    public void testNew_Torrent_Pass() {
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        Torrent torrent2 = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(5,1,() -> unsafe.add(20))
                .withFloodgate(6,2,() -> unsafe.print())
                .build();

        assertEquals(2,torrent.size());
        assertEquals(2,torrent2.size());

        assertEquals(CLOSED,torrent.getState());
        assertEquals(CLOSED,torrent2.getState());

        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,torrent.toList().get(0).getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,torrent.toList().get(0).getIterations());
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,torrent.toList().get(1).getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,torrent.toList().get(1).getIterations());

        assertEquals(5,torrent2.toList().get(0).getThreads());
        assertEquals(1,torrent2.toList().get(0).getIterations());
        assertEquals(6,torrent2.toList().get(1).getThreads());
        assertEquals(2,torrent2.toList().get(1).getIterations());

        assertTrue(torrent.toString().contains("stability=STABLE),state=CLOSED,floodgates=2,flood-marshal=External"));
    }

    @Test
    public void testNew_TorrentException_Fail() {
        // Then
        assertThrows(NullPointerException.class, () -> Torrent.builder(null));
        assertThrows(IllegalArgumentException.class, () -> Torrent.builder(UnsafeStatistics.class).build());
    }

    @Test
    public void testOpen_State_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // When
        torrent.open();

        // Then
        try {
            assertEquals(OPENED, torrent.getState());
            assertEquals(2, torrent.size());
            torrent.forEach(fg -> assertEquals(OPENED, fg.getState()));
        } finally {
            // Force clean up
            torrent.close(true);
        }
    }

    @Test
    public void testOpen_IllegalStateException_Fail() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // When
        torrent.open();

        // Then
        try {
            assertEquals(OPENED, torrent.getState());
            assertEquals(2, torrent.size());
            assertThrows(IllegalStateException.class, torrent::open);
        } finally {
            // Force clean up
            torrent.close(true);
        }
    }

    @Test
    public void testFlood_IllegalStateException_Fail() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // Then
        assertEquals(CLOSED, torrent.getState());
        assertEquals(2, torrent.size());
        assertThrows(IllegalStateException.class, torrent::flood);
    }


    @Test
    public void testClose_State_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // When
        torrent.open();
        torrent.close();

        assertEquals(CLOSED,torrent.getState());
    }

    @Test
    public void testClose_IllegalStateException_Fail() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // When
        States state = torrent.getState();

        // Then
        assertEquals(CLOSED,state);
        assertThrows(IllegalStateException.class, torrent::close);
    }


    @Test
    public void testIterator_Torrent_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // Then
        assertEquals(2,torrent.size());
        torrent.forEach(fg -> {
            assertEquals(CLOSED,fg.getState());
            assertEquals(5,fg.getThreads());
            assertEquals(5,fg.getIterations());
            assertTrue(fg.toString().contains("state=CLOSED,flood-workers=5,flood-iterations=5,flood-marshal=External"));
            assertTrue(fg.getTarget().getName().contains("UnsafeStatistics"));
        });
    }

    @Test
    public void testToList_UnsupportedOperationException_Fail() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.add(10))
                .withFloodgate(() -> unsafe.print())
                .build();

        // When
        List<ConcurrentResourceFloodStability<?>> floodgates = torrent.toList();

        // Then
        assertEquals(2,floodgates.size());
        ConcurrentResourceFloodStability<?> floodgate = floodgates.get(0);

        assertThrows(UnsupportedOperationException.class,floodgate::open);
        assertThrows(UnsupportedOperationException.class,floodgate::flood);
        assertThrows(UnsupportedOperationException.class,floodgate::close);
    }

    @Test
    public void testFlood_TargetResources_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate("print", () -> unsafe.print())
                .withFloodgate("add", () -> unsafe.add(10))
                .build();

        // When
        torrent.open();
        Map<String, List<?>> result = torrent.flood();

        // Then
        assertEquals(FLOODED, torrent.getState());
        assertEquals(2, result.size());
        assertEquals(10,torrent.getThreads());
        assertEquals(50,torrent.getIterations());
        assertEquals(5.0,torrent.getAverageIterations());

        if (!unsafe.equals(expected)) {
            logger.info("Statistics corrupted this time, expected: {}, but got {}", expected, unsafe);
        }
    }

    @Test
    public void testFlood_TargetUnstableResources_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate("print", () -> unsafe.print())
                .withFloodgate("div", () -> unsafe.div(-1))
                .build();

        // When
        torrent.open();
        Map<String, List<?>> result = torrent.flood();

        // Then
        assertEquals(FLOODED, torrent.getState());
        assertEquals(2, result.size());

        if (!unsafe.equals(expected)) {
            logger.info("Statistics corrupted this time, expected: {}, but got {}", expected, unsafe);
        }
    }

}
