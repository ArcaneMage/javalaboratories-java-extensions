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

import java.util.concurrent.ExecutorService;

/**
 * An implementation of {@link FloodExecutorService}.
 * <p>
 * {@link Floodgate} and {@link Torrent} objects leverage this {@code thread
 * pool}.
 * <p>
 * Use the {@link FloodExecutorService#close(boolean)} to release allocated
 * memory resources pertaining to the pool, the parameter {@code true} forces
 * unfinished {@link FloodWorker} to conclude its task. It is recommended to use
 * the {@link FloodExecutorService#close} and allow the pool to take the
 * appropriate action.
 */
public interface FloodExecutorService extends ExecutorService {
    /**
     * Maximum timeout in which to wait for {@link FloodWorker} objects to
     * terminate their work gracefully.
     */
    int SHUTDOWN_TIMEOUT_SECONDS = 5;

    /**
     * Releases all allocated memory resources pertaining to this {@link
     * ExecutorService} object.
     * <p>
     * The {@code service} will wait
     * {@link FloodExecutorService#SHUTDOWN_TIMEOUT_SECONDS} for the
     * {@link FloodWorker} objects to conclude their work gracefully, upon which the
     * service will then terminate.
     */
    default void close() {
        close(false);
    }

    /**
     * Releases all allocated memory resources pertaining to this {@link
     * ExecutorService} object.
     * <p>
     * The {@code service} will wait
     * {@link FloodExecutorService#SHUTDOWN_TIMEOUT_SECONDS} for the
     * {@link FloodWorker} objects to conclude their work gracefully, upon which the
     * service will then terminate. This is governed by the {@code force} parameter.
     *
     * @param force {@code true} to instruct all {@link FloodWorker} objects to
     *              conclude their work immediately; {@code false} to encourage
     *              graceful conclusion within the allotted time of
     *              {@link FloodExecutorService#SHUTDOWN_TIMEOUT_SECONDS}
     */
    void close(boolean force);
}
