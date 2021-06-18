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
package org.javalaboratories.core.util;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Try;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * StopWatch provides a convenient means for timings of methods or routines.
 * <p>
 * There are no explicit methods to start and stop the timings, because these
 * are naturally determined through the process of invoking the function that
 * is currently being timed. In other words, calling the function will start the
 * {@link StopWatch} and all the function comes to a natural/unnatural
 * conclusion, the {@link StopWatch} is automatically stopped.
 * <p>
 * Use the {@link StopWatch#time(Runnable)}
 *
 * <pre>
 *     {@code
 *          StopWatch stopWatch = new StopWatch();
 *
 *          // This is a common usecase of the StopWatch
 *          stopWatch.time(() -> doSomethingMethod(1000));
 *     }
 * </pre>
 *
 * This class is considered thread-safe.
 *
 * @author Kevin Henry, JavaLaboratories
 */
@EqualsAndHashCode
public final class StopWatch implements Serializable {

    private static final Map<String,StopWatch> watches = new ConcurrentHashMap<>();

    private volatile long cycles;
    private volatile long time;
    private final String name;

    /**
     * Factory method to provide an instance of a {@link StopWatch}.
     * <p>
     * The {@code watch} is stored in a container of {@code watches} and can be
     * accessed by this method or via the {@link StopWatch#forEach(BiConsumer)}
     * method.
     * @param name Unique name of the {@link StopWatch}. Consider a meaningful
     *             name such as a method name.
     * @return an instance of {@link StopWatch}.
     */
    public static StopWatch watch(final String name) {
        Objects.requireNonNull(name,"Name?");
        return watches.computeIfAbsent(name,StopWatch::new);
    }

    /**
     * Performs an iteration over all known {@link StopWatch} instances in the
     * container.
     *
     * @param consumer function to perform operation on each iteration.
     */
    public static void forEach (final BiConsumer<String,StopWatch> consumer) {
        Objects.requireNonNull(consumer);
        watches.forEach(consumer);
    }

    /**
     * Clears the container of all {@link StopWatch} instances in the {@code
     * watches} container.
     * <p>
     * Container is considered empty after calling this method and no longer
     * manages orphaned {@link StopWatch} objects.
     */
    public static void clear() {
        forEach((n, s) -> s.reset());
        watches.clear();
    }

    /**
     * Default constructor
     */
    private StopWatch(final String name) {
        Objects.requireNonNull(name,"Name?");
        time = 0;
        cycles = 0;
        this.name = name;
    }

    /**
     * This is the current time, as opposed to the average time as {@link
     * StopWatch#getTime()} returns.
     *
     * @return time in its "natural" form in nanoseconds.
     */
    public long getRawTime() {
        return getRawTime(TimeUnit.NANOSECONDS);
    }

    /**
     * This is the current time, as opposed to the average time as {@link
     * StopWatch#getTime()} returns.
     *
     * @param unit to convert the raw time.
     * @return time in its "natural" form.
     */
    public long getRawTime(final TimeUnit unit) {
        Objects.requireNonNull(unit);
        return unit.convert(time,TimeUnit.NANOSECONDS);
    }

    /**
     * If there are multiple cycles/iterations (number of times the method {@link
     * StopWatch#time(Runnable)} is called, an average of time elapsed is returned.
     *
     * @return current running total of {@code StopWatch} time. If zero, the
     * process may not started yet or is incomplete.
     */
    public long getTime() {
        return Try.of(() -> time / cycles)
                .orElse(0L)
                .fold(0L,n -> n);
    }

    /**
     * Returns number of cycles/iterations. This represents the number of times
     * the {@link StopWatch#time} is called.
     * <p>
     * Therefore {@code getTime} maintains a running total. If the value
     * returned is divided by the number of {@code cycles}, this would yield the
     * average time.
     *
     * @return number of cycles/iterations.
     */
    public long getCycles() {
        return cycles;
    }

    /**
     * Returns the current {@link StopWatch} time. If zero, the process hasn't
     * started yet or is incomplete. {@link TimeUnit} is useful for
     * converting the time in nanoseconds to some other unit, for example
     * seconds or minutes.
     *
     * @param unit to convert the time.
     * @return {@link StopWatch} time converted to {@link TimeUnit}.
     */
    public long getTime(final TimeUnit unit) {
        Objects.requireNonNull(unit);
        return unit.convert(getTime(),TimeUnit.NANOSECONDS);
    }

    /**
     * Returns a string representation of {@link StopWatch} time. If zero,
     * the process hasn't started yet or is incomplete. The {@link String}
     * representation is HH:MM:SS.SSS, hours, minutes, seconds and milliseconds
     * respectively.
     *
     * @return {@link StopWatch} time in {@link String} form.
     */
    public String getTimeAsString() {
        long nanos = getTime();
        return String.format("%02d:%02d:%02d.%03d",
                TimeUnit.HOURS.convert(nanos,TimeUnit.NANOSECONDS) % 24,
                TimeUnit.MINUTES.convert(nanos,TimeUnit.NANOSECONDS) % 60,
                TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) % 60,
                TimeUnit.MILLISECONDS.convert(nanos,TimeUnit.NANOSECONDS) % 1000);
    }

    /**
     * Zeroes the {@link StopWatch}.
     */
    public void reset() {
        synchronized (this) {
            time = 0;
            cycles = 0;
        }
    }

    /**
     * This is the all important method that kicks off the timed process.
     *
     * <pre>
     *     {@code
     *          StopWatch stopWatch = new StopWatch();
     *
     *          // This is a common usecase of the StopWatch
     *          stopWatch.time(() -> doSomethingMethod(1000));
     *     }
     * </pre>
     *
     * @param runnable function that encapsulates the process to be timed.
     */
    public void time(final Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable function?");
        action(s -> runnable.run()).accept(null);
    }

    /**
     * This function is similar to {@link StopWatch#time(Runnable)} but designed
     * to be used with {@code forEach} methods of collections or {@code Streams}.
     * <p>
     * It will start the timings just before {@code accept} method is invoked;
     * and stop the timing {@code post-accept} method. This occurs on every
     * iteration of the {@code forEach} loop.
     * <pre>
     *     {@code
     *         // Given
     *         List<Integer> numbers = Arrays.asList(1,2,3,4);
     *
     *         // When
     *         numbers.forEach(stopWatch1.time(n -> doSomethingVoidMethodForMilliseconds(100)));
     *
     *         // Then
     *         assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) >= 100);
     *     }
     * </pre>
     *
     * @param consumer function
     * @param <T> type of parameter accepted to be consumed.
     * @return Consumer object with encapsulated timer logic.
     */
    public <T> Consumer<T> time(final Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer,"Consumer function?");
        return action(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StopWatch["+getTimeAsString()+"]";
    }

    private <T> Consumer<T> action(final Consumer<? super T> consumer) {
        return (s) -> {
            long start = System.nanoTime();
            try {
                consumer.accept(s);
            } finally {
                synchronized(this) {
                    cycles++;
                    time += System.nanoTime() - start;
                }
            }
        };
    }
}
