package com.excelsior.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

@SuppressWarnings("WeakerAccess")
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
                (a,l)  -> a[0] += 1L,
                (l,r)  -> {l[0] += r[0]; return l;},
                result -> Nullable.of(result[0]),
                Collections.emptySet()
        );
    }

    public static <T> Reducer<T,?,Nullable<Double>> averagingDouble(ToDoubleFunction<? super T> mapper) {
        return new ReducerImpl<>(
                ()     -> new double[2],
                (a,l)  -> {a[0]  = a[0] + mapper.applyAsDouble(l); a[1] += 1;},
                (l,r)  -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Nullable.of (result[0] / result[1]),
                EnumSet.of(Collector.Characteristics.CONCURRENT)
        );
    }

    public static <T> Reducer<T,?,Nullable<Double>> averagingLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                ()     -> new long[2],
                (a,l)  -> {a[0]  = a[0] + mapper.applyAsLong(l); a[1] += 1;},
                (l,r)  -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Nullable.of ( (double) result[0] / result[1]),
                EnumSet.of(Collector.Characteristics.CONCURRENT)
        );
    }

    public static <T> Reducer<T,?,Nullable<Double>> averagingInt(ToIntFunction<? super T> mapper) {
        return new ReducerImpl<>(
                ()     -> new int[2],
                (a,l)  -> {a[0]  = a[0] + mapper.applyAsInt(l); a[1] += 1;},
                (l,r)  -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Nullable.of ( (double) result[0] / result[1]),
                EnumSet.of(Collector.Characteristics.CONCURRENT)
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
                (a,s)-> a.add(s.toString()),
                StringJoiner::merge,
                (result -> Nullable.of(result.toString())),
                Collections.emptySet());
    }

    public static <T> Reducer<T,Partition<T>,Nullable<Map<Boolean,List<T>>>> partitioningBy(Predicate<? super T> predicate) {
        return new ReducerImpl<>(
                Partition::new,
                (a,v) -> a.put(predicate.test(v),v),
                (l,r) -> { l.putAll(r); return l; },
                Nullable::of,
                Collections.emptySet()
        );
    }

    public static <T> Reducer<T,?,Nullable<IntSummaryStatistics>> summarizingInt(ToIntFunction<? super T> mapper) {
        return new ReducerImpl<>(
                IntSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsInt(v)),
                (l,r) -> {l.combine(r); return l;},
                Nullable::of,
                EnumSet.of(Collector.Characteristics.CONCURRENT)
        );
    }

    public static <T> Reducer<T,?,Nullable<LongSummaryStatistics>> summarizingLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                LongSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsLong(v)),
                (l,r) -> {l.combine(r); return l;},
                Nullable::of,
                EnumSet.of(Collector.Characteristics.CONCURRENT)
        );
    }

    public static <T> Reducer<T,?,Nullable<DoubleSummaryStatistics>> summarizingDouble(ToDoubleFunction<? super T> mapper) {
        return new ReducerImpl<>(
                DoubleSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsDouble(v)),
                (l,r) -> {l.combine(r); return l;},
                Nullable::of,
                EnumSet.of(Collector.Characteristics.CONCURRENT)
        );
    }


    /**
     * Partition class used by partitionBy
     */
    private static class Partition<T> extends AbstractMap<Boolean,List<T>>  {
        private Map<Boolean,List<T>> map;

        public Partition() {
            map = new HashMap<>();
            map.put(true, new ArrayList<>());
            map.put(false, new ArrayList<>());
        }

        public T put(Boolean key, T value) {
            map.get(key).add(value);
            return value;
        }

        @Override
        public Set<Entry<Boolean, List<T>>> entrySet() {
            return map.entrySet();
        }

        public List<T> put(Boolean k, List<T> list) {
            list.forEach (v -> put(k,v));
            return list;
        }

    }

}
