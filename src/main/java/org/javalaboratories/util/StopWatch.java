package org.javalaboratories.util;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.Math.round;

/**
 * StopWatch provides a convenient means for timings of methods.
 * <p>
 * There are no explicit methods to start and stop the timings, because these are
 * naturally determined through the process of invoking the function that is currently
 * being timed. In other words, calling the function will start the
 * {@link StopWatch} and all the function comes to a natural/unnatural conclusion,
 * the {@link StopWatch} is automatically stopped.
 * <p>
 * Number of instances of {@link StopWatch} is unlimited, and if the instances are
 * related, useful statistics are available via the class' methods or the
 * {@link StopWatch#print()} to print pre-formatted data into a string. Every
 * instance has a unique name, which is useful all reviewing the statistics.
 * <p>
 * Use the {@link StopWatch#time(Runnable) or the {@link StopWatch#time(Consumer)}
 * method to start the timings, the latter is particularly useful for
 * {@link Collection#forEach(Consumer)} and/or streams.
 *
 * <pre>
 *     {@code
 *          StopWatch stopWatch = StopWatch.watch("methodOne");
 *          StopWatch stopWatch2 = StopWatch.watch("methodTwo");
 *
 *          // This is a common usecase of the StopWatch
 *          stopWatch.time(() -> doSomethingMethod(1000));
 *
 *          // Here is aother sceanario where the for each loop is measured.
 *          List<Integer> numbers = Arrays.asList(1,2,3,4);
 *
 *          numbers.forEach(stopWatch2.time(n -> doSomethingMethod2(n)));
 *
 *          // This command will print statistics for all StopWatch instances
 *          System.out.println(StopWatch.print());
 *
 *          // Output :-
 *
 *          Method                       Time (s)    %       Cycles Cycle Time(s)
 *          --------------------------------------------------------------------
 *          methodOne                00:00:00.504   8%            1 00:00:00.504
 *          methodTwo                00:00:01.451  92%            4 00:00:00.363
 *     }
 * </pre>
 * @author Kevin Henry, Excelsior Software
 */
@SuppressWarnings("WeakerAccess")
public final class StopWatch {
    public enum State {STAND_BY, RUNNING, STOPPED}

    private static Map<String,StopWatch> watches = new LinkedHashMap<>();
    private static long sumTotal = 0L;

    private final Cycles cycles;
    private final String name;
    private State state;

    public static StopWatch watch(final String name) {
        Objects.requireNonNull(name);
        return watches.computeIfAbsent(name, StopWatch::new);
    }

    public static void clear() {
        watches.values().stream()
                .filter(s -> s.getState() == State.RUNNING)
                .findAny()
                .ifPresent(s -> {throw new IllegalStateException("Found RUNNING state in StopWatch objects");});
        watches.values().forEach(StopWatch::reset);
        watches.clear();
        sumTotal = 0L;
    }

    public static String print() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-24s %12s %4s %12s %12s\n","Method","Time (s)","%","Cycles","Cycle Time(s)"));
        builder.append("--------------------------------------------------------------------\n");
        watches.forEach((k,v) -> builder.append(v.printStats()).append("\n"));
        return builder.toString();
    }

    private StopWatch(final String name) {
        this.name = name;
        this.state = State.STAND_BY;
        this.cycles = new Cycles();
    }

    public void time(final Runnable runnable) {
        Objects.requireNonNull(runnable,"Expected a runnable object");
        action(s -> runnable.run()).accept(null);
    }

    public <T> Consumer<T> time(final Consumer<? super T> action) {
        Objects.requireNonNull(action,"Expected a consumer function");
        return action(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StopWatch stopWatch = (StopWatch) o;
        return name.equals(stopWatch.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Cycles getCycles() {
        return cycles;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        verify(State.STAND_BY,State.STOPPED);
        return cycles.getTime();
    }

    public long getTime(TimeUnit unit) {
        return unit.convert(getTime(),TimeUnit.NANOSECONDS);
    }

    public String getTimeAsString() {
        return formatTimeUnits(getTime());
    }

    public int getTotalPercentile() {
        return cycles.getTime() == 0L ? 0 : (int) round((double) getTime() / sumTotal * 100);
    }

    public State getState() {
        return state;
    }

    public void reset() {
        if ( state == State.STAND_BY )
            return;
        verify(State.STOPPED);
        sumTotal -= cycles.getTime();
        cycles.reset();
        state = State.STAND_BY;
    }

    @Override
    public String toString() {
        if ( getState() == State.RUNNING ) {
            return String.format("StopWatch[name='%s',state='%s',cycles=%s]",name,state,cycles);
        } else {
            return String.format("StopWatch[name='%s',time=%d,millis=%d,seconds=%d,total-percentile=%d,state='%s',cycles=%s]",
                    name,getTime(),getTime(TimeUnit.MILLISECONDS),getTime(TimeUnit.SECONDS),getTotalPercentile(),state,cycles);
        }
    }

    private <T> Consumer<T> action(Consumer<? super T> consumer) {
        verify(State.STAND_BY,State.STOPPED);
        return s -> {
            verify(State.STAND_BY,State.STOPPED);
            long start = System.nanoTime();
            try {
                state = State.RUNNING;
                consumer.accept(s);
            } finally {
                long time = System.nanoTime() - start;
                cycles.setTime(time);
                sumTotal += time;
                state = State.STOPPED;
            }
        };
    }

    private String printStats() {
        String result;
        String name = getName();
        if ( name.length() > 24 )
            name = name.substring(0,21)+"...";
        if ( getState() == State.RUNNING )
            result = String.format("%-24s %14s", name, ">> "+getState())+" <<";
        else {
            result = String.format("%-24s %12s %3d%% %12d %12s", name, getTimeAsString(), getTotalPercentile(),
                    getCycles().getCount(), getCycles().getMeanTimeAsString());
        }
        return result;
    }

    private void verify(State... states) {
        boolean found = Arrays.stream(states)
                .anyMatch(state -> this.state == state);
        if (!found)
            throw new IllegalStateException("Not in the correct state(s): "+ Arrays.toString(states));
    }

    private static String formatTimeUnits(long nanos) {
        return String.format("%02d:%02d:%02d.%03d",
                TimeUnit.HOURS.convert(nanos,TimeUnit.NANOSECONDS) % 24,
                TimeUnit.MINUTES.convert(nanos,TimeUnit.NANOSECONDS) % 60,
                TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) % 60,
                TimeUnit.MILLISECONDS.convert(nanos,TimeUnit.NANOSECONDS) % 1000);
    }

    public final class Cycles {
        private long count;
        private long time;

        private Cycles() {}

        public long getCount() {
            return count;
        }

        public String toString() {
            return String.format("Cycles[count=%d]", count);
        }

        public long getMeanTime() {
            return count == 0L ? 0L : (getTime() / count);
        }

        public long getMeanTime(TimeUnit unit) {
            return unit.convert(getMeanTime(),TimeUnit.NANOSECONDS);
        }

        public String getMeanTimeAsString() {
            return formatTimeUnits(getMeanTime());
        }

        public long getTime() {
            return time;
        }

        private void setTime(long value) {
            verify(State.RUNNING);
            time += value;
            count++;
        }

        private void reset() {
            count = 0L;
            time = 0L;
        }
    }
}
