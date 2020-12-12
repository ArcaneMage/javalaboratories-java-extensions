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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Getter
public class Floodgate<T> extends AbstractResourceFloodTester<List<T>> {

    public static final long FLOOD_WAIT_TIMEOUT_MINUTES = 5L;
    public static final int DEFAULT_FLOOD_WORKERS = 5;
    public static final int DEFAULT_FLOOD_ITERATIONS = 5;

    private static final Logger logger = LoggerFactory.getLogger(Floodgate.class);

    private static final String FLOODGATE_GROUP_NAME = "Floodgate-Group";
    private static final String WORKER_THREAD_NAME="Floodgate-Worker-%d";
    private static final ThreadGroup FLOODGATE_GROUP = new ThreadGroup(FLOODGATE_GROUP_NAME);
    private static final AtomicInteger threadIndex = new AtomicInteger(0);

    private static final int MIN_THREADS = 1;
    private static final int MIN_ITERATIONS = 1;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10L;

    @Getter(AccessLevel.NONE)
    private final FloodController floodManagement;

    @Getter(AccessLevel.NONE)
    private final CountDownLatch workLatch;

    private final Supplier<T> resource;
    private final int threads;
    private final int iterations;

    private States state;

    @Getter(AccessLevel.NONE)
    private FloodExecutorService service;
    @Getter(AccessLevel.NONE)
    private List<Future<T>> futures;

    public <U> Floodgate(final Class<U> clazz, final Runnable resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    public <U> Floodgate(final Class<U> clazz, final Supplier<T> resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Runnable resource) {
        this(clazz,threads,iterations,() -> {resource.run(); return null;});
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> resource) {
        this(clazz,threads,iterations,resource,getController());
    }

    <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> resource, final FloodController controller) {
        super(clazz);
        if (threads < MIN_THREADS || iterations < MIN_ITERATIONS || resource == null || controller == null)
            throw new IllegalArgumentException("Review floodgate constructor arguments");
        this.resource = resource;
        this.threads = threads;
        this.iterations = iterations;
        this.floodManagement = controller;
        this.futures = null;
        this.workLatch = new CountDownLatch(this.threads);
        this.state = States.CLOSED;
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public boolean open() {
        if (state == States.CLOSED) {
            this.service = createExecutor();
            Supplier<T> resource = prepare(this.resource);
            futures = prime(resource);
            state = States.OPENED;
        } else {
            throw new IllegalStateException(String.format("Floodgate not closed, state=%s",state));
        }
        return true;
    }

    @Override
    public List<T> flood() {
        return flood(FLOOD_WAIT_TIMEOUT_MINUTES,TimeUnit.MINUTES);
    }

    public List<T> flood(final long timeout, final TimeUnit units) {
        if (state != States.OPENED)
            throw new IllegalStateException(String.format("Floodgate not open, state=%s",state));
        TimeUnit u = Objects.requireNonNull(units);

        List<T> result;
        try {
            logger.info("{}: Flooding resource with {} flood workers, each iterating {} times",getTarget().getName(),
                    threads,iterations);
            if (service.getActiveCount() < threads)
                logger.warn("{}: Active thread count {}. Not all flood workers are ready",getTarget().getName(),
                        service.getActiveCount());
            if (!(floodManagement instanceof ExternalFloodController)) {
                floodManagement.flood();
            } else {
                logger.info("{}: Flood controller externally managed -- deferred management", getTarget().getName());
            }
            if (!workLatch.await(timeout, u))
                logger.error("{}: Insufficient wait timeout specified, not all flood workers have completed their work",
                        getTarget().getName());

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
        String controller = floodManagement instanceof ExternalFloodController ? "External" : "Internal";
        return String.format("[name=%s,state=%s,flood-workers=%d,flood-iterations=%d,flood-controller=%s]", resource,state,
                threads,iterations,controller);
    }

    protected Supplier<T> prepare(final Supplier<T> resource) {
        Supplier<T> t = Objects.requireNonNull(resource);
        return () -> {
            T result = null;
            try {
                floodManagement.halt();
                int i = 0;
                while (i++ < iterations) {
                    result = t.get();
                }
                logger.info("{}: Finished flooding resource object successfully", this.getTarget().getName());
            } catch (InterruptedException e) {
                logger.info("{}: Finished flooding resource object but with interruption", this.getTarget().getName());
            } catch (Throwable throwable) {
                logger.error("{}: Flood resource raised an exception during flood", this.getTarget().getName(),throwable);
            } finally {
                workLatch.countDown();
            }
            return result;
        };
    }

    protected List<Future<T>> prime(final Supplier<T> resource) {
        List<Future<T>> result = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Future<T> f = service.submit(resource::get);
            result.add(f);
        }
        return result;
    }

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
                    service.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (!service.isTerminated()) {
                        service.shutdownNow();
                        logger.error("{}: Flood workers still active, but SHUTDOWN_TIMEOUT {} seconds exceeded -- forcing shutdown",
                                getTarget().getName(), SHUTDOWN_TIMEOUT_SECONDS);
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

    private static FloodController getController() {
        return new FloodController() {
            final CountDownLatch latch = new CountDownLatch(1);
            @Override
            public void halt() throws InterruptedException {
                latch.await();
            }
            @Override
            public void flood() {
                latch.countDown();
            }
        };
    }

    private List<T> finalise(List<Future<T>> futures) {
        List<T> result = new ArrayList<>();
        if (futures != null) {
            futures.forEach(f -> {
                try {
                    result.add(f.get());
                } catch (InterruptedException | ExecutionException | CancellationException ignored) {
                    // This is okay: no need to report forced shutdown of threads; execution failures
                    // are already reported by flood workers.
                }
            });
        }
        return result;
    }

    private FloodExecutorService createExecutor() {
        FloodExecutorService result = new FloodThreadPoolExecutor(threads,Floodgate::newFloodWorker);
        logger.info("{}: Flood pool service created successfully, number of flood workers {}",getTarget().getName(),threads);
        return result;
    }

    interface FloodExecutorService extends ExecutorService {
        int getActiveCount();
    }

    private static class FloodThreadPoolExecutor extends ThreadPoolExecutor implements FloodExecutorService {
        public FloodThreadPoolExecutor(int threads,ThreadFactory threadFactory) {
            super(threads,threads,0L,TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(),threadFactory);
        }
    }

}
