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

import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * {@code FloodWorker} which is tasked in performing the {@code resource} test.
 * <p>
 * All workers have a priority which is used by the
 * {@link FloodExecutorService} to determine a fairer distribution of all
 * {@code submitted} tasks in the {@code core pool}.
 *
 * @param <V> Type of value returned from the asynchronous task.
 * @see FloodThreadPoolExecutor
 * @see Torrent.TorrentFloodThreadPoolExecutor
 * @see Floodgate
 * @see Torrent
 */
@EqualsAndHashCode(callSuper = true)
public final class FloodWorker<V> extends FutureTask<V> implements Comparable<FloodWorker<V>> {

    /**
     * There are five priority levels: HIGHEST, HIGH, MEDIUM, LOW and LOWEST.
     */
    public enum FloodWorkerPriority {
        HIGHEST(0),HIGH(1),MEDIUM(2),LOW(3),LOWEST(4);
        private final int level;
        FloodWorkerPriority(int value) {this.level = value;}
        public int getLevel() {return level;}
        public static FloodWorkerPriority toPriority(int value) {
            return Arrays.stream(FloodWorkerPriority.values())
                    .filter(p -> p.getLevel() == value)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }

    private final FloodWorkerPriority priority;

    /**
     * Default constructor.
     * <p>
     * @param callable asynchronous task to perform
     * @param priority of this {@code flood worker}
     */
    public FloodWorker(final Callable<V> callable, final FloodWorkerPriority priority) {
        super(callable);
        this.priority = priority;
    }
    @Override
    public int compareTo(FloodWorker<V> o) {
        return this.priority.getLevel() - o.priority.getLevel();
    }
}
