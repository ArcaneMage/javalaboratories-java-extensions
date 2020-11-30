package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class PromisesTest extends AbstractConcurrencyTest {

    @Test
    public void testNewPromise_Promise_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNewPromise_Promise_Pass")));

        // Then
        promise.getResult().orElseThrow();
        assertNotNull(promise);
        assertEquals(FULFILLED,promise.getState());
        assertTrue(promise.toString().contains("promise"));
    }

    @Test
    public void testAll_Promises_Pass() {
        CountDownLatch latch = new CountDownLatch(4);
        // Given
        List<PrimaryAction<Integer>> actions = Arrays.asList(
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[0]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[1]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[2]")),
                PrimaryAction.of(() -> doLongRunningTask("testAll_Promises_Pass[3]"))
        );

        // When
        List<Promise<Integer>> promises = Promises.all(actions);

        // Then
        wait("testAll_Promises_Pass");
        assertEquals(4,promises.size());
        //promises.forEach(p -> assertEquals(FULFILLED,p.getState()));
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
