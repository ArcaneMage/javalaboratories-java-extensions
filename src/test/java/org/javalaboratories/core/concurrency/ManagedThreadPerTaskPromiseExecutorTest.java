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
package org.javalaboratories.core.concurrency;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javalaboratories.core.concurrency.ManagedPromiseService.ServiceStates.CLOSING;
import static org.javalaboratories.core.concurrency.ManagedPromiseService.ServiceStates.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagedThreadPerTaskPromiseExecutorTest extends AbstractConcurrencyTest {

    private ManagedThreadPerTaskPromiseExecutor service;

    private static final Logger logger = LoggerFactory.getLogger(ManagedThreadPerTaskPromiseExecutorTest.class);

    @BeforeEach
    public void setup() {
        service = new ManagedThreadPerTaskPromiseExecutor(4, false);
    }

    @AfterEach
    public void tearDown() {
        service.signalTerm();
    }

    @Test
    public void testStop_Timeout_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedThreadPerTaskPromiseExecutor.class);
        service.execute(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        service.stop(250,false);

        // Then
        assertTrue(logCaptor.getInfoLogs().contains("Not all virtual promises kept following shutdown -- forced shutdown"));
    }

    @Test
    public void testStop_TimeoutRetries_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedThreadPerTaskPromiseExecutor.class);
        service.execute(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        service.stop(250,true);

        // Then
        assertTrue(logCaptor.getInfoLogs()
                .stream()
                .anyMatch(s -> s.contains("Awaiting termination of some virtual promises")));
    }

    @Test
    public void testStop_Interruption_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(AbstractManagedPromiseService.class);
        Thread main = Thread.currentThread();
        service.execute(() -> doLongRunningTask("testStop_Interruption_Pass"));
        service.execute(() -> { sleep(350); main.interrupt();});

        // When
        service.stop(500,false);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Termination of threads (promises) interrupted -- promises not kept"));
    }

    @Test
    public void testStop_InvalidTimeout_Pass () {
        assertThrows(IllegalArgumentException.class, () -> service.stop(50,false));
    }

    @Test
    public void testSignalTerm_Manual_Pass () {
        // Given
        service.signalTerm();

        // Then
        assertEquals(INACTIVE, service.getState());
    }

    @Test
    public void testSignalTerm_ProcessShutdownSimulation_Pass () {
        // Given
        service.execute(() -> doLongRunningTask("testSignalTerm_ProcessShutdownSimulation_Pass"));
        fireSigTerm(service);

        // When
        service.signalTerm();

        // Then
        assertEquals(CLOSING, service.getState());
    }
}
