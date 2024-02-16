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

public class ManagedThreadPoolPromiseExecutorTest extends AbstractConcurrencyTest {

    private ManagedThreadPoolPromiseExecutor service;

    private static final Logger logger = LoggerFactory.getLogger(ManagedThreadPoolPromiseExecutorTest.class);

    @BeforeEach
    public void setup() {
        service = new ManagedThreadPoolPromiseExecutor(4, false);
    }

    @AfterEach
    public void tearDown() {
        service.signalTerm();
    }

    @Test
    public void testStop_Timeout_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedThreadPoolPromiseExecutor.class);
        service.execute(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        service.stop(64,false);

        // Then
        assertTrue(logCaptor.getInfoLogs().contains("Not all promises kept following shutdown -- forced shutdown"));
    }

    @Test
    public void testStop_TimeoutRetries_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedThreadPoolPromiseExecutor.class);
        service.execute(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        service.stop(64,true);

        // Then
        assertTrue(logCaptor.getInfoLogs()
                .stream()
                .anyMatch(s -> s.contains("Awaiting termination of some promises")));
    }

    @Test
    public void testStop_Interruption_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(AbstractManagedPromiseService.class);
        Thread main = Thread.currentThread();
        service.execute(() -> doLongRunningTask("testStop_Interruption_Pass"));
        service.execute(() -> { sleep(64); main.interrupt();});

        // When
        service.stop(128,false);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Termination of threads (promises) interrupted -- promises not kept"));
    }

    @Test
    public void testStop_InvalidTimeout_Pass () {
        assertThrows(IllegalArgumentException.class, () -> service.stop(24,false));
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
