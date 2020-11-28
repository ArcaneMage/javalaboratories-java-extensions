package org.javalaboratories.core.concurrency;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Thread pools that implement this interface are considered managed in that
 * they have the ability to shutdown promise threads gracefully in the event
 * of a SIGTERM signal received by the JVM.
 * <p>
 * The contract of this interface is that the
 * {@link ManagedPromisePool#signalTerm()} must be invoked having received
 * SIGTERM signal. Following this, the managed pool will wait for outstanding
 * promises for {@link ManagedPromisePool#SHUTDOWN_WAIT_TIMEOUT} time before
 * re-attempting to terminate them -- this will continue until all promises
 * have concluded. It is for this reason that {@link Promise) objects
 * are carefully written such that they do not block the shutdown sequence
 * indefinitely.
 * <p>
 * Other shutdown strategies are currently being considered.
 */
public interface ManagedPromisePool extends Executor {
    enum ServiceStates {ACTIVE, CLOSING, INACTIVE}

    long SHUTDOWN_WAIT_TIMEOUT = 5L;

    /**
     * Returns the current state of this pool service.
     * <p>
     * Indicates whether the thread pool is in an {@link ServiceStates#ACTIVE}
     * state, the normal mode of operation where tasks of {@link Action} objects
     * are accepted for processing. Any other state results in task being
     * rejected and the thread pool actively in the process of shutting down.
     *
     * @return the current state of the {@link ManagedPromisePool}
     */
    ServiceStates getState();

    /**
     * Calling this method starts the shutting down process of the
     * {@link ManagedPromisePoolExecutor} thread pool.
     * <p>
     * It will patiently wait for tasks of {@link Action} objects to conclude
     * indefinitely, retrying every {@link ManagedPromisePool#SHUTDOWN_WAIT_TIMEOUT}.
     * Hence, it is important the threads are not made to run infinitely.
     */
    default void free() {
        free(SHUTDOWN_WAIT_TIMEOUT,true);
    }

    /**
     * Calling this method starts the shutting down process of the
     * {@link ManagedPromisePool} thread pool.
     * <p>
     * Use this method to conclude the thread pool ahead of application shutdown.
     * Specify the timeout period and whether the pool should retry after
     * timeouts. If the {@code retry} is {@code false}, then potentially
     * after timeout some threads may still be live, these will be interrupted,
     * resulting in unkept promises.
     *
     * @param timeout value in seconds.
     * @param retry if {@code true} indefinitely attempts to terminate threads
     *             after shutdown (use with caution).
     */
    void free(final long timeout, final boolean retry);

    /**
     * When the JVM receives a SIGTERM signal, this method is called to shutdown
     * the {@link ManagedPromisePool} gracefully.
     * <p>
     * If the {@link ManagedPromisePool} is already in the process of shutting
     * down additional requests are ignored.
     * <p>
     * This method does not require a state transition handler unlike
     * {@link ManagedPromisePool#signalTerm(Consumer)} overloaded method.
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
     * the {@link ManagedPromisePool} gracefully.
     * <p>
     * If the {@link ManagedPromisePool} is already in the process of shutting
     * down further requests are ignored, but the {@code stateTransitionHandler}
     * will be invoked, if available.
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
    default void signalTerm(Consumer<ServiceStates> stateTransitionHandler) {
        if ( stateTransitionHandler != null )
            stateTransitionHandler.accept(getState());
        if (getState() != ServiceStates.ACTIVE) { // Must be already in the process of termination
            return;
        }
        try {
            free();
        } finally {
            if (stateTransitionHandler != null )
                stateTransitionHandler.accept(getState());
        }
    }
}
