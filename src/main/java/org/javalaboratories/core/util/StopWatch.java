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

import java.io.PrintStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * StopWatch provides a convenient means for timings of methods or routines.
 * <p>
 * There are no explicit methods to start and stop the timings because these
 * are naturally determined through the process of invoking a function that
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
 *          // This is a common use case of the StopWatch
 *          stopWatch.time(() -> doSomethingMethod(1000));
 *     }
 * </pre>
 *
 * This class is considered thread-safe.
 *
 * @author Kevin Henry, Java Laboratories
 */
@EqualsAndHashCode
public final class StopWatch implements Serializable, Comparable<StopWatch> {
    @Serial
    private static final long serialVersionUID = 2397064136918983422L;

    private static final String DEFAULT_DATETIME_FORMAT ="HH:mm:ss.SSS";
    private static final Map<String,StopWatch> watches = new ConcurrentHashMap<>();

    private volatile long cycles;
    private volatile long time;
    private final String name;

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
     * @return Outputs all elapse timings in ascending order in String form.
     */
    public static String println() {
        StringBuffer buffer = new StringBuffer();
        if (watches.size() > 0) {
            List<StopWatch> list = new ArrayList<>();
            watches.forEach((n, s) -> list.add(s));
            Collections.sort(list);
            buffer.append("\nElapse Time      Name\n");
            buffer.append("------------ --- --------------------------------\n");
            list.forEach(s -> buffer.append(String.format("%s %03d %-32s%n", s.getTimeAsString(), s.getCycles(), s.getName())));
            buffer.append("-------------------------------------------------\n");
        }
        return buffer.toString();
    }

    /**
     * Outputs all elapse timings in ascending order to the provided {@link
     * PrintStream}.
     *
     * @param stream PrintStream object.
     */
    public static void println(final PrintStream stream) {
        Objects.requireNonNull(stream, "Stream?");
        stream.println(println());
    }

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
     * Default constructor
     */
    private StopWatch(final String name) {
        Objects.requireNonNull(name,"Name?");
        time = 0;
        cycles = 0;
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final StopWatch other) {
        Objects.requireNonNull(other);
        return Long.compare(this.getTime(), other.getTime());
    }

    /**
     * Returns a string representation of {@link StopWatch} time formatted
     * by the provided {@code formatter}.
     *
     * @param formatter that encapsulates a pattern with which to format
     *                  the current time.
     * @return formatted time as a String.
     */
    public String format(final DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter,"Formatter?");
        long nanos = getTime();
        LocalTime localTime = LocalTime.of (
                (int) (TimeUnit.HOURS.convert(nanos,TimeUnit.NANOSECONDS) % 24),
                (int) (TimeUnit.MINUTES.convert(nanos,TimeUnit.NANOSECONDS) % 60),
                (int) (TimeUnit.SECONDS.convert(nanos,TimeUnit.NANOSECONDS) % 60),
                (int) (nanos % 1000000000)
        );
        return formatter
                .withZone(ZoneId.systemDefault())
                .format(localTime);
    }

    /**
     * Returns number of cycles/iterations. This represents the number of times
     * the {@link StopWatch#time} is called.
     * <p>
     * Therefore {@code getTime} maintains a running total. If the value
     * returned is divided by the number of {@code cycles}, this would yield
     * the average time.
     *
     * @return number of cycles/iterations.
     */
    public long getCycles() {
        return cycles;
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
     * If there are multiple cycles/iterations (number of times the method
     * {@link StopWatch#time(Runnable)} is called, an average of time elapsed
     * is returned.
     *
     * @return current running total of {@code StopWatch} time. If zero, the
     * process may not started yet or is incomplete.
     */
    public long getTime() {
        return Try.of(() -> time / cycles)
                .orElse(0L)
                .fold(0L, n -> n);
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
        return format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    /**
     * @return name of {@link StopWatch}
     */
    public String getName() {
        return name;
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
     * This function is similar to {@link StopWatch#time(Runnable)} but
     * designed to be used with {@code forEach} methods of collections or
     * {@code Streams}.
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
     * This is the all important method that kicks off the timed process.
     *
     * <pre>
     *     {@code
     *          StopWatch stopWatch = new StopWatch();
     *
     *          // This is a common use case of the StopWatch
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
     * This is then variant on the {@link StopWatch#time(Runnable)} that kicks
     * off the timed process;
     * <pre>
     *     {@code
     *          StopWatch stopWatch = new StopWatch();
     *
     *          // A value is returned from the timed computation
     *          int retval = stopWatch.time(() -> doSomethingMethod(1000));
     *          System.out.println(retval);
     *     }
     * </pre>
     * @param supplier function encapsulating computation to be timed, a value
     *                 is returned.
     * @param <T> type of returned value.
     * @return value from timed computation.
     */
    public <T> T time(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier,"Supplier function?");
        return action(supplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StopWatch["+ getTimeAsString()+"]";
    }

    private <T> Consumer<T> action(final Consumer<? super T> consumer) {
        return s -> {
            long start = System.nanoTime();
            try {
                consumer.accept(s);
            } finally {
                record(start);
            }
        };
    }

    private <T> T action(final Supplier<? extends T> supplier) {
        long start = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            record(start);
        }
    }

    private void record(final long start) {
        synchronized(this) {
            time += System.nanoTime() - start;
            cycles++;
        }
    }
}