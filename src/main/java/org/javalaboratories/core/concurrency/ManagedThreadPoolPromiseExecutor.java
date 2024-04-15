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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code ManagedPromisePoolExecutor} is a custom thread pool executor designed
 * for use with {@link Promise} objects.
 * <p>
 * If required, but rarely necessary, it is possible to provide an alternative
 * thread pool. Achieving this involves configuring the
 * "{@code promise-configuration.properties}" file, but it is required that the
 * thread pool must inherit from {@link AbstractManagedPromiseService} class.
 * <p>
 * When the JVM is signalled to shut down, whether via SIGTERM or through natural
 * program termination, the thread pool will wait for any outstanding running
 * {@code Promise} threads to terminate before concluding. Therefore, it is
 * important that {@code Promise} objects reach to a natural conclusion. It is not
 * advisable for threads to run infinitely. If this is a possibility then it
 * would to be prudent to force shutdown the pool service with the
 * {@link ManagedThreadPoolPromiseExecutor#stop(long, boolean)} specifying a timeout
 * without retries ahead of program termination.
 * <p>
 * Currently, various strategies are under consideration to improve shutdown
 * behaviour.
 *
 * @see ManagedThreadPerTaskPromiseExecutor
 * @see ManagedPromiseService
 */
public class ManagedThreadPoolPromiseExecutor extends AbstractManagedPromiseService {

    protected static final String PROMISES_THREAD_GROUP = "Promises-Group";

    private static final Logger logger = LoggerFactory.getLogger(ManagedThreadPoolPromiseExecutor.class);

    private static final AtomicInteger workerIndex = new AtomicInteger(0);
    private static final String WORKER_THREAD_NAME="Promise-Worker-%d";
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(PROMISES_THREAD_GROUP);

    private final ThreadPoolExecutor delegate;

    /**
     * Constructs an instance of this thread pool.
     * <p>
     * Constructor called from the {@link ManagedPromiseServiceFactory}, if
     * configured to create an instance of this object. Automatic shutdown
     * management is enabled by default.
     *
     * @param capacity Number maximum thread workers to carryout promises.
     */
    public ManagedThreadPoolPromiseExecutor(final int capacity) {
        this(capacity,true);
    }

    /**
     * Constructs an instance of this thread pool with optional automatic
     * shutdown management.
     * <p>
     * Constructor is package level access only for unit testing purposes. It
     * is recommended to use
     * {@link ManagedThreadPoolPromiseExecutor#ManagedThreadPoolPromiseExecutor(int)}
     * or the {@link ManagedPromiseServiceFactory} to create an instance of this
     * thread pool.
     *
     * @param capacity Number maximum thread workers to carryout promises.
     * @param autoShutdown {@code true} manage automatic shutdown when VM
     *                                 receives SIGTERM.
     */
    ManagedThreadPoolPromiseExecutor(final int capacity, final boolean autoShutdown) {
        super(capacity,autoShutdown);
        delegate = new ThreadPoolExecutor(capacity, capacity, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(),
                ManagedThreadPoolPromiseExecutor::newPromiseWorker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminate(long timeout, boolean retry) throws InterruptedException {
        int i = 0;
        delegate.shutdown();
        while (!delegate.awaitTermination(timeout, TimeUnit.MILLISECONDS) && retry) {
            logger.info("Awaiting termination of some promises -- elapsed {} seconds", (++i * timeout) / 1000.0);
        }
        if (!delegate.isTerminated()) {
            delegate.shutdownNow();
            logger.info("Not all promises kept following shutdown -- forced shutdown");
        }
    }

    private static Thread newPromiseWorker(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,workerIndex.incrementAndGet());
        Thread result = new Thread(THREAD_GROUP,runnable);
        result.setName(name);
        return result;
    }
}
