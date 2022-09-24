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

import org.javalaboratories.core.concurrency.utils.FloodWorker.FloodWorkerPriority;
import org.javalaboratories.core.concurrency.utils.ResourceFloodStability.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * An implementation of {@link FloodExecutorService}.
 * <p>
 * This implementation creates {@link FloodWorker} object for each a {@link
 * Callable} submitted task -- submission of {@link Runnable} objects is not
 * supported by this executor. {@link Floodgate} submits {@code resource}
 * objects as {@link Callable} objects. The following illustrates the composition
 * of the {@code Callable} object:
 * <pre>
 *     {@code
 *         Callable --> [Decorated] Supplier --> [Decorated] (Supplier | Runnable) --> [Target] (resource)
 *     }
 * </pre>
 * Essentially the underlying {@code target} under test has several decorated
 * layers (composition), and so there is no obligation on the {@code target} to
 * extend an object and/or implement an interface except it must call the method
 * of an object subjected to testing, the {@code resource}.
 * <p>
 * Use the {@link FloodExecutorService#close(boolean)} to release allocated
 * memory resources pertaining to the pool, the parameter {@code true} forces
 * unfinished {@link FloodWorker} to conclude its task. It is recommended to use
 * the {@link FloodExecutorService#close} and allow the pool to take the
 * appropriate action.
 *
 * @see FloodWorker
 * @see FloodExecutorService
 */
public class FloodThreadPoolExecutor extends ThreadPoolExecutor implements FloodExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(FloodExecutorService.class);

    private static final String FLOODGATE_GROUP_NAME = "Floodgate-Group";
    private static final String WORKER_THREAD_NAME="Floodgate-Worker-%d";
    private static final ThreadGroup FLOODGATE_GROUP = new ThreadGroup(FLOODGATE_GROUP_NAME);
    private static final AtomicInteger threadIndex = new AtomicInteger(0);
    private final static AtomicInteger roundRobinPriority = new AtomicInteger(0);

    private Target target;
    protected List<Future<?>> futures;

    /**
     * Creates an instance of this thread pool.
     *
     * @param target a representation of the {@code target} to be tested.
     * @param threads the number {@link FloodWorker} threads required in core pool.
     */
    public FloodThreadPoolExecutor(final Target target, final int threads) {
        this(target,threads,threads);
    }

    /**
     * Creates an instance of this thread pool.
     *
     * @param target a representation of the {@code target} to be tested.
     * @param corePoolSize the number {@link FloodWorker} threads required in
     *                     core pool.
     * @param maximumPoolSize maximum number of {@link FloodWorker} threads required in
     *                        pool.
     */
    public FloodThreadPoolExecutor(final Target target, final int corePoolSize, int maximumPoolSize) {
        super(corePoolSize,maximumPoolSize,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                FloodThreadPoolExecutor::newFloodWorkerThread);
        futures = new ArrayList<>();
        this.target = target;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Each {@link FloodWorker} is given a priority ranging from HIGHEST to
     * LOWEST. This is not based on the criteria or the nature of
     * the task but rather based on a round-robin algorithm. In others words,
     * each task will receive the next subsequent priority, and if the priority
     * LOWEST is reached, then the HIGHEST is then given followed by HIGH, etc.
     *
     * @param callable submitted task to execute.
     * @param <T> type of value returned from task.
     * @return a new instance of {@link FloodWorker} object.
     * @see FloodWorker
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        int level = roundRobinPriority.getAndIncrement() % FloodWorkerPriority.values().length;
        return new FloodWorker<>(callable, FloodWorkerPriority.toPriority(level));
    }

    /**
     * Sets the current {@code target} under test.
     * @param target the underlying {@code target} under test.
     */
    public void setTarget(final Target target) {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This thread pool does not support {@link Runnable} objects.
     * @throws UnsupportedOperationException is the default implementation.
     */
    public Future<?> submit(Runnable task) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This thread pool does not support {@link Runnable} objects.
     * @throws UnsupportedOperationException is the default implementation.
     */
    public <T> Future<T> submit(Runnable task, T result) {
        throw new UnsupportedOperationException();
    }

    public <T> Future<T> submit(Callable<T> callable) {
        RunnableFuture<T> ftask = newTaskFor(callable);
        futures.add(ftask);
        execute(ftask);
        return ftask;
    }

    /**
     * Releases allocated memory pertaining to the pool.
     * <p>
     * Use the {@link FloodExecutorService#close(boolean)} to release allocated
     * memory resources pertaining to the pool, the parameter {@code true} forces
     * unfinished {@link FloodWorker} to conclude its task. It is recommended to use
     * the {@link FloodExecutorService#close} and allow the pool to take the
     * appropriate action.
     *
     * @param force {@code true} to instruct {@link FloodWorker} objects to stop
     *              working; {@code false} to wait for natural termination, if
     *              possible.
     */
    public void close(boolean force) {
        final Consumer<Future<?>> cancel = f -> {if (!f.isDone()) f.cancel(false);};
        try {
            if (!force) {
                shutdown();
                logger.info(target.getName()+": Shutting down flood pool service, but first waiting {} seconds for flood workers to " +
                        "complete their work",SHUTDOWN_TIMEOUT_SECONDS);
                awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (!isTerminated()) {
                    shutdownNow();
                    logger.error(target.getName()+": Flood workers still active, but SHUTDOWN_TIMEOUT {} seconds exceeded -- " +
                            "forcing shutdown",SHUTDOWN_TIMEOUT_SECONDS);
                    futures.forEach(cancel);
                }
            } else {
                logger.error(target.getName()+": Not waiting for flood workers, forcing immediate shutdown");
                shutdownNow();
                futures.forEach(cancel);
            }
        } catch (InterruptedException e) {
            logger.error(target.getName()+": Termination of worker threads interrupted");
        } finally {
            logger.info(target.getName()+": Flood pool service shutdown successfully");
        }
    }

    private static Thread newFloodWorkerThread(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,threadIndex.incrementAndGet());
        Thread result = new Thread(FLOODGATE_GROUP,runnable);
        result.setName(name);
        return result;
    }
}