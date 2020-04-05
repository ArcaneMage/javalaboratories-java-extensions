package com.excelsior.util;

import com.excelsior.util.statistics.LongStatisticalCalculators;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public final class Reducers {

    private static final Set<Collector.Characteristics> NO_CHARACTERISTICS = Collections.emptySet();

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

    /**
     * Partition class used by partitionBy reducer
     */
    private static class Partition<T> extends AbstractMap<Boolean, T> {
        private class PartitionSet extends AbstractSet<Map.Entry<Boolean, T>> {
            @Override
            public Iterator<Map.Entry<Boolean, T>> iterator() {
                Map.Entry<Boolean,T> falseEntry = new SimpleEntry<>(false, Partition.this.falseEntry);
                Map.Entry<Boolean,T> trueEntry = new SimpleEntry<>(true, Partition.this.trueEntry);
                return Arrays.asList(falseEntry,trueEntry).iterator();
            }
            @Override
            public int size() {
                return 2;
            }
        }
        private final T falseEntry;
        private final T trueEntry;
        private final PartitionSet set;

        Partition(final T falseEntry, final T trueEntry) {
            this.falseEntry = falseEntry;
            this.trueEntry = trueEntry;
            set = new PartitionSet();
        }

        @Override
        public Set<Entry<Boolean, T>> entrySet() {
            return set;
        }

        @Override
        public T put(Boolean key, T value) {
            set.stream()
                    .filter(e -> e.getKey() == key)
                    .findFirst()
                    .ifPresent(e -> e.setValue(value));
            return value;
        }
    }

    private Reducers() {}

    public static <T> Reducer<? super T, ?, Stream<Long>> counting() {
        return new ReducerImpl<> (
                () -> new long[1],
                (a,l) -> a[0] += 1L,
                (l,r) -> {l[0] += r[0]; return l;},
                result -> Stream.of(result[0]),
                NO_CHARACTERISTICS);
    }

    public static <T> Reducer<T,?,Stream<Double>> averagingDouble(ToDoubleFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new double[2],
                (a,l) -> {a[0]  = a[0] + mapper.applyAsDouble(l); a[1] += 1;},
                (l,r) -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Stream.of (result[0] / result[1]),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<Double>> averagingLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new long[2],
                (a,l) -> {a[0]  = a[0] + mapper.applyAsLong(l); a[1] += 1;},
                (l,r) -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Stream.of ( (double) result[0] / result[1]),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<Double>> averagingInt(ToIntFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new int[2],
                (a,l) -> {a[0]  = a[0] + mapper.applyAsInt(l); a[1] += 1;},
                (l,r) -> {l[0] += r[0]; l[1] += r[1]; return l;},
                result -> Stream.of ( (double) result[0] / result[1]),
                NO_CHARACTERISTICS
        );
    }

    public static Reducer<CharSequence,?,Stream<String>> joining() {
        return new ReducerImpl<>(
                StringBuilder::new,
                StringBuilder::append,
                (l,r) -> {l.append(r); return l;},
                (result -> Stream.of(result.toString())),
                NO_CHARACTERISTICS);
    }

    public static Reducer<CharSequence,?,Stream<String>> joining(String delimiter) {
        return joining(delimiter,"","");
    }

    public static Reducer<CharSequence,?,Stream<String>> joining(CharSequence delimiter,
                                                         CharSequence prefix,
                                                         CharSequence suffix) {
        return new ReducerImpl<>(
                () -> new StringJoiner(delimiter, prefix, suffix),
                (a,s) -> a.add(s.toString()),
                StringJoiner::merge,
                (result -> Stream.of(result.toString())),
                NO_CHARACTERISTICS);
    }

    public static <T> Reducer<T,?,Nullable<T>> maxBy(Comparator<? super T>  comparator) {
        return reduce(BinaryOperator.maxBy(comparator));
    }

    public static <T> Reducer<T,?,Nullable<T>> minBy(Comparator<? super T>  comparator) {
        return reduce(BinaryOperator.minBy(comparator));
    }

    public static <T> Reducer<T,?,Stream<Map<Boolean,List<T>>>> partitioningBy(Predicate<? super T> predicate) {
        return new ReducerImpl<>(
                () -> new Partition<List<T>>(new ArrayList<>(),new ArrayList<>()),
                (a,v) -> a.get(predicate.test(v)).add(v),
                (l,r) -> { r.forEach((rk,v) -> l.get(rk).addAll(v)); return l; },
                (result -> Stream.of(Collections.unmodifiableMap(result))),
                NO_CHARACTERISTICS
        );
    }

    public static <T,K> Reducer<T,?,Stream<Map<K,List<T>>>> groupingBy(Function<? super T, ? extends K> function) {
        BiConsumer<Map<K,List<T>>,T> accumulator = (a,v) -> {
            K key = function.apply(v);
            Objects.requireNonNull(key,"Cannot accept NULL result from function");
            a.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(v);
        };

        BinaryOperator<Map<K, List<T>>> combiner = (l,r) -> {
            r.forEach((rk, v) -> l.computeIfAbsent(rk, lk -> new ArrayList<>())
                    .addAll(v));
            return l;
        };

        return new ReducerImpl<>(
                HashMap::new,
                accumulator,
                combiner,
                (result -> Stream.of(Collections.unmodifiableMap(result))),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<Integer>> summingInt(ToIntFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new int[1],
                (a,v) -> a[0] += mapper.applyAsInt(v),
                (l,r) -> {l[0] += r[0]; return l;},
                (result -> Stream.of(result[0])),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<Long>> summingLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new long[1],
                (a,v) -> a[0] += mapper.applyAsLong(v),
                (l,r) -> {l[0] += r[0]; return l;},
                (result -> Stream.of(result[0])),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<Double>> summingDouble(ToDoubleFunction<? super T> mapper) {
        return new ReducerImpl<>(
                () -> new double[1],
                (a,v) -> a[0] += mapper.applyAsDouble(v),
                (l,r) -> {l[0] += r[0]; return l;},
                (result -> Stream.of(result[0])),
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<IntSummaryStatistics>> summarizingInt(ToIntFunction<? super T> mapper) {
        return new ReducerImpl<>(
                IntSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsInt(v)),
                (l,r) -> {l.combine(r); return l;},
                Stream::of,
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<LongSummaryStatistics>> summarizingLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                LongSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsLong(v)),
                (l,r) -> {l.combine(r); return l;},
                Stream::of,
                NO_CHARACTERISTICS
        );
    }

    public static <T> Reducer<T,?,Stream<LongStatisticalCalculators>> calculateStatisticsLong(ToLongFunction<? super T> mapper) {
        return new ReducerImpl<>(
                LongStatisticalCalculators::new,
                (a,v) -> a.accept(mapper.applyAsLong(v)),
                (l,r) -> {l.combine(r); return l;},
                Stream::of,
                NO_CHARACTERISTICS
        );
    }


    public static <T> Reducer<T,?,Stream<DoubleSummaryStatistics>> summarizingDouble(ToDoubleFunction<? super T> mapper) {
        return new ReducerImpl<>(
                DoubleSummaryStatistics::new,
                (a,v) -> a.accept(mapper.applyAsDouble(v)),
                (l,r) -> {l.combine(r); return l;},
                Stream::of,
                NO_CHARACTERISTICS
        );
    }

    private static <T> Reducer<T,Holder<T>,Nullable<T>> reduce(BinaryOperator<T> operator) {
        return new ReducerImpl<>(
                Holders::writableHolder,
                (a,v) -> a.set(operator.apply(a.get() != null ? a.get() : v,v)),
                (l,r) -> { l.set(operator.apply((l.get()),r.get())); return l; },
                (result) -> Nullable.ofNullable(result.get()),
                NO_CHARACTERISTICS
        );
    }
}
