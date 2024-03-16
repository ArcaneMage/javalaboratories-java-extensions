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
 * This is a helper class for {@code float Holders}, that is to say {@link Holder}
 * objects containing {@code float} value type.
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
 *                .map(Float::valueOf)
 *                .collect(FloatHolders.summing())
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
 *             .reduce(Holder.of(0.0f),FloatHolders::sum,FloatHolders::sum)
 *             .map(n -> n / 2)
 *             .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");
 *
 *         assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15.0",result);
 *         logger.info(result);
 *     }
 * </pre>
 *
 * @see DoubleHolders
 * @see IntegerHolders
 * @see LongHolders
 */
public final class FloatHolders {

    /**
     * Constructs a {@link Holder} object to hold a {@code float} value.
     *
     * @param value the float value.
     * @return an instance {@link Holder} with contained {@code float}
     * value.
     */
    public static Holder<Float> of(float value) {
        return Holder.of(value);
    }

    /**
     * Constructs a {@link Holder} object to hold a read-only {@code float}
     * value.
     *
     * @param value the float value.
     * @return a read-only instance {@link Holder} with contained {@code float}
     * value.
     */
    public static Holder<Float> readOnly(float value) {
        return Holder.of(value).readOnly();
    }

    /**
     * Adds two {@link Holder} objects containing {@code float} values
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
    public static Holder<Float> sum(final Holder<Float> a, final Holder<Float> b) {
        Holder<Float> ha = Holder.copy(() -> Objects.requireNonNull(a));
        return ha.map(n -> n + Objects.requireNonNull(b).fold(0.0f,v -> v));
    }

    /**
     * Adds {@code int} value to {@link Holder} object containing {@code
     * float} values together.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant summation is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code int} parameter,
     * @return summation result in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Float> sum(final Holder<Float> a, final int b) {
        return sum(a,(long) b);
    }

    /**
     * Adds {@code long} value to {@link Holder} object containing {@code
     * float} values together.
     * <p>
     * The given {@link Holder} object is not mutated, and a new instance
     * containing the resultant summation is returned.
     *
     * @param a the first {@link Holder} object or operand.
     * @param b the second {@code long} parameter,
     * @return summation result in new {@link Holder} object.
     * @throws NullPointerException when either parameter is a null reference.
     */
    public static Holder<Float> sum(final Holder<Float> a, final long b) {
        Holder<Float> r = Holder.of(0.0f);
        return r.map(n -> n + Objects.requireNonNull(a).fold(0.0f,v -> v) + b);
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
    public static Holder<Float> max(final Holder<Float> a, final Holder<Float> b) {
        return of(Float.max(Objects.requireNonNull(a).fold(0.0f, Function.identity()),
                Objects.requireNonNull(b).fold(0.0f, Function.identity())));
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
    public static Holder<Float> max(final Holder<Float> a, final int b) {
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
    public static Holder<Float> max(final Holder<Float> a, final long b) {
        return of(Float.max(Objects.requireNonNull(a).fold(0.0f,n -> n),b));
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
    public static Holder<Float> min(final Holder<Float> a, final Holder<Float> b) {
        return of(Float.min(Objects.requireNonNull(a).fold(0.0f, Function.identity()),
                Objects.requireNonNull(b).fold(0.0f, Function.identity())));
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
    public static Holder<Float> min(final Holder<Float> a, final int b) {
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
    public static Holder<Float> min(final Holder<Float> a, final long b) {
        return of(Float.min(Objects.requireNonNull(a).fold(0.0f,n -> n),b));
    }

    /**
     * Sums {@link Holder} objects containing {@code float} values together.
     * <p>
     * Returns a custom {@link Collector} that collects and sums {@link Holder}
     * objects containing {@code float} values.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Float,Holder<Float>,Holder<Float>> summing() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Float>> supplier() {
                return () -> Holder.of(0.0f);
            }

            @Override
            public BiConsumer<Holder<Float>, Float> accumulator() {
                return (a,b) -> a.setGet(v -> v + b);
            }

            @Override
            public BinaryOperator<Holder<Float>> combiner() {
                return (a,b) -> {a.setGet(v -> v + b.get()); return a;};
            }

            @Override
            public Function<Holder<Float>, Holder<Float>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     * Calculates the largest {@code float} value contained in a stream of
     * {@link Holder} objects.
     * <p>
     * Returns a custom {@link Collector} that collects and determines a {@link
     * Holder} object with the largest {@code float} value in the {@code
     * stream}.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Float,Holder<Float>,Holder<Float>> max() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Float>> supplier() {
                return () -> Holder.of(0.0f);
            }

            @Override
            public BiConsumer<Holder<Float>, Float> accumulator() {
                return (a, b) -> a.setGet(v -> Float.max(v, b));
            }

            @Override
            public BinaryOperator<Holder<Float>> combiner() {
                return (a, b) -> {a.setGet(v -> Float.max(v, b.get()));return a;};
            }

            @Override
            public Function<Holder<Float>, Holder<Float>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     * Calculates the smallest {@code float} value contained in a stream of
     * {@link Holder} objects.
     * <p>
     * Returns a custom {@link Collector} that collects and determines a {@link
     * Holder} object with the smallest {@code float} value in the {@code
     * stream}.
     *
     * @return a custom Collector for summation.
     */
    public static Collector<Float,Holder<Float>,Holder<Float>> min() {
        return new Collector<>() {

            @Override
            public Supplier<Holder<Float>> supplier() {
                return () -> Holder.of(Float.MAX_VALUE);
            }

            @Override
            public BiConsumer<Holder<Float>, Float> accumulator() {
                return (a, b) -> a.setGet(v -> Float.min(v, b));
            }

            @Override
            public BinaryOperator<Holder<Float>> combiner() {
                return (a, b) -> {a.setGet(v -> Float.min(v, b.get()));return a;};
            }

            @Override
            public Function<Holder<Float>, Holder<Float>> finisher() {
                return h -> h;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }
    
    private FloatHolders() {}
}