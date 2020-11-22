package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;
import static org.junit.jupiter.api.Assertions.*;

public class PromiseTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseTest.class);

    private BiConsumer<Integer,Throwable> intResponse;
    private BiConsumer<Void,Throwable> voidResponse;

    @BeforeEach
    public void setup() {
        intResponse = (result, exception) -> {
            if ( exception != null ) {
                logger.error("Exception detected in complete handler:", exception);
            } else {
                logger.info("Result received in complete handler: {}", result);
            }
        };

        voidResponse = (result, exception) -> {
            if ( exception != null ) {
                logger.error("Exception detected in complete handler:", exception);
            } else {
                logger.info("Result received in complete Void handler: {}", result);
            }
        };
    }

    @Test
    public void testNew_PrimaryAction_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryAction_Pass")));

        // Then
        wait("testNew_PrimaryAction_Pass");
        int value  = promise.getResult().orElseThrow();
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandler_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryActionCompleteHandler_Pass"), intResponse));

        // Then
        wait("testNew_PrimaryActionCompleteHandler_Pass");
        int value = promise.getResult().orElseThrow();
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandlerException_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTaskWithException("testNew_PrimaryActionCompleteHandlerException_Pass"),intResponse));

        // Then
        wait("testNew_PrimaryActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testThen_Action_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_Action_Pass")))
                .then(Action.of(value -> getValue(latch, received, () -> value)));

        // Then
        wait(latch,"testThen_Action_Pass");
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());

    }

    @Test
    public void testThen_ActionCompleteHandler_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_ActionCompleteHandler_Pass")))
                .then(Action.of(value -> getValue(latch, received, () -> value),voidResponse));

        // Then
        wait(latch,"testThen_ActionCompleteHandler_Pass");
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());

    }

    @Test
    public void testThen_ActionCompleteHandlerException_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_ActionCompleteHandlerException_Pass")))
                .then(Action.of(value -> getValue(latch, received, () -> value / 0),voidResponse));

        // Then
        wait(latch,"testThen_ActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        // Interestingly enough this works because of compiler optimization. The reference of the "then" method
        // is assigned to the promise object :)
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testThen_TransmuteAction_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteAction_Pass")))
                .then(TransmuteAction.of(value -> getValue(latch, received, () -> value + 1)));

        // Then
        wait(latch,"testThen_TransmuteAction_Pass");
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testThen_TransmuteActionCompleteHandlerException_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandlerException_Pass")))
                .then(TransmuteAction.of(value -> getValue(latch, received, () -> (value + 1) / 0),intResponse));

        // Then
        wait(latch,"testThen_TransmuteActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testThen_TransmuteActionCompleteHandler_Pass() {
        // Given
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandler")))
                .then(TransmuteAction.of(value -> getValue(latch, received, () -> value + 1),intResponse));

        // Then
        wait(latch,"testThen_TransmuteActionCompleteHandler_Pass");
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testHandle_TransmuteActionException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testHandle_TransmuteActionException_Pass")))
                .then(TransmuteAction.of(value -> getValue(null, received, () -> (value + 1) / 0)))
                .handle((e) -> logger.error("testHandle_TransmuteActionException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // Then
        assertEquals(REJECTED,promise.getState());
    }

    @Test
    public void testGetAction_Pass() {
        PrimaryAction<Void> action = PrimaryAction.of(() -> null);
        Promise<Void> promise = Promises.newPromise(action);

        assertEquals(action,promise.getAction());
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());

        assertEquals(FULFILLED,promise.getState());
    }

    @Test
    public void testToString_Pass() {
        PrimaryAction<Void> action = PrimaryAction.of(() -> null);
        Promise<Void> promise = Promises.newPromise(action);

        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertTrue(promise.toString().contains("state=FULFILLED,service=[capacity=24,state=ACTIVE,shutdownHook=NEW"));

        assertEquals(FULFILLED,promise.getState());
    }


    private int getValue(CountDownLatch latch, AtomicInteger atomicInteger, Supplier<Integer> supplier) {
        try {
            int value = supplier.get();
            atomicInteger.set(value);
            logger.info("Received and processed value ({}) asynchronously", atomicInteger.get());
            return value;
        } finally {
            if ( latch != null )
                latch.countDown();
        }
    }

    private void wait(String testcase, Object... params) {
        wait(null,testcase,params);
    }

    private void wait(CountDownLatch latch, String testcase, Object... params) {
        String logMessage = testcase+": Waiting for kept promises";
        logger.info(logMessage,params);
        if ( latch != null ) {
            try {
                latch.await(4,TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Latch interrupted",e);
            }
        }
    }

    private int doLongRunningTaskWithException(String narrative) {
        logger.error("doLongRunningTaskWithException: {} ",narrative);
        throw new IllegalStateException("Threw an exception in promise");
    }

    private int doLongRunningTask(String narrative) {
        //sleep(1000);
        logger.info("doLongRunningTask: {} asynchronously",narrative);
        return 127;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Do nothing
        }
    }
}
