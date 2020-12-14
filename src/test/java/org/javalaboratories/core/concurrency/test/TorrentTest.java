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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.javalaboratories.core.concurrency.test.ResourceFloodTester.States.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TorrentTest extends AbstractResourceFloodTest {

    @BeforeEach
    public void setup() {
        unsafe = new UnsafeStatistics();
        safe = new SafeStatistics();
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

        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,torrent.floodgates().get(0).getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,torrent.floodgates().get(0).getIterations());
        assertEquals(Floodgate.DEFAULT_FLOOD_WORKERS,torrent.floodgates().get(1).getThreads());
        assertEquals(Floodgate.DEFAULT_FLOOD_ITERATIONS,torrent.floodgates().get(1).getIterations());

        assertEquals(5,torrent2.floodgates().get(0).getThreads());
        assertEquals(1,torrent2.floodgates().get(0).getIterations());
        assertEquals(6,torrent2.floodgates().get(1).getThreads());
        assertEquals(2,torrent2.floodgates().get(1).getIterations());

        assertTrue(torrent.toString().contains("[target=ResourceFloodTester.Target(name={IndeterminateTarget-001}),state=CLOSED,floodgates=2,flood-controller=External]"));
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
    public void testFlood_TargetResources_Pass() {
        // Given
        Torrent torrent = Torrent.builder(UnsafeStatistics.class)
                .withFloodgate(() -> unsafe.print())
                .withFloodgate(() -> unsafe.add(10))
                .build();

        // When
        torrent.open();
        Map<String, List<?>> result = torrent.flood();

        // Then
        assertEquals(FLOODED,torrent.getState());
        assertEquals(2, result.size());


    }
}
