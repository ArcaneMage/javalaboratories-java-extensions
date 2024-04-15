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

import java.util.concurrent.atomic.AtomicReference;

/**
 * This abstract class provides a means to shut down {@code managed} services
 * gracefully.
 * <p>
 * They implement the {@link ManagedPromiseService} interface, which manage
 * instances of {@link Promise} objects.
 *
 * @see ManagedThreadPerTaskPromiseExecutor
 * @see ManagedThreadPoolPromiseExecutor
 */
public abstract class AbstractManagedPromiseService implements ManagedPromiseService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractManagedPromiseService.class);
    private final Thread shutdownHook;
    private final AtomicReference<ServiceStates> state;
    private final int capacity;

    public AbstractManagedPromiseService(final int capacity, final boolean autoShutdown) {
        if ( autoShutdown ) {
            this.shutdownHook = new Thread(Handlers.runnable(() -> signalTerm(this::logShutdownState)));
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        } else {
            this.shutdownHook = null;
        }
        this.state = new AtomicReference<>(ServiceStates.ACTIVE);
        this.capacity = capacity;
    }

    public final int getCapacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ServiceStates getState() {
        return state.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdownEnabled() {
        return shutdownHook != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop(final long timeout, final boolean retry) {
        if (timeout < MIN_WAIT_TIMEOUT)
            throw new IllegalArgumentException("Insufficient timeout");
        if (getState() == ServiceStates.ACTIVE) {
            changeState(ServiceStates.ACTIVE, ServiceStates.CLOSING);
            try {
                terminate(timeout,retry);
            } catch (InterruptedException e) {
                logger.error("Termination of threads (promises) interrupted -- promises not kept");
            } finally {
                changeState(ServiceStates.CLOSING, ServiceStates.INACTIVE);
            }
        }
    }

    /**
     * Terminates for the managed service.
     * <p>
     * This method is called indirectly from the
     * {@link AbstractManagedPromiseService#stop(long, boolean)} method.
     *
     * @param timeout value in milliseconds.
     * @param retry if {@code true} indefinitely attempts to terminate threads
     *             after shutdown (use with caution).
     * @throws InterruptedException termination is interruptible.
     */
    protected abstract void terminate(final long timeout, final boolean retry) throws InterruptedException;

    /**
     * @return a {@code String} representation of this
     * {@link ManagedPromiseService} thread pool.
     */
    @Override
    public String toString() {
        return String.format("[capacity=%d,state=%s,shutdownHook=%s]", capacity, getState(),
                isShutdownEnabled() ? "enabled" : "disabled");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void changeState(ServiceStates from, ServiceStates to) {
        // Atomically changes states using low-level
        // CAS strategy (compare-and-swap)
        do {
        } while (!state.compareAndSet(from, to));
    }

    private void logShutdownState(ServiceStates state) {
        switch (state) {
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
}
