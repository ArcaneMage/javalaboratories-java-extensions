package com.excelsior.util;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Reducers {

    private static class ReducerImpl<T,A,R> implements Reducer<T,A,R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A,T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A,R> finisher;
        private final Set<Characteristics> characteristics;

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

        public ReducerImpl(Supplier<A> supplier,
                           BiConsumer<A,T> accumulator,
                           BinaryOperator<A> combiner,
                           Function<A,R> finisher,
                           Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }
    }

    private Reducers() {}

    public static <T> Reducer<? super T, ?, Nullable<Long>> counting() {
        return new ReducerImpl<> (
                ()     -> new long[1],
                (e,l)  -> e[0] += 1L,
                (l,r)  -> {l[0] += r[0]; return l;},
                result -> Nullable.of(result[0]),
                Collections.emptySet()
        );
    }

    public static <T,R extends Long> Reducer<T,?,Nullable<Double>> averagingLong(Function<? super T, ? extends R> mapper) {
        return new ReducerImpl<>(
                ()     -> new long[2],
                (e,l)  -> {e[0]  = e[0] + (Long) mapper.apply(l); e[1] += 1;},
                (l,r)  -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Nullable.of ( (double) result[0] / result[1]),
                Collections.emptySet()
        );
    }

    public static Reducer<CharSequence,?,Nullable<String>> joining() {
        return new ReducerImpl<>(
                StringBuilder::new,
                StringBuilder::append,
                (l,r) -> {l.append(r); return l;},
                (result -> Nullable.of(result.toString())),
                Collections.emptySet());
    }

    public static Reducer<CharSequence,?,Nullable<String>> joining(String delimiter) {
        return joining(delimiter,"","");
    }

    public static Reducer<CharSequence,?,Nullable<String>> joining(CharSequence delimiter,
                                                         CharSequence prefix,
                                                         CharSequence suffix) {
        return new ReducerImpl<>(
                () -> new StringJoiner(delimiter, prefix, suffix),
                (sj,cs)-> sj.add(cs.toString()),
                StringJoiner::merge,
                (result -> Nullable.of(result.toString())),
                Collections.emptySet());
    }
}
