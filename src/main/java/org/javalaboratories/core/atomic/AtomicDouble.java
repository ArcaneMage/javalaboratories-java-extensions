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
package org.javalaboratories.core.atomic;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * A double value that may be updated automatically. An {@code AtomicDouble} is
 * thread-safe allowing atomic operations on the internal double value. It does,
 * however, inherit from {@link Number} to allow access by tools and utilities
 * that work with numerically based classes.
 */
public class AtomicDouble extends Number {

    @Serial
    private static final long serialVersionUID = -7469909919078037355L;

    private final AtomicLong longBits;

    /**
     * Default constructor that initialises the value to zero.
     */
    public AtomicDouble() {
        this(0);
    }

    /**
     * Constructs this {@code AtomicDouble} with an initial value.
     *
     * @param initialValue Intial value of this {@code AtomicDouble}
     */
    public AtomicDouble(final double initialValue) {
        longBits = new AtomicLong(toLongBits(initialValue));
    }

    /**
     * Atomically updates the current value with results of the applying the
     * given function to the current given value, returning the updated value.
     *
     * @param x updated value
     * @param accumulatorFunction a function of two arguments
     * @return the updated value
     */
    public final double accumulateAndGet(final double x, final DoubleBinaryOperator accumulatorFunction) {
        double prev,next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev, x);
        } while(!compareAndSet(prev,next));
        return next;
    }

    /**
     * Atomically adds the given value/delta to the current value.
     *
     * @param delta value
     * @return the updated value
     */
    public final double addAndGet(final double delta) {
        return accumulateAndGet(delta, Double::sum);
    }

    /**
     * Sets that value to the given updated value only if the updated value is
     * equal to the expected value. This is done atomically.
     *
     * @param expect expected value
     * @param update the new value
     * @return {@code true} if successful, {@code False} to indicate actual
     * value does not equal to the expected value.
     */
    public final boolean compareAndSet(final double expect, final double update) {
        return longBits.compareAndSet(toLongBits(expect),toLongBits(update));
    }

    /**
     * Decrements the current value by one atomically.
     *
     * @return the updated value
     */
    public final double decrementAndGet() {
        return accumulateAndGet(-1.0,Double::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double doubleValue() {
        return get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final float floatValue() {
        return (float) get();
    }

    /**
     * Gets the current value.
     *
     * @return the current value.
     */
    public final double get() {
        return toDouble(longBits.get());
    }

    /**
     * Atomically updates the current value with the results of applying the
     * given function to the current value, returning the previous value.
     *
     * @param x the updated value
     * @param accumulatorFunction function of two arguments
     * @return the previous value
     */
    public final double getAndAccumulate(final double x, DoubleBinaryOperator accumulatorFunction) {
        double prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev,x);
        } while(!compareAndSet(prev,next));
        return prev;
    }

    /**
     * Adds the given value to the current value atomically.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final double getAndAdd(final double delta) {
        return getAndAccumulate(delta,Double::sum);
    }

    /**
     * Decrements the current value by one and returns the previous value
     * atomically.
     *
     * @return the previous value.
     */
    public final double getAndDecrement() {
        return getAndAccumulate(-1.0,Double::sum);
    }

    /**
     * Increments the current value by one and returns the previous value
     * atomically.
     *
     * @return the previous value.
     */
    public final double getAndIncrement() {
        return getAndAccumulate(1.0,Double::sum);
    }

    /**
     * Sets the current value and returns the previous value atomically.
     *
     * @return the previous value.
     */
    public final double getAndSet(final double newValue) {
        return toDouble(longBits.getAndSet(toLongBits(newValue)));
    }

    /**
     * Updates the current value with the results of the function
     * atomically.
     *
     * @param updateFunction the function
     * @return the previous value
     */
    public final double getAndUpdate(final DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * Increments the current value by one and returns the current value.
     *
     * @return the updated value.
     */
    public final double incrementAndGet() {
        return accumulateAndGet(1.0,Double::sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int intValue() {
        return (int) get();
    }

    /**
     * Eventually sets the given value.
     *
     * @param newValue the new value
     */
    public final void lazySet(double newValue) {
        longBits.lazySet(toLongBits(newValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long longValue() {
        return (long) get();
    }

    /**
     * Sets the current value with the given value.
     *
     * @param newValue the new value.
     */
    public final void set(final double newValue) {
        longBits.set(toLongBits(newValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return String.valueOf(get());
    }

    /**
     * Atomically updates the current value with the results of applying the
     * given function.
     *
     * @param updateFunction a function
     * @return the updated value.
     */
    public final double updateAndGet(final DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    /**
     * Sets that value to the given updated value only if the updated value is
     * equal to the expected value. This is done atomically.
     *
     * @param expect expected value
     * @param update the new value
     * @return {@code true} if successful, {@code False} to indicate actual
     * value does not equal to the expected value.
     */
    public final boolean weakCompareAndSet(final double expect, final double update) {
        return longBits.weakCompareAndSetPlain(toLongBits(expect), toLongBits(update));
    }

    private long toLongBits(final double value) {
        return Double.doubleToLongBits(value);
    }

    private double toDouble(final long longBits) {
        return Double.longBitsToDouble(longBits);
    }
}
