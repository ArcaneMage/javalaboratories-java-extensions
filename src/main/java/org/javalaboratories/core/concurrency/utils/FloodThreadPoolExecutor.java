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

public class FloodThreadPoolExecutor extends ThreadPoolExecutor implements FloodExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(FloodExecutorService.class);

    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private static final String FLOODGATE_GROUP_NAME = "Floodgate-Group";
    private static final String WORKER_THREAD_NAME="Floodgate-Worker-%d";
    private static final ThreadGroup FLOODGATE_GROUP = new ThreadGroup(FLOODGATE_GROUP_NAME);
    private static final AtomicInteger threadIndex = new AtomicInteger(0);

    private Target target;
    protected List<Future<?>> futures;

    private static Thread newFloodWorkerThread(final Runnable runnable) {
        String name = String.format(WORKER_THREAD_NAME,threadIndex.incrementAndGet());
        Thread result = new Thread(FLOODGATE_GROUP,runnable);
        result.setName(name);
        return result;
    }

    private final static AtomicInteger roundRobinPriority = new AtomicInteger(0);

    public FloodThreadPoolExecutor(final Target target, final int threads) {
        this(target,threads,threads);
    }

    public FloodThreadPoolExecutor(final Target target, final int corePoolSize, int maximumPoolSize) {
        super(corePoolSize,maximumPoolSize,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                FloodThreadPoolExecutor::newFloodWorkerThread);
        futures = new ArrayList<>();
        this.target = target;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        int level = roundRobinPriority.getAndIncrement() % FloodWorkerPriority.values().length;
        return new FloodWorker<>(callable, FloodWorkerPriority.toPriority(level));
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public <T> Future<T> submit(Callable<T> callable) {
        RunnableFuture<T> ftask = newTaskFor(callable);
        futures.add(ftask);
        execute(ftask);
        return ftask;
    }

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
}