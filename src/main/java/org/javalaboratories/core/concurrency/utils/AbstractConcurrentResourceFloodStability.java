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
package org.javalaboratories.core.concurrency.utils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States.CLOSED;
import static org.javalaboratories.core.concurrency.utils.ResourceFloodStability.States.OPENED;

/**
 * This class manages a thread pool of {@code flood workers} who are tasked with
 * sending multiple requests to the {@link ResourceFloodStability.Target} {@code
 * resource(s)}.
 * <p>
 * The results are then returned, ({@link Runnable} types do not have the ability
 * to return values), to the client object. After calling the method
 * {@link AbstractConcurrentResourceFloodStability#flood()}, the default behaviour
 * of class is to immediately {@code flood} the {@link ResourceFloodStability.Target}
 * without any concerns for timing or synchronization of the start process -- in
 * other words there is no "starter pistol". This may be sufficient for many tests,
 * but other strategies should be considered to increase the chances of thread
 * interleaving and race conditions.
 * <p>
 * Post-flood, all allocated resources associated with the
 * {@link FloodExecutorService} are destroyed. However, if the
 * {@link AbstractConcurrentResourceFloodStability#open()} method is called but the
 * {@code flood} is unused for whatever reason, it is highly recommend to call
 * the {@link AbstractConcurrentResourceFloodStability#close()} method to clean up.
 *
 * @param <T> Type of value returned from the {@link ResourceFloodStability.Target}
 * {@code resource}
 * @see Floodgate
 * @see Torrent
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class AbstractConcurrentResourceFloodStability<T> extends AbstractResourceFloodStability<List<T>>
        implements ConcurrentResourceFloodStability<List<T>> {

    public static final long DEFAULT_TIMEOUT_MINUTES = 5L;

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentResourceFloodStability.class);

    private static final int MIN_THREADS = 1;
    private static final int MIN_ITERATIONS = 1;

    @Getter(AccessLevel.PROTECTED)
    private FloodExecutorService service;
    @EqualsAndHashCode.Include
    private final int threads;
    @EqualsAndHashCode.Include
    private final int iterations;

    private States state;

    @Getter(AccessLevel.NONE)
    private boolean internalService;
    @Getter(AccessLevel.NONE)
    private List<Future<T>> futures;

    /**
     * Constructs an instance of this {@link ConcurrentResourceFloodStability} object.
     * <p>
     * This constructor assumes the {@code tag} is unnecessary.
     *
     * @param clazz class of {@link ResourceFloodStability.Target} undergoing test.
     * @param threads number of active threads tasked with sending requests
     *               to {@code resource}
     * @param iterations number of request repetitions per request thread
     * @param <U> Type of class currently under test.
     * @throws IllegalArgumentException if {@code threads} or {@code iterations}
     * are negative.
     */
    public <U> AbstractConcurrentResourceFloodStability(final Class<U> clazz, final int threads, final int iterations) {
        this(clazz,null,threads,iterations);
    }

    /**
     * Constructs an instance of this {@link ConcurrentResourceFloodStability} object.
     * <p>
     * @param clazz class of {@link ResourceFloodStability.Target} undergoing test.
     * @param tag a meaningful name of the resource under test.
     * @param threads number of active threads tasked with sending requests
     *               to {@code resource}
     * @param iterations number of request repetitions per request thread
     * @param <U> Type of class currently under test.
     * @throws IllegalArgumentException if {@code threads} or {@code iterations}
     * are negative.
     */
    public <U> AbstractConcurrentResourceFloodStability(final Class<U> clazz, final String tag, final int threads,
                                                        final int iterations) {
        this(clazz,tag,threads,iterations,null);
    }

    /**
     * Constructs an instance of this {@link ConcurrentResourceFloodStability} object.
     * <p>
     * @param clazz class of {@link ResourceFloodStability.Target} undergoing test.
     * @param tag a meaningful name of the resource under test.
     * @param threads number of active threads tasked with sending requests
     *               to {@code resource}
     * @param iterations number of request repetitions per request thread
     * @param service alternative executor service. If null, then one will be
     *                automatically created.
     * @param <U> Type of class currently under test.
     * @throws IllegalArgumentException if {@code threads} or {@code iterations}
     * are negative.
     */
    public <U> AbstractConcurrentResourceFloodStability(final Class<U> clazz, final String tag, final int threads,
                                                        final int iterations, FloodExecutorService service) {
        super(clazz, tag);
        if (threads < MIN_THREADS || iterations < MIN_ITERATIONS)
            throw new IllegalArgumentException("Review constructor arguments");
        this.service = service;
        this.threads = threads;
        this.iterations = iterations;
        this.futures = null;
        this.internalService = false;
        this.state = CLOSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean open() {
        if (state == CLOSED) {
            this.service = createExecutor();
            Supplier<T> resource = primeResource();
            futures = primeThreads(resource);
            state = OPENED;
        } else {
            throw new IllegalStateException(String.format("State not closed, state=%s",state));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        close(false);
    }

    /**
     * Closes and releases all allocated resources pertaining to {@code flood
     * workers}.
     * <p>
     * If the {@code force} parameter is {@code true}, an attempt is made to
     * shutdown the internal pool of {@code flood workers}, and if there any
     * still processing, rather than waiting they will be shutdown immediately.
     * <p>
     * It's advisable to use the {@link AbstractConcurrentResourceFloodStability#close()}
     * and allow those objects to take the correct course of action. This method
     * is used by unit tests only.
     *
     * @param force {@code true} to force shutdown of {@code flood workers}.
     */
    void close(boolean force) {
        if (state == OPENED) {
            try {
                if (internalService) {
                    service.close(force);
                } else {
                    logger.info(message("Flood pool service is an external service -- not closing"));
                }
            } finally {
                state = CLOSED;
            }
        } else
            throw new IllegalStateException(String.format("State not open state=%s",state));
    }

    /**
     * {@inheritDoc}
     *
     * This implementation will wait up to
     * {@link AbstractConcurrentResourceFloodStability#DEFAULT_TIMEOUT_MINUTES},
     * currently 5 minutes before closing the {@code resource}.
     */
    @Override
    public final List<T> flood() {
        return flood(DEFAULT_TIMEOUT_MINUTES,TimeUnit.MINUTES);
    }

    /**
     * Floods the {@link ResourceFloodStability.Target} with requests and blocks
     * the current thread, waiting for the flood to complete.
     * <p>
     * Specify the {@code timeout} and its {@code unit} to inform
     * {@link ResourceFloodStability} how long to wait for flood completion. If
     * the {@code flood workers} fail to complete their work within the allotted
     * time the threads are signalled to terminate regardless of the outcome
     * of requests.
     * <p>
     * However, this method is dependent on the implementation of the
     * {@link AbstractConcurrentResourceFloodStability#await(long, TimeUnit)}
     * method; the default implementation is to {@code join} to the current
     * thread. For a more sophisticated mechanism, it is recommended to override
     * the {@code await} method.
     *
     * @param timeout maximum time to wait.
     * @param unit the unit of the timeout.
     * @return a list of values returned from each {@code thread} request, if
     * possible.
     */
    public final List<T> flood(final long timeout, final TimeUnit unit) {
        if (this.getState() != OPENED)
            throw new IllegalStateException(String.format("State not open, state=%s",state));
        TimeUnit u = Objects.requireNonNull(unit);
        List<T> result;
        try {
            superviseFlood();
            await(timeout,u);
        } catch (InterruptedException ignore) {
        } finally {
            close();
            result = finalise(futures);
            state = States.FLOODED;
            afterFlood();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("[target=%s,state=%s,flood-workers=%d,flood-iterations=%d]", getTarget(),
                getState(),getThreads(),getIterations());
    }

    /**
     * Override this method for additional behaviour after the flood.
     * <p>
     * Default implementation is to do nothing. This is a life-cycle method.
     */
    protected void afterFlood() {}

    /**
     * {@link AbstractConcurrentResourceFloodStability#flood(long, TimeUnit)} calls
     * this method during the {@code flood} process.
     * <p>
     * It is a blocking method, waiting until the maximum {@code timeout} for
     * the {@code flood workers} to complete.
     * <p>
     * The implementation is simply a {@code thread} join to the current thread.
     * It knows nothing about the state of the threads, so for something a
     * little sophisticated is required then override in derived classes.
     *
     * @param timeout maximum time to wait.
     * @param units the unit of the timeout.
     * @throws InterruptedException if current thread is interrupted.
     */
    protected void await(long timeout, TimeUnit units) throws InterruptedException {
        units.timedJoin(Thread.currentThread(),timeout);
    }

    /**
     * Performs and manages the flood mechanics required by this object.
     * <p>
     * Default implementation is to generate initialisation logging. Override in
     * derived classes for additional/alternative behaviour.
     */
    protected void superviseFlood() {
        logger.info(message("Flooding resource with {} flood workers, each iterating {} times"),getThreads(),
                getIterations());
    }

    /**
     * Returns {@code resource}, a {@link Supplier} object encapsulating the
     * request.
     * <p>
     * The purpose of this method is to make ready the request to be issued by
     * the {@code flood workers}. For example, performing request repetitions
     * within the worker thread. It is possible to override this method in
     * derived classes, but ensure a call to this method is made to maintain
     * the behaviour integrity of this class.
     * <p>
     * In other words, the {@code resource} returned from this method is is
     * decorated with several layers of encapsulation.
     *
     * @return a primed {@code resource} for processing.
     */
    protected Supplier<T> primeResource() {
        return () -> {
            T result = null;
            if (getTarget().getStability() == Target.Stability.STABLE) {
                int i = 0;
                try {
                    while (i++ < getIterations()) {
                        result = getResource().get();
                        Thread.yield();
                    }
                } catch (Throwable t) {
                    logger.error(message("Targeted resource raised an exception during flood"),t);
                    getTarget().unstable();
                }
            } else {
                logger.warn(message("Target state is unstable -- cannot flood"));
            }
            return result;
        };
    }

    /**
     * Retrieve underlying {@code resource} to be {@code targeted}.
     * <p>
     * {@link AbstractConcurrentResourceFloodStability#primeResource()} depends
     * on this method in order to {@code prime} the {@code resource} for the
     * {@code flood workers}.
     *
     * @return underlying {@code resource}
     */
    protected abstract Supplier<T> getResource();

    private FloodExecutorService createExecutor() {
        FloodExecutorService result;
        internalService = false;
        if (service == null) {
            result = new FloodThreadPoolExecutor(getTarget(),threads);
            internalService = true;
            logger.info(message("Flood pool service created successfully, number of flood workers {}"), threads);
        } else {
            result = service;
        }
        return result;
    }

    private List<Future<T>> primeThreads(final Supplier<T> resource) {
        List<Future<T>> result = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Future<T> f = service.submit(resource::get);
            result.add(f);
        }
        return result;
    }

    private List<T> finalise(List<Future<T>> futures) {
        List<T> result = new ArrayList<>();
        if (futures != null) {
            futures.forEach(f -> {
                try {
                    if (f.isDone()) {
                        result.add(f.get());
                    }
                } catch (InterruptedException | ExecutionException | CancellationException ignored) {
                    // This is okay: no need to report forced shutdown of
                    // threads or execution failures; these are already reported
                    // by the flood workers.
                }
            });
        }
        return result;
    }
}
