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
package org.javalaboratories.core.concurrency.test;

import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * This class manages a thread pool of {@code flood workers} who are tasked with
 * sending multiple requests to the {@link Target's} {@code resource(s)}.
 * <p>
 * The results are then returned, ({@link Runnable} types do not have the ability
 * to return values), to the client object. After calling the method
 * {@link AbstractConcurrentResourceFloodTester#flood()}, the default behaviour of
 * class is to immediately {@code flood} the {@link Target} without any concerns
 * for timing or synchronization the start process -- in other words there is no
 * "starter pistol". This may be sufficient for many tests, but other
 * strategies should be considered to increase the chances of thread interleaving
 * and race conditions.
 * <p>
 * Post-flood, all allocated resources associated with the
 * {@link FloodExecutorService} are destroyed. However, if the
 * {@link AbstractConcurrentResourceFloodTester#open()} method is called but the
 * {@code flood} is unused for whatever reason, it is highly recommend to call
 * the {@link AbstractConcurrentResourceFloodTester#close()} method to clean up.
 *
 * @param <T> Type of value returned from the {@link Target's} {@code resource}
 */
@Getter
public abstract class AbstractConcurrentResourceFloodTester<T> extends AbstractResourceFloodTester<List<T>> implements ConcurrentResourceFloodTester<List<T>> {

    public static final long DEFAULT_TIMEOUT_MINUTES = 5L;

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentResourceFloodTester.class);

    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;
    private static final String FLOODGATE_GROUP_NAME = "Floodgate-Group";
    private static final String WORKER_THREAD_NAME="Floodgate-Worker-%d";
    private static final ThreadGroup FLOODGATE_GROUP = new ThreadGroup(FLOODGATE_GROUP_NAME);
    private static final AtomicInteger threadIndex = new AtomicInteger(0);

    private static final int MIN_THREADS = 1;
    private static final int MIN_ITERATIONS = 1;

    @Getter(AccessLevel.PROTECTED)
    private FloodExecutorService service;

    private final int threads;
    private final int iterations;

    private States state;

    @Getter(AccessLevel.NONE)
    private List<Future<T>> futures;

    /**
     * Constructs an instance of this {@link ConcurrentResourceFloodTester} object.
     * <p>
     * @param clazz class of {@link Target} undergoing test.
     * @param threads number of active threads tasked with sending requests
     *               to {@code resource}
     * @param iterations number of request repetitions per request thread
     * @param <U> Type of class currently under test.
     * @throws IllegalArgumentException if {@code threads} or {@code iterations}
     * are negative.
     */
    public <U> AbstractConcurrentResourceFloodTester(final Class<U> clazz, final int threads, final int iterations) {
        super(clazz);
        if (threads < MIN_THREADS || iterations < MIN_ITERATIONS)
            throw new IllegalArgumentException("Review constructor arguments");
        this.threads = threads;
        this.iterations = iterations;
        this.futures = null;
        this.state = States.CLOSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean open() {
        if (state == States.CLOSED) {
            this.service = createExecutor();
            state = States.OPENED;
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
     * {@inheritDoc}
     *
     * This implementation will wait up to
     * {@link AbstractConcurrentResourceFloodTester#DEFAULT_TIMEOUT_MINUTES},
     * currently 5 minutes before closing the {@code resource}.
     */
    @Override
    public final List<T> flood() {
        return flood(DEFAULT_TIMEOUT_MINUTES,TimeUnit.MINUTES);
    }

    /**
     * Floods the {@link Target} with requests and blocks the current thread,
     * waiting for the flood to complete.
     * <p>
     * Specify the {@code timeout} and its {@code unit} to inform
     * {@link ResourceFloodTester} how long to wait for flood completion. If
     * the {@code flood workers} fail to complete their work within the allotted
     * time the threads are signalled to terminate regardless of the outcome
     * of requests.
     * <p>
     * However, this method is dependent on the implementation of the
     * {@link AbstractConcurrentResourceFloodTester#await(long, TimeUnit)}
     * method; the default implementation is to {@code join} to the current
     * thread. For a more sophisticated mechanism, it is recommended to override
     * the {@code await} method.
     *
     * @param timeout maximum time to wait
     * @param unit the unit of the timeout.
     * @return a list of values returned from each {@code thread} request,
     * if possible.
     */
    public final List<T> flood(final long timeout, final TimeUnit unit) {
        if (this.getState() != States.OPENED)
            throw new IllegalStateException(String.format("State not open, state=%s",state));
        TimeUnit u = Objects.requireNonNull(unit);
        List<T> result;
        try {
            Supplier<T> resource = primeResource();
            futures = primeThreads(resource);
            logger.info("{}: Flooding resource with {} flood workers, each iterating {} times",getTarget().getName(),
                    getThreads(),getIterations());
            if (getService().getActiveCount() < getThreads() )
                logger.warn("{}: Active thread count {}. Not all flood workers are ready",getTarget().getName(),
                        getService().getActiveCount());
            await(timeout,u);
        } catch (InterruptedException ignore) {
        } finally {
            close();
            result = finalise(futures);
            state = States.FLOODED;
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
     * {@link AbstractConcurrentResourceFloodTester#flood(long, TimeUnit)} calls
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
     * Returns {@code resource}, a {@link Supplier} object encapsulating the
     * request.
     * <p>
     * The purpose of this method is to make ready the request to be issued by
     * the {@code flood workers}. For example, the "original" {@code resource}
     * may be in fact be a {@link Runnable}, but this class expects a
     * {@link Supplier} object, and so the role of this method is to transform
     * the original {@code resource} into an acceptable form, ready for the
     * {@code flood workers}. In other words, the {@code resource} returned
     * from this method may be decorated with several layers of encapsulation.
     *
     * @return a primed {@code resource} for processing.
     */
    protected abstract Supplier<T> primeResource();

    /**
     * Closes and releases all allocated resources pertaining to {@code flood
     * workers}.
     * <p>
     * If the {@code force} parameter is {@code true}, an attempt is made to
     * shutdown the internal pool of {@code flood workers}, and if there any
     * still processing, rather than waiting they will be shutdown immediately.
     * <p>
     * It's advisable to use the {@link Floodgate#close()} and allow this
     * object to take the correct course of action. This method is provided
     * for unit tests purposes, hence {@code default} access-level -- do not
     * alter this.
     *
     * @param force {@code true} to force shutdown of {@code flood workers}.
     */
    void close(boolean force) {
        if (state == States.OPENED) {
            try {
                if (!force) {
                    service.shutdown();
                    logger.info("{}: Shutting down flood pool service, but first waiting {} seconds for flood workers to " +
                            "complete their work",getTarget().getName(),SHUTDOWN_TIMEOUT_SECONDS);
                    service.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (!service.isTerminated()) {
                        service.shutdownNow();
                        logger.error("{}: Flood workers still active, but SHUTDOWN_TIMEOUT {} seconds exceeded -- " +
                                "forcing shutdown", getTarget().getName(), SHUTDOWN_TIMEOUT_SECONDS);
                    }
                } else {
                    logger.error("{}: Not waiting for flood workers, forcing immediate shutdown", getTarget().getName());
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("{} Termination of worker threads interrupted", getTarget().getName());
            } finally {
                logger.info("{}: Flood pool service shutdown successfully", getTarget().getName());
                state = States.CLOSED;
            }
        }
    }

    private static Thread newFloodWorker(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,threadIndex.incrementAndGet());
        Thread result = new Thread(FLOODGATE_GROUP,runnable);
        result.setName(name);
        return result;
    }

    private FloodExecutorService createExecutor() {
        FloodExecutorService result = new FloodThreadPoolExecutor(threads,AbstractConcurrentResourceFloodTester::newFloodWorker);
        logger.info("{}: Flood pool service created successfully, number of flood workers {}",getTarget().getName(),threads);
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
                    result.add(f.isDone() ? f.get() : null);
                } catch (InterruptedException | ExecutionException | CancellationException ignored) {
                    // This is okay: no need to report forced shutdown of
                    // threads or execution failures; these are already reported
                    // by the flood workers.
                }
            });
        }
        return result;
    }

    /**
     * Representation of the {@code flood worker} pool.
     * <p>
     * Publishes useful methods from the underlying implementation.
     */
    public interface FloodExecutorService extends ExecutorService {
        int getActiveCount();
    }

    /**
     * Customised {@link ThreadPoolExecutor} to manage {@code flood workers}
     */
    private static class FloodThreadPoolExecutor extends ThreadPoolExecutor implements FloodExecutorService {
        /**
         * Constructs this {@link FloodThreadPoolExecutor} object.
         *
         * @param threads number of threads required in pool
         * @param threadFactory customised thread factory.
         */
        public FloodThreadPoolExecutor(int threads, ThreadFactory threadFactory) {
            super(threads,threads,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(),threadFactory);
        }
    }
}
