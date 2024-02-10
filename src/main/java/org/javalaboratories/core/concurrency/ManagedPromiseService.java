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

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Thread pools that implement this interface are considered managed in that
 * they have the ability to shut down threads gracefully in the event of a
 * SIGTERM signal received by the JVM.
 * <p>
 * The contract of this interface is that the
 * {@link ManagedPromiseService#signalTerm()} must be invoked having received
 * SIGTERM signal. Following this, the managed pool will wait for outstanding
 * promises for {@link ManagedPromiseService#WAIT_TIMEOUT} time before
 * re-attempting to terminate them -- this will continue until all promises
 * have concluded. It is for this reason that {@code thread} objects
 * are carefully written such that they do not block the shutdown sequence
 * indefinitely.
 * <p>
 * Other shutdown strategies are currently being considered.
 */
public interface ManagedPromiseService extends Executor {
    enum ServiceStates {ACTIVE, CLOSING, INACTIVE}

    long MIN_WAIT_TIMEOUT = 250L;
    long WAIT_TIMEOUT = 5000L;

    /**
     * Returns the current state of this pool service.
     * <p>
     * Indicates whether the thread pool is in an {@link ServiceStates#ACTIVE}
     * state, the normal mode of operation where tasks of {@link Action} objects
     * are accepted for processing. Any other state results in task being
     * rejected and the thread pool actively in the process of shutting down.
     *
     * @return the current state of the {@link ManagedPromiseService}
     */
    ServiceStates getState();

    /**
     * @return {@code true} to indicate the auto-shutdown of the thread pool is
     * enabled.
     */
    boolean isShutdownEnabled();

    /**
     * Calling this method starts the shutting down process of the
     * {@link ManagedPromisePoolExecutor} thread pool.
     * <p>
     * It will patiently wait for tasks of {@link Action} objects to conclude
     * indefinitely, retrying every {@link ManagedPromiseService#WAIT_TIMEOUT}.
     * Hence, it is important the threads are not made to run infinitely.
     */
    default void stop() {
        stop(WAIT_TIMEOUT,true);
    }

    /**
     * Calling this method starts the shutting down process of the
     * {@link ManagedPromiseService} thread pool.
     * <p>
     * Use this method to conclude the thread pool ahead of application shutdown.
     * Specify the timeout period and whether the pool should retry after
     * timeouts. If the {@code retry} is {@code false}, then potentially
     * after timeout some threads may still be live, these will be interrupted,
     * resulting in unkept promises.
     *
     * @param timeout value in milliseconds.
     * @param retry if {@code true} indefinitely attempts to terminate threads
     *             after shutdown (use with caution).
     * @throws IllegalArgumentException if timeout value &lt;
     *                  {@link ManagedPromiseService#MIN_WAIT_TIMEOUT}
     */
    void stop(final long timeout, final boolean retry);

    /**
     * When the JVM receives a SIGTERM signal, this method is called to shutdown
     * the {@link ManagedPromiseService} gracefully.
     * <p>
     * This method is considered idempotent. So if the {@link ManagedPromiseService}
     * is already in the process of shutting down additional requests are ignored.
     * <p>
     * This method does not require a state transition handler unlike
     * {@link ManagedPromiseService#signalTerm(Consumer)} overloaded method.
     * <p>
     * The thread pool patiently waits for outstanding promises to be fulfilled,
     * and it is for this reason, it is important the tasks must not run
     * indefinitely/infinitely.
     */
    default void signalTerm() {
        signalTerm(null);
    }

    /**
     * When the JVM receives a SIGTERM signal, this method is called to shutdown
     * the {@link ManagedPromiseService} gracefully.
     * <p>
     * This method is considered idempotent. So if the {@link ManagedPromiseService}
     * is already in the process of shutting down further requests are ignored,
     * but the {@code stateTransitionHandler} will be invoked, if available.
     * <p>
     * The thread pool patiently waits for outstanding promises to be fulfilled,
     * and it is for this reason, it is important the tasks must not run
     * indefinitely/infinitely.
     * <p>
     * {@code stateTransitionHandler} function offers opportunities for clients
     * to consume shutdown state transitions.
     *
     * @param stateTransitionHandler consumes shutdown states for processing
     */
    default void signalTerm(final Consumer<ServiceStates> stateTransitionHandler) {
        if (stateTransitionHandler != null)
            stateTransitionHandler.accept(getState());
        if (getState() != ServiceStates.ACTIVE) { // Must be already in the process of termination
            return;
        }
        try {
            stop();
        } finally {
            if (stateTransitionHandler != null)
                stateTransitionHandler.accept(getState());
        }
    }
}
