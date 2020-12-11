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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Floodgate<T> implements MultithreadedFloodTester<List<T>> {

    public static final long FLOOD_WAIT_TIMEOUT_MINUTES = 5L;
    public static final int DEFAULT_FLOOD_WORKERS = 2;
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

    @EqualsAndHashCode.Include
    private final String name;

    private final Supplier<T> target;
    private final int threads;
    private final int iterations;

    private States state;

    @Getter(AccessLevel.NONE)
    private CountDownLatch workLatch;
    @Getter(AccessLevel.NONE)
    private ExecutorService service;
    @Getter(AccessLevel.NONE)
    private List<Future<T>> futures;

    public <U> Floodgate(final Class<U> clazz, final Runnable target) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,target);
    }

    public <U> Floodgate(final Class<U> clazz, final Supplier<T> target) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,target);
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Runnable target) {
        this(clazz,threads,iterations,() -> {target.run(); return null;});
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> target) {
        this(clazz,threads,iterations,target,getController());
    }

    <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> target, final FloodController controller) {
        if (clazz == null || threads < MIN_THREADS || iterations < MIN_ITERATIONS || target == null || controller == null)
            throw new IllegalArgumentException("Review floodgate constructor arguments");

        this.name = String.format("{%s-%s}",clazz.getSimpleName(), UUID.randomUUID());
        this.threads = threads;
        this.iterations = iterations;
        this.target = target;
        this.floodManagement = controller;
        this.futures = null;
        this.state = States.CLOSED;
    }

    public boolean open() {
        if (state == States.CLOSED) {
            this.service = createExecutor();
            Supplier<T> target = prepare(this.target);
            futures = prime(target);
            state = States.OPENED;
        } else {
            throw new IllegalStateException(String.format("Floodgate not closed, state=%s",state));
        }
        return true;
    }

    public List<T> flood() {
        return flood(FLOOD_WAIT_TIMEOUT_MINUTES,TimeUnit.MINUTES);
    }

    public List<T> flood(final long timeout, final TimeUnit units) {
        if (state != States.OPENED)
            throw new IllegalStateException(String.format("Floodgate not open, state=%s",state));
        TimeUnit u = Objects.requireNonNull(units);

        List<T> result;
        try {
            int i = 0;
            boolean adequateTime = true;
            while (i < iterations && adequateTime) {
                reset();
                logger.info("{} - Flooding target with {} flood workers, iteration ({})",name,threads,++i);
                if (getActiveCount() < threads)
                    logger.warn("{} - Active thread count {}. Not all flood workers are ready",name,getActiveCount());
                if (!(floodManagement instanceof ExternalFloodController)) {
                    floodManagement.flood();
                } else {
                    logger.info("{} - Flood controller externally managed -- deferred management",name);
                }
                if ((adequateTime = workLatch.await(timeout,u)))
                    logger.error("{} - Insufficient wait timeout specified, not all flood workers have completed their work",name);
            }
        } catch (InterruptedException ignore) {
        } finally {
            shutdown();
            result = finalise(futures);
            state = States.FLOODED;
        }
        return result;
    }

    protected Supplier<T> prepare(final Supplier<T> target) {
        Supplier<T> t = Objects.requireNonNull(target);
        return () -> {
            T result = null;
            try {
                floodManagement.halt();
                result = t.get();
            } catch (Throwable throwable) {
                logger.error("{} - Exception thrown from within target during flood",name,throwable);
            } finally {
                workLatch.countDown();
            }
            return result;
        };
    }

    protected List<Future<T>> prime(final Supplier<T> target) {
        List<Future<T>> result = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Future<T> f = service.submit(target::get);
            result.add(f);
        }
        return result;
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
            public void halt() {
                try {
                    latch.await();
                } catch (InterruptedException ignore) {}
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
                    // This okay: no need to report forced shutdown of threads; execution failures
                    // are already reported by flood workers.
                }
            });
        }
        return result;
    }

    private int getActiveCount() {
        // TODO: Should introduce custom executor implementation for this -- thinking about it
        int result = 0;
        if (service instanceof ThreadPoolExecutor) {
            result = ((ThreadPoolExecutor) service).getActiveCount();
        } else {
            logger.warn("{} - This implementation of executor service does not support Active Count",name);
        }
        return result;
    }

    private void shutdown() {
        service.shutdown();
        try {
            service.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!service.isTerminated()) {
                service.shutdownNow();
                logger.error("{} -Worker threads still active, but SHUTDOWN_TIMEOUT {} exceeded -- forced shutdown",
                        name,SHUTDOWN_TIMEOUT_SECONDS);
            }
        } catch (InterruptedException e) {
            logger.error("{} Termination of worker threads interrupted",name);
        } finally {
            logger.info("{} - Flood pool shutdown successfully",name);
        }
    }

    private ExecutorService createExecutor() {
        ExecutorService service;
        service = Executors.unconfigurableExecutorService(
                Executors.newFixedThreadPool(threads,Floodgate::newFloodWorker)
        );
        logger.info("{} - Flood pool created successfully, number of flood workers {}",name,threads);
        return service;
    }

    private void reset() {
        workLatch = new CountDownLatch(threads);
    }
}
