package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class PromiseTest extends AbstractConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseTest.class);

    private BiConsumer<Integer,Throwable> intResponse;

    @BeforeEach
    public void setup() {
        intResponse = (result, exception) -> {
            if (exception != null) {
                logger.error("Exception detected in complete handler:", exception);
            } else {
                logger.info("Result received in complete handler: {}", result);
            }
        };
    }

    @Test
    public void testNew_PrimaryAction_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryAction_Pass")));

        // Then
        waitMessage("testNew_PrimaryAction_Pass");
        int value  = promise.getResult().orElseThrow();
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandler_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryActionCompleteHandler_Pass"), intResponse));

        // Then
        waitMessage("testNew_PrimaryActionCompleteHandler_Pass");
        int value = promise.getResult().orElseThrow();
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandlerException_Fail() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTaskWithException("testNew_PrimaryActionCompleteHandlerException_Pass"),intResponse));

        // Then
        waitMessage("testNew_PrimaryActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testEquals_Promise_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTaskWithException("testNew_PrimaryActionCompleteHandlerException_Pass"),intResponse));
        Promise<Integer> promise2 = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTaskWithException("testNew_PrimaryActionCompleteHandlerException_Pass"),intResponse));
        Promise<Integer> promise3 = promise;

        boolean equalityTests;

        // When
        equalityTests = promise.equals(promise2) && promise2.equals(promise3);

        // Then
        assertFalse(equalityTests);
    }

    @Test
    public void testThenAccept_Consumer_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);

        Promise<Integer> promise = Promises.newPromise(() -> doLongRunningTask("testThenAccept_Consumer_Pass"))
                .thenAccept(value -> getValue(received, () -> value));

        // When
        waitMessage("testThenAccept_Consumer_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testThenApply_Function_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(() -> doLongRunningTask("testThenApply_Function_Pass"))
                .thenApply(value -> getValue(received, () -> value + 1));

        // When
        waitMessage("testThenApply_Function_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testThen_TaskAction_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskAction_Pass")))
                .then(TaskAction.of(value -> getValue(received, () -> value)));

        // When
        waitMessage("testThen_TaskAction_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testThen_TaskActionCompleteHandler_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskActionCompleteHandler_Pass")))
                .then(TaskAction.of(value -> getValue(received, () -> value),intResponse));

        // When
        waitMessage("testThen_TaskActionCompleteHandler_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testThen_TaskActionCompleteHandlerException_Fail() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskActionCompleteHandlerException_Pass")))
                .then(TaskAction.of(value -> getValue(received, () -> value / 0),intResponse));

        // When
        waitMessage("testThen_TaskActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());

        // Then
        // Interestingly enough this works because the reference of the "then" method
        // is assigned to the promise object :)
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testThen_TransmuteAction_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteAction_Pass")))
                .then(TransmuteAction.of(value -> getValue(received, () -> value + 1)));

        // When
        waitMessage("testThen_TransmuteAction_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testThen_TransmuteActionCompleteHandlerException_Fail() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandlerException_Pass")))
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0),intResponse));

        // When
        waitMessage("testThen_TransmuteActionCompleteHandlerException_Pass");
        promise.await();

        // Then
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testThen_TransmuteActionCompleteHandler_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandler")))
                .then(TransmuteAction.of(value -> getValue(received, () -> value + 1),intResponse));

        // When
        waitMessage("testThen_TransmuteActionCompleteHandler_Pass");
        promise.await();

        // Then
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testHandle_TransmuteActionException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testHandle_TransmuteActionException_Pass")))
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0)))
                .handle((e) -> logger.error("testHandle_TransmuteActionException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // Then
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testHandle_TransmuteActionCompleteHandlerException_Fail() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testHandle_TransmuteActionException_Pass")))
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0),intResponse))
                .handle((e) -> logger.error("testHandle_TransmuteActionCompleteHandlerException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // Then
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    @SuppressWarnings("NumericOverflow")
    public void testHandle_HandleFailFastException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(()-> getValue(received, () ->  128 / 0)))
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1))))
                .handle((e) -> logger.error("testHandle_HandleFailFastException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // Then
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testAwait_Promises_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testAwait_Promises_Pass")))
                .then(TaskAction.of(value -> getValue(received, () -> value)));

        // Then
        waitMessage("testAwait_Promises_Pass");
        promise.await();

        // Given
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testGetAction_Pass() {
        // Given
        PrimaryAction<Void> action = PrimaryAction.of(() -> null);
        Promise<Void> promise = Promises.newPromise(action);

        // Then
        assertEquals(action,promise.getAction());
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(FULFILLED,promise.getState());
    }

    @Test
    public void testToString_Pass() {
        // Given
        PrimaryAction<Void> action = PrimaryAction.of(() -> null);
        Promise<Void> promise = Promises.newPromise(action);

        // Then
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertTrue(promise.toString().contains("state=ACTIVE,shutdownHook=NEW"));
        assertEquals(FULFILLED,promise.getState());
    }
}
