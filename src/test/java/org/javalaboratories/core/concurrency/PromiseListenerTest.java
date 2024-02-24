package org.javalaboratories.core.concurrency;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;
import static org.javalaboratories.core.concurrency.PromiseEvent.Actions.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class PromiseListenerTest extends AbstractConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(PromiseListenerTest.class);

    private static final int DEFAULT_LISTENER_TIMEOUT = 128;

    private BiConsumer<Integer,Throwable> intResponse;

    private List<PromiseEventListener> listeners;
    private LogCaptor logCaptor;

    @BeforeEach
    public void setup() {
        intResponse = (result, exception) -> {
            if (exception != null) {
                logger.error("Exception detected in complete handler:", exception);
            } else {
                logger.info("Result received in complete handler: {}", result);
            }
        };

        listeners = Arrays.asList(
                new PromiseEventListener("Listener-A"),
                new PromiseEventListener("Listener-B"),
                new PromiseEventListener("Listener-C")
        );
        logCaptor = LogCaptor.forClass(PromiseListenerTest.class);

    }

    @Test
    public void testNew_PrimaryAction_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryAction_Pass")),listeners);

        // When
        waitMessage("testNew_PrimaryAction_Pass");
        int value  = promise.getResult().orElseThrow();
        awaitListeners(1, DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandler_Pass() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testNew_PrimaryActionCompleteHandler_Pass"),intResponse),
                listeners);

        // When
        int value = promise.getResult().orElseThrow();
        awaitListeners(1, DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        waitMessage("testNew_PrimaryActionCompleteHandler_Pass");
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,value);
    }

    @Test
    public void testNew_PrimaryActionCompleteHandlerException_Fail() {
        // Given
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTaskWithException("testNew_PrimaryActionCompleteHandlerException_Pass"),intResponse),
                listeners);

        // Then
        assertEquals(3,listeners.size());
        waitMessage("testNew_PrimaryActionCompleteHandlerException_Pass");
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 0));
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Exception detected in complete handler")));
    }

    @Test
    public void testThenAccept_Consumer_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);

        Promise<Integer> promise = Promises.newPromise(() -> doLongRunningTask("testThenAccept_Consumer_Pass"),listeners)
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
        Promise<Integer> promise = Promises.newPromise(() -> doLongRunningTask("testThenApply_Function_Pass"),listeners)
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
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskAction_Pass")),listeners)
                .then(TaskAction.of(value -> getValue(received, () -> value)));

        // When
        waitMessage("testThen_TaskAction_Pass");
        promise.await();
        awaitListeners(2,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 2));
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testThen_TaskActionCompleteHandler_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskActionCompleteHandler_Pass")),listeners)
                .then(TaskAction.of(value -> getValue(received, () -> value),intResponse));

        // When
        waitMessage("testThen_TaskActionCompleteHandler_Pass");
        promise.await();
        awaitListeners(2,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 2));
        assertEquals(FULFILLED,promise.getState());
        assertEquals(127,received.get());
    }

    @Test
    public void testThen_TaskActionCompleteHandlerException_Fail() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TaskActionCompleteHandlerException_Pass")),listeners)
                .then(TaskAction.of(value -> getValue(received, () -> value / 0),intResponse));

        // When
        waitMessage("testThen_TaskActionCompleteHandlerException_Pass");
        promise.await();
        awaitListeners(1,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        // Interestingly enough this works because the reference of the "then" method
        // is assigned to the promise object :)
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Exception detected in complete handler")));
    }

    @Test
    @Disabled
    public void testThen_TransmuteAction_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteAction_Pass")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> value + 1)));

        // When
        waitMessage("testThen_TransmuteAction_Pass");
        promise.await();
        awaitListeners(2,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 2));
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    @Disabled
    public void testThen_TransmuteActionTypeTest_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<String> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteAction_Pass")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> value + 1)))
                .then(TransmuteAction.of(value -> value+""));

        // When
        waitMessage("testThen_TransmuteActionTypeTest_Pass");
        String result = promise.getResult().get();
        awaitListeners(3,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 3));
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
        assertEquals("128",result);
    }

    @Test
    public void testThen_TransmuteActionCompleteHandlerException_Fail() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandlerException_Pass")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0),intResponse));

        // When
        waitMessage("testThen_TransmuteActionCompleteHandlerException_Pass");
        promise.await();
        awaitListeners(1, DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Exception detected in complete handler")));
    }

    @Test
    @Disabled
    public void testThen_TransmuteActionCompleteHandler_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testThen_TransmuteActionCompleteHandler")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> value + 1),intResponse));

        // When
        waitMessage("testThen_TransmuteActionCompleteHandler_Pass");
        promise.await();
        awaitListeners(2,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 2));
        assertEquals(FULFILLED,promise.getState());
        int value = received.get();
        assertEquals(128,value);
    }

    @Test
    public void testHandle_TransmuteActionException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testHandle_TransmuteActionException_Pass")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0)))
                .handle((e) -> logger.error("testHandle_TransmuteActionException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // When
        awaitListeners(1,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Houston we have a problem in the main thread!!")));
    }

    @Test
    public void testHandle_TransmuteActionCompleteHandlerException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testHandle_TransmuteActionException_Pass")),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1) / 0),intResponse))
                .handle((e) -> logger.error("testHandle_TransmuteActionCompleteHandlerException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // When
        assertEquals(3,listeners.size());
        awaitListeners(1,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Houston we have a problem in the main thread!!")));
    }

    @Test
    @SuppressWarnings("NumericOverflow")
    public void testHandle_HandleFailFastException_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(()-> getValue(received, () ->  128 / 0)),listeners)
                .then(TransmuteAction.of(value -> getValue(received, () -> (value + 1))))
                .handle((e) -> logger.error("testHandle_HandleFailFastException_Pass <-- Houston we have a problem in the main thread!! :)",e));

        // When
        assertEquals(3,listeners.size());

        // Then
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 0));
        assertEquals(REJECTED,promise.getState());
        assertTrue(logCaptor.getErrorLogs().stream().anyMatch(s -> s.contains("Houston we have a problem in the main thread!!")));
    }

    @Test
    public void testAwait_Promises_Pass() {
        // Given
        AtomicInteger received = new AtomicInteger(0);
        Promise<Integer> promise = Promises.newPromise(PrimaryAction.of(() -> doLongRunningTask("testAwait_Promises_Pass")),listeners)
                .then(TaskAction.of(value -> getValue(received, () -> value)));

        // When
        waitMessage("testAwait_Promises_Pass");
        promise.await();
        awaitListeners(2,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        logger.debug("listeners={}",listeners);
        assertEquals(FULFILLED,promise.getState());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 2));
        assertEquals(127,received.get());
    }

    @Test
    public void testGetAction_Pass() {
        // Given
        PrimaryAction<Integer> action = PrimaryAction.of(() -> null);
        Promise<Integer> promise = Promises.newPromise(action,listeners);
        assertEquals(action,promise.getAction());
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());

        // When
        awaitListeners(1,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        logger.debug("listeners={}",listeners);
        assertEquals(FULFILLED,promise.getState());
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
    }

    @Test
    public void testToString_Pass() {
        // Given
        PrimaryAction<Integer> action = PrimaryAction.of(() -> null);
        Promise<Integer> promise = Promises.newPromise(action,listeners);

        // When
        awaitListeners(1,DEFAULT_LISTENER_TIMEOUT);

        // Then
        assertEquals(3,listeners.size());
        assertThrows(NoSuchElementException.class, () -> promise.getResult().orElseThrow());
        assertTrue(promise.toString().contains("state=ACTIVE,shutdownHook=enabled"));
        assertTrue(listeners.stream().allMatch(listener -> listener.getEvents() == 1));
        assertEquals(FULFILLED,promise.getState());
    }

    private void awaitListeners(int expectedEvents, long await) {
        long start = System.currentTimeMillis();
        long elapsed = 0;
        while (!listeners.stream().allMatch(listener -> listener.getEvents() == expectedEvents) && elapsed <= await) {
            sleep(32);
            elapsed = System.currentTimeMillis() - start;
        }
        logger.debug("awaitListeners elapsed time={}",elapsed);
    }

    public static class PromiseEventListener implements PromiseEventSubscriber<Integer> {
        private final String name;
        private int events;

        public PromiseEventListener(final String name) {
            events = 0;
            this.name = name;
        }
        @Override
        public void notify(final PromiseEvent<Integer> event) {
            if (event.isAny(PRIMARY_ACTION,TASK_ACTION,TRANSMUTE_ACTION)) {
                logger.info("Listener {} received event={}, state={}",name,event.getEventId(),event.getValue());
                events++;
            }
        }
        public int getEvents() {
            return events;
        }
        public String toString() {return "[name="+name+", events="+events+"]";}
    }
}
