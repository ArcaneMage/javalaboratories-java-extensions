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
package org.javalaboratories.core.util.Holders;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * This is a helper class for {@code int Holders}, that is to say {@link Holder}
 * objects containing {@code int} value type.
 * <p>
 * The class supports the following mathematical functions {@code sum, min} and
 * {@code max} designed to be used with {@code Streams} collector and reduction
 * methods.
 * <p>
 * An example of usage:
 * <pre>
 *     {@code
 *         List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
 *         String result = numbers.parallelStream()
 *                .filter(n -> n % 2 == 0)
 *                .map(Integer::valueOf)
 *                .collect(IntegerHolders.summing())
 *                .map(n -> n / 2)
 *                .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");
 *
 *         assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15.0",result);
 *         logger.info(result);
 *     }
 * </pre>
 * Alternatively, use the {@code sum, max} and {@code min} functions with the {@code
 * Streams} {@code reduce} method:
 * <pre>
 *     {@code
 *         List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
 *         String result = numbers.parallelStream()
 *             .filter(n -> n % 2 == 0)
 *             .reduce(Holder.of(0),IntegerHolders::sum,IntegerHolders::sum)
 *             .map(n -> n / 2)
 *             .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");
 *
 *         assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15.0",result);
 *         logger.info(result);
 *     }
 * </pre>
 *
 * @see FloatHolders
 * @see DoubleHolders
 * @see LongHolders
 */
public final class IntegerHolders {

    /**
     * Constructs a {@link Holder} object to hold a {@code int} value.
     *
     * @param value the int value.
     * @return an instance {@link Holder} with contained {@code int}
     * value.
     */
    public static Holder<Integer> of(int value) {
        return Holder.of(value);
    }

    /**
     * Constructs a {@link Holder} object to hold a read-only {@code int}
     * value.
     *
     * @param value the int value.
     * @return a read-only instance {@link Holder} with contained {@code int}
     * value.
     */
    public static Holder<Integer> readOnly(int value) {
        return Holder.of(value).readOnly();
    }

    /**
     * Adds two {@link Holder} objects containing {@code int} values
     * together.
     * <p>
     * The given {@link Holder} objects are not mutated, and a new instance
     * containing the resultant summation is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@link Holder} object or operand.
     * @return summation result in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> sum(final Holder<Integer> a, final Holder<Integer> b) {
        Holder<Integer> ha = Holder.copy(() -> Objects.requireNonNull(a));
        return ha.map(n -> n + Objects.requireNonNull(b).fold(0,v -> v));
    }

    /**
     * Adds {@code int} value to {@link Holder} object containing {@code
     * int} values together.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant summation is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code int} parameter,
     * @return summation result in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> sum(final Holder<Integer> a, final int b) {
        return sum(a,(long) b);
    }

    /**
     * Adds {@code long} value to {@link Holder} object containing {@code
     * int} values together.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant summation is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code long} parameter,
     * @return summation result in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> sum(final Holder<Integer> a, final long b) {
        Holder<Integer> r = Holder.of(0);
        return r.map(n -> n + Objects.requireNonNull(a).fold(0,v -> v) + (int)b);
    }

    /**
     * Returns the greater of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@link Holder} object or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> max(final Holder<Integer> a, final Holder<Integer> b) {
        return of(Integer.max(Objects.requireNonNull(a).fold(0, Function.identity()),
                Objects.requireNonNull(b).fold(0, Function.identity())));
    }

    /**
     * Returns the greater of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code int} value or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> max(final Holder<Integer> a, final int b) {
        return max(a,(long) b);
    }

    /**
     * Returns the greater of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code long} value or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> max(final Holder<Integer> a, final long b) {
        return of(Integer.max(Objects.requireNonNull(a).fold(0,n -> n),(int)b));
    }

    /**
     * Returns the smaller of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@link Holder} object or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> min(final Holder<Integer> a, final Holder<Integer> b) {
        return of(Integer.min(Objects.requireNonNull(a).fold(0, Function.identity()),
                Objects.requireNonNull(b).fold(0, Function.identity())));
    }

    /**
     * Returns the smaller of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code int} value or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> min(final Holder<Integer> a, final int b) {
        return min(a, (long) b);
    }

    /**
     * Returns the smaller of two values in a {@link Holder} object.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant value is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code long} value or operand.
     * @return the resultant value in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Integer> min(final Holder<Integer> a, final long b) {
        return of(Integer.min(Objects.requireNonNull(a).fold(0,n -> n),(int)b));
    }

    /**
     * Sums {@link Holder} objects containing {@code int} values together.
     * <p>
     * Returns a custom {@link Collector} that collects and sums {@link Holder}
     * objects containing {@code int} values.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Integer,Holder<Integer>,Holder<Integer>> summing() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Integer>> supplier() {
                return () -> Holder.of(0);
            }

            @Override
            public BiConsumer<Holder<Integer>, Integer> accumulator() {
                return (a,b) -> a.setGet(v -> v + b);
            }

            @Override
            public BinaryOperator<Holder<Integer>> combiner() {
                return (a,b) -> {a.setGet(v -> v + b.get()); return a;};
            }

            @Override
            public Function<Holder<Integer>, Holder<Integer>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
            }
        };
    }

    /**
     * Calculates the largest {@code int} value contained in a stream of
     * {@link Holder} objects.
     * <p>
     * Returns a custom {@link Collector} that collects and determines a {@link
     * Holder} object with the largest {@code int} value in the {@code
     * stream}.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Integer,Holder<Integer>,Holder<Integer>> max() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Integer>> supplier() {
                return () -> Holder.of(0);
            }

            @Override
            public BiConsumer<Holder<Integer>, Integer> accumulator() {
                return (a, b) -> a.setGet(v -> Integer.max(v, b));
            }

            @Override
            public BinaryOperator<Holder<Integer>> combiner() {
                return (a, b) -> {a.setGet(v -> Integer.max(v, b.get()));return a;};
            }

            @Override
            public Function<Holder<Integer>, Holder<Integer>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
            }
        };
    }

    /**
     * Calculates the smallest {@code int} value contained in a stream of
     * {@link Holder} objects.
     * <p>
     * Returns a custom {@link Collector} that collects and determines a {@link
     * Holder} object with the smallest {@code int} value in the {@code
     * stream}.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Integer,Holder<Integer>,Holder<Integer>> min() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Integer>> supplier() {
                return () -> Holder.of(Integer.MAX_VALUE);
            }

            @Override
            public BiConsumer<Holder<Integer>, Integer> accumulator() {
                return (a, b) -> a.setGet(v -> Integer.min(v, b));
            }

            @Override
            public BinaryOperator<Holder<Integer>> combiner() {
                return (a, b) -> {a.setGet(v -> Integer.min(v, b.get()));return a;};
            }

            @Override
            public Function<Holder<Integer>, Holder<Integer>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
            }
        };
    }

    private IntegerHolders() {}
}