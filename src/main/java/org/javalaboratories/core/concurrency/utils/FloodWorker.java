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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * {@code FloodWorker} which tasked in performing the {@code resource} test.
 * <p>
 * All workers have a priority in the queue, but for executor which employs
 * {@code non-priority blocking queues}, this will have no impact on the
 * priority of the {@code flood worker}.
 *
 * @param <V> Type of value returned from the asynchronous task.
 */
public final class FloodWorker<V> extends FutureTask<V> implements Comparable<FloodWorker<V>> {
    private final Logger logger = LoggerFactory.getLogger(FloodWorker.class);

    public enum FloodWorkerPriority {
        HIGHEST(0),HIGH(1),MEDIUM(2),LOW(3),LOWEST(4);
        private final int level;
        FloodWorkerPriority(int value) {this.level = value;}
        public int getLevel() {return level;}
        public static FloodWorkerPriority toPriority(int value) {
            FloodWorkerPriority result = Arrays.stream(FloodWorkerPriority.values())
                    .filter(p -> p.getLevel() == value)
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
            return result;
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
