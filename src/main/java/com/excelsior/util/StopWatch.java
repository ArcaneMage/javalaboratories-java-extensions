package com.excelsior.util;

import java.util.*;
import java.util.function.Consumer;

import static java.lang.Math.round;

@SuppressWarnings("WeakerAccess")
public final class StopWatch {
    public enum State {STAND_BY, RUNNING, STOPPED}

    public interface Cycles {
        long getValue();
        void increment();
        double getTimeInSeconds();
        void reset();
    }

    private static Map<String,StopWatch> watches = new LinkedHashMap<>();
    private static long sumTotal = 0L;

    private final Cycles cycles;
    private final String name;
    private long time;
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
        this.time = 0;
        this.cycles = new CyclesImpl();
    }

    public void time(Runnable runnable) {
        Objects.requireNonNull(runnable,"Expected a runnable function");
        time(s -> runnable.run());
    }

    public void time(Consumer<? super StopWatch> consumer) {
        Objects.requireNonNull(consumer,"Expected a consumer function");
        verify(State.STAND_BY);
        long start = System.nanoTime();
        try {
            state = State.RUNNING;
            getCycles().increment();
            consumer.accept(this);
        } finally {
            time = System.nanoTime() - start;
            state = State.STOPPED;
            sumTotal += time;
        }
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
        verify(State.STOPPED);
        return time;
    }

    public long getTimeInMillis() {
        return round(getTime() / 1000000.0);
    }

    public double getTimeInSeconds() {
        return getTime() / (1000.0 * 1000000.0);
    }

    public int getTotalPercentile() {
        return (int) round((double) getTime() / sumTotal * 100);
    }

    public State getState() {
        return state;
    }

    public void reset() {
        if ( state == State.STAND_BY )
            return;
        verify(State.STOPPED);
        cycles.reset();
        state = State.STAND_BY;
        sumTotal -= time;
    }

    @Override
    public String toString() {
        if ( getState() == State.STAND_BY || getState() == State.RUNNING ) {
            return String.format("StopWatch[name='%s',state='%s',cycles=%s]",name,state,cycles);
        } else {
            return String.format("StopWatch[name='%s',time=%d,seconds=%.5f,millis=%d,total-percentile=%d,state='%s',cycles=%s]",
                    name,time,getTimeInSeconds(),getTimeInMillis(),getTotalPercentile(),state,cycles);
        }
    }

    private String printStats() {
        String result;
        String name = getName();
        if ( name.length() > 24 )
            name = name.substring(0,21)+"...";
        if ( getState() != State.STOPPED )
            result = String.format("%-24s %14s", name, ">> "+getState())+" <<";
        else {
            result = String.format("%-24s %12.5f %3d%% %12d %12.5f", name, getTimeInSeconds(), getTotalPercentile(),
                    getCycles().getValue(), getCycles().getTimeInSeconds());
        }
        return result;
    }

    private void verify(State state) {
        if ( this.state != state )
            throw new IllegalStateException("Not in "+state+" state");
    }

    private class CyclesImpl implements Cycles {
        private long value;

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void increment() {
            verify(State.RUNNING);
            value++;
        }

        @Override
        public double getTimeInSeconds() {
            return (StopWatch.this.getTime() / (double) value) / (1000.0 * 1000000.0);
        }

        public void reset() {
            if ( state == State.STAND_BY ) return;
            verify(State.STOPPED);
            value = 0;
        }

        public String toString() {
            return String.format("Cycles[value=%d]", value);
        }
    }
}
