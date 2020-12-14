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

@Getter
public abstract class AbstractConcurrentResourceFloodTester<T> extends AbstractResourceFloodTester<List<T>> implements ConcurrentResourceFloodTester<List<T>> {

    public static final long FLOOD_WAIT_TIMEOUT_MINUTES = 5L;

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

    public <U> AbstractConcurrentResourceFloodTester(final Class<U> clazz, final int threads, final int iterations) {
        super(clazz);
        if (threads < MIN_THREADS || iterations < MIN_ITERATIONS)
            throw new IllegalArgumentException("Review constructor arguments");
        this.threads = threads;
        this.iterations = iterations;
        this.futures = null;
        this.state = States.CLOSED;
    }

    @Override
    public boolean open() {
        if (state == States.CLOSED) {
            this.service = createExecutor();
            Supplier<T> resource = primeResource();
            futures = primeThreads(resource);
            state = States.OPENED;
        } else {
            throw new IllegalStateException(String.format("State not closed, state=%s",state));
        }
        return true;
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public final List<T> flood() {
        return flood(FLOOD_WAIT_TIMEOUT_MINUTES,TimeUnit.MINUTES);
    }

    public final List<T> flood(final long timeout, final TimeUnit units) {
        if (this.getState() != States.OPENED)
            throw new IllegalStateException(String.format("State not open, state=%s",state));
        TimeUnit u = Objects.requireNonNull(units);
        List<T> result;
        try {
            floodResource(timeout, u);
        } catch (InterruptedException ignore) {
        } finally {
            close();
            result = finalise(futures);
            state = States.FLOODED;
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("[target=%s,state=%s,flood-workers=%d,flood-iterations=%d]", getTarget(),
                getState(),getThreads(),getIterations());
    }

    protected abstract void floodResource(final long timeout, final TimeUnit units) throws InterruptedException;

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

    interface FloodExecutorService extends ExecutorService {
        int getActiveCount();
    }

    private static class FloodThreadPoolExecutor extends ThreadPoolExecutor implements FloodExecutorService {
        public FloodThreadPoolExecutor(int threads, ThreadFactory threadFactory) {
            super(threads,threads,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(),threadFactory);
        }
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
}
