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

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code ManagedVirtualPromiseExecutor} is a custom thread "pool" executor
 * designed for use with {@link Promise} objects running in virtual threads.
 * <p>
 * An internal cache of the {@code executor} is not maintained as the
 * "pool" reference seems to suggest, but rather a virtual thread per
 * task is created on demand. The {@code capacity} can be huge and
 * therefore scalable. The capacity is really used to control and protect
 * backend services, such as database connections or file handles.
 * <p>
 * If required, it is possible to provide an alternative thread pool. Achieving
 * this involves configuring the "{@code promise-configuration.properties}" file,
 * but it is required that the thread pool must inherit from the
 * {@link AbstractManagedPromiseService} class.
 * <p>
 * When the JVM is signalled to shut down, whether via SIGTERM or through natural
 * program termination, the thread pool will wait for any outstanding running
 * {@code Promise} virtual threads to terminate before concluding. Therefore, it is
 * important that {@code Promise} objects reach to a natural conclusion. It is not
 * advisable for virtual threads to run infinitely. If this is a possibility then
 * it would to be prudent to to force shutdown the pool service with the
 * {@link ManagedVirtualPromiseExecutor#stop(long, boolean)} specifying a timeout
 * without retries ahead of program termination.
 * <p>
 * Currently, various strategies are under consideration to improve shutdown
 * behaviour.
 *
 * @see ManagedPromisePoolExecutor
 * @see ManagedPromiseService
 */
public class ManagedVirtualPromiseExecutor extends AbstractManagedPromiseService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedVirtualPromiseExecutor.class);

    public static final int DEFAULT_VIRTUAL_THREADS = 2048;
    private static final AtomicInteger workerIndex = new AtomicInteger(0);
    private static final String WORKER_THREAD_NAME="Promise-Virtual-Worker-%d";

    private final ExecutorService delegate;
    private final Semaphore semaphore;

    /**
     * Constructs an instance of this virtual thread "pool".
     * <p>
     * An internal cache of the {@code executor} is not maintained as the
     * "pool" reference seems to suggest, but rather a virtual thread per
     * task is created on demand. The {@code capacity} can be huge and
     * therefore scalable. The capacity is really used to control and protect
     * backend services, such as database connections or file handles.
     * <p>
     * Constructor called from the {@link PromisePoolServiceFactory}, if
     * configured to create an instance of this object. Automatic shutdown
     * management is enabled by default.
     *
     * @param capacity Number of maximum virtual thread workers to carryout
     *                 promises.
     */
    public ManagedVirtualPromiseExecutor(final int capacity) {
        this(capacity,true);
    }

    /**
     * Constructs an instance of this virtual thread "pool".
     * <p>
     * An internal cache of the {@code executor} is not maintained as the
     * "pool" reference seems to suggest, but rather a virtual thread per
     * task is created on demand. The {@code capacity} can be huge and
     * therefore scalable. The capacity is really used to control and protect
     * backend services, such as database connections or file handles.
     * <p>
     * Constructor called from the {@link PromisePoolServiceFactory}, if
     * configured to create an instance of this object. Automatic shutdown
     * management is enabled by default.
     *
     * @param capacity Number of maximum virtual thread workers to carryout
     *                promises.
     * @param autoShutdown {@code true} manage automatic shutdown when VM
     *                                 receives SIGTERM.
     */
    ManagedVirtualPromiseExecutor(final int capacity, final boolean autoShutdown) {
        super(autoShutdown);
        delegate = Executors.newThreadPerTaskExecutor(ManagedVirtualPromiseExecutor::newVirtualPromiseWorker);
        semaphore = new Semaphore(capacity < 1 ? DEFAULT_VIRTUAL_THREADS : capacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminate(long timeout, boolean retry) throws InterruptedException {
        int i = 0;
        delegate.shutdown();
        while (!delegate.awaitTermination(timeout, TimeUnit.MILLISECONDS) && retry) {
            logger.info("Awaiting termination of some virtual promises  -- elapsed {} seconds", (++i * timeout) / 1000.0);
        }
        if (!delegate.isTerminated()) {
            delegate.shutdownNow();
            logger.info("Not all virtual promises kept following shutdown -- forced shutdown");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Runnable command) {
        Runnable r = Objects.requireNonNull(command);
        delegate.execute(() -> {
            try {
                semaphore.acquire();
                r.run();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                semaphore.release();
            }
        });
    }

    private static Thread newVirtualPromiseWorker(final Runnable command) {
        Thread.Builder.OfVirtual ofVirtual = Thread.ofVirtual()
                .name(String.format(WORKER_THREAD_NAME,workerIndex.incrementAndGet()));
        return ofVirtual.unstarted(command);
    }
}
