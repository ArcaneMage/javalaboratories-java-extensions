package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class PromisesTest extends AbstractConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(PromisesTest.class);

    @Test
    public void testNewPromise_Promise_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNewPromise_Promise_Pass")));

        // Then
        promise.getResult().orElseThrow();
        assertNotNull(promise);
        assertEquals(FULFILLED,promise.getState());
        assertTrue(promise.toString().matches("^\\[identity=\\{.*},state=FULFILLED,service=\\[capacity=\\d*,state=ACTIVE,shutdownHook=NEW]]"));
    }

    @Test
    public void testAll_Promises_Pass() {
        // Given
        List<PrimaryAction<Integer>> actions = Arrays.asList(
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[0]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[1]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[2]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[3]"))
        );

        // When
        Promise<List<Promise<Integer>>> promise = Promises
                .all(actions,true)
                .then(TaskAction.of(results -> results.forEach(result -> logger.info("Promise states of tasks: {}",result.getState()))));

        // Then
        wait("testAll_Promises_Pass");
        promise.await();
        assertEquals(FULFILLED,promise.getState());
    }

    @Test
    public void testAll_PromisesException_Pass() {
        // Given
        List<PrimaryAction<Integer>> actions = Arrays.asList(
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[0]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[1]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[2]")),
                PrimaryAction.of(() -> doLongRunningTaskWithException("testAll_Promises_Pass[3]"))
        );

        // When
        Promise<List<Promise<Integer>>> promise = Promises
                .all(actions)
                .then(TaskAction.of(results -> results.forEach(result -> logger.info("Promise states of tasks: {}",result.getState()))))
                .handle(e -> logger.error("Reporting error signalled in promise: ",e));


        // Then
        assertEquals(REJECTED,promise.getState());
    }


    // Only enable for manual observation of behaviour
    // If enabled, it is recommended to run this test class exclusively
    @Disabled
    void testShutdown_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> {doLongRunningTask("testShutdown_Pass (10 seconds process)"); sleep(10000); return 255;}));

        System.exit(128);
    }
}
