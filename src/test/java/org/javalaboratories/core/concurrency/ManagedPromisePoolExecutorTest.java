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

public class ManagedPromisePoolExecutorTest extends AbstractConcurrencyTest {

    private ManagedPromisePoolExecutor pool;

    private static final Logger logger = LoggerFactory.getLogger(ManagedPromisePoolExecutorTest.class);

    @BeforeEach
    public void setup() {
        pool = new ManagedPromisePoolExecutor(4, false);
    }

    @AfterEach
    public void tearDown() {
        pool.signalTerm();
    }

    @Test
    public void testStop_Timeout_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedPromiseService.class);
        pool.submit(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        pool.stop(250,false);

        // Then
        assertTrue(logCaptor.getInfoLogs().contains("Not all promises kept following shutdown -- forced shutdown"));
    }

    @Test
    public void testStop_TimeoutRetries_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedPromiseService.class);
        pool.submit(() -> doLongRunningTask("testStop_Timeout_Pass"));

        // When
        pool.stop(250,true);

        // Then
        assertTrue(logCaptor.getInfoLogs()
                .stream()
                .anyMatch(s -> s.contains("Awaiting termination of some promise")));
    }

    @Test
    public void testStop_Interruption_Pass () {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ManagedPromiseService.class);
        Thread main = Thread.currentThread();
        pool.submit(() -> doLongRunningTask("testStop_Interruption_Pass"));
        pool.execute(() -> { sleep(350); main.interrupt();});

        // When
        pool.stop(500,false);

        // Then
        assertTrue(logCaptor.getErrorLogs().contains("Termination of threads (promises) interrupted -- promises not kept"));
    }

    @Test
    public void testStop_InvalidTimeout_Pass () {
        assertThrows(IllegalArgumentException.class, () -> pool.stop(50,false));
    }

    @Test
    public void testSignalTerm_Manual_Pass () {
        // Given
        pool.signalTerm();

        // Then
        assertEquals(INACTIVE, pool.getState());
    }

    @Test
    public void testSignalTerm_ProcessShutdownSimulation_Pass () {
        // Given
        pool.submit(() -> doLongRunningTask("testSignalTerm_ProcessShutdownSimulation_Pass"));
        fireSigTerm();

        // When
        pool.signalTerm();

        // Then
        assertEquals(CLOSING, pool.getState());
    }

    private void fireSigTerm() {
        // Intentionally fire simulated SIGTERM from thread outside of "Promises Group"
        Thread t = new Thread(() -> {pool.signalTerm(); logger.debug("Issued SIGTERM signal");});
        t.start();
        sleep(16); // Current thread sleeps to allow other threads to run
    }
}
