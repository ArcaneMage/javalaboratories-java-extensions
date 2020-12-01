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

import org.javalaboratories.core.handlers.Handlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@code PoolExecutorService} is a custom thread pool executor designed
 * for use with {@link Promise} objects.
 * <p>
 * If required, but rarely necessary, it is possible to provide an alternative
 * thread pool. Achieving this involves configuring the
 * "{@code promise-configuration.properties}" file, but it is required that the
 * thread pool must inherit from {@link ManagedPromisePoolExecutor} class.
 * <p>
 * When the JVM is signalled to shutdown, whether via SIGTERM or through natural
 * program termination, the thread pool will wait for any outstanding running
 * {@code Promise} threads to terminate before concluding. Therefore, it is
 * important that {@code Promise} objects reach to a natural conclusion. It is not
 * advisable for threads to run infinitely. If this is a possibility then it
 * would to be prudent to to force shutdown the pool service with the
 * {@link ManagedPromisePoolExecutor#free(long, boolean)} specifying a timeout without
 * retries ahead of program termination.
 * <p>
 * Currently, various strategies are under consideration to improve shutdown
 * behaviour.
 */
public class ManagedPromisePoolExecutor extends ThreadPoolExecutor implements ManagedPoolService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedPoolService.class);

    private static final AtomicInteger workerIndex = new AtomicInteger(0);
    private static final String WORKER_THREAD_NAME="Promise-Worker-%d";

    private final int capacity;
    private final AtomicReference<ServiceStates> state;
    private final Thread shutdownHook;

    /**
     * Constructs an instance of this thread pool.
     * <p>
     * Constructor called from the {@link PromisePoolServiceFactory}, if
     * configured to create an instance of this object.
     * @param capacity Number maximum thread workers to carryout promises.
     */
    public ManagedPromisePoolExecutor(final int capacity) {
        super(capacity,capacity,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(), ManagedPromisePoolExecutor::newPromiseWorker);
        this.capacity = capacity;
        this.state = new AtomicReference<>(ServiceStates.ACTIVE);
        this.shutdownHook = new Thread(Handlers.runnable(() -> signalTerm(this::logShutdownState)));
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    /**
     * Returns the current state of this pool service.
     * <p>
     * Indicates whether the thread pool is in an {@link ServiceStates#ACTIVE}
     * state, the normal mode of operation where tasks of {@link Action} objects
     * are accepted for processing. Any other state results in task being
     * rejected and the thread pool actively in the processing of shutting down.
     *
     * @return the current state of the {@link ManagedPromisePoolExecutor}
     */
    @Override
    public ServiceStates getState() {
        return state.get();
    }

    @Override
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
                    shutdownNow();
                    logger.debug("Not all promises kept following shutdown -- forced shutdown.");
                }
            } catch (InterruptedException e) {
                logger.error("Termination of capacity (promises) interrupted -- promises not kept");
            } finally {
                changeState(ServiceStates.CLOSING, ServiceStates.INACTIVE);
            }
        }
    }

    /**
     * @return a {@code String} representation of this {@link ManagedPromisePoolExecutor} thread pool.
     */
    @Override
    public String toString() {
        return String.format("[capacity=%d,state=%s,shutdownHook=%s]",capacity,state,shutdownHook.getState());
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void changeState(ServiceStates from, ServiceStates to) {
        // Atomically changes states using low-level
        // CAS strategy (compare-and-swap)
        do {
        } while (!state.compareAndSet(from, to));
    }

    private void logShutdownState(ServiceStates state) {
        switch ( state ) {
            case ACTIVE:
                logger.debug("Termination signal received -- shutting down gracefully");
                break;
            case CLOSING:
                logger.debug("Termination signal received, but ignored -- unnecessary");
                break;
            case INACTIVE:
                logger.debug("Termination concluded");
                break;
        }
    }

    private static Thread newPromiseWorker(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,workerIndex.incrementAndGet());
        Thread result = new Thread(runnable);
        result.setName(name);
        return result;
    }
}
