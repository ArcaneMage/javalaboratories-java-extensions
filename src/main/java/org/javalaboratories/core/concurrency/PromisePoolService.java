package org.javalaboratories.core.concurrency;

import org.javalaboratories.core.handlers.Handlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@code PromisePoolService} is a custom thread pool executor designed for use
 * with {@link Promise} objects.
 * <p>
 * If required, but rarely necessary, it is possible to provide an alternative
 * thread pool. Achieving this involves configuring the
 * {@code promise-configuration.properties} file, but it is required that the
 * thread pool must inherit from {@link PromisePoolService} class.
 */
@SuppressWarnings("WeakerAccess")
public class PromisePoolService extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(PromisePoolService.class);

    public enum ServiceStates {ACTIVE, CLOSING, INACTIVE}

    private static final AtomicInteger workerIndex = new AtomicInteger(0);
    private static final String WORKER_THREAD_NAME="Promise-Worker-%d";
    private static final long SHUTDOWN_WAIT_TIMEOUT = 5L;

    private final int capacity;
    private final AtomicReference<ServiceStates> state;
    private final Thread shutdownHook;

    public PromisePoolService(final int capacity) {
        super(capacity,capacity,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(),PromisePoolService::newPromiseWorker);
        this.capacity = capacity;
        this.shutdownHook = new Thread(Handlers.runnable(this::signalTerm));
        this.state = new AtomicReference<>(ServiceStates.ACTIVE);
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    protected ServiceStates getState() {
        return state.get();
    }

    public final void free() {
        free(SHUTDOWN_WAIT_TIMEOUT,true);
    }

    public final void free(final long timeout, final boolean retry) {
        if ( timeout < 1 )
            throw new IllegalArgumentException("Insufficient timeout");
        if ( getState() == ServiceStates.ACTIVE) {
            changeState(ServiceStates.ACTIVE, ServiceStates.CLOSING);
            int i = 0;
            shutdown();
            try {
                while ( !awaitTermination(timeout, TimeUnit.SECONDS) && retry ) {
                    logger.info("Awaiting termination of some promises  -- elapsed {} seconds", ++i * SHUTDOWN_WAIT_TIMEOUT);
                }
                if ( !isTerminated() ) {
                    logger.debug("Not all promises kept following shut down.");
                }
            } catch (InterruptedException e) {
                logger.error("Termination of capacity (promises) interrupted -- promises not kept");
            } finally {
                changeState(ServiceStates.CLOSING, ServiceStates.INACTIVE);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("[capacity=%d,state=%s,shutdownHook=%s]",capacity,state,shutdownHook.getState());
    }

    private static Thread newPromiseWorker(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,workerIndex.incrementAndGet());
        Thread result = new Thread(runnable);
        result.setName(name);
        return result;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void changeState(ServiceStates from, ServiceStates to) {
        // Atomically changes states using low-level
        // CAS strategy (compare-and-swap)
        do {
        } while (!state.compareAndSet(from, to));
    }

    private void signalTerm() throws Exception  {
        if ( getState() != ServiceStates.ACTIVE ) { // Must be already in the process of termination
            logger.debug("Termination signal received, but ignored -- unnecessary");
            return;
        }
        logger.debug("Termination signal received -- shutting down gracefully");
        try {
            free();
        } finally {
            logger.debug("Termination concluded");
        }
    }
}
