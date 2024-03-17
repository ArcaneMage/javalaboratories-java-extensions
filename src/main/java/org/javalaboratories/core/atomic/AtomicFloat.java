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

import org.javalaboratories.core.function.FloatBinaryOperator;
import org.javalaboratories.core.function.FloatUnaryOperator;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An integer value that may be updated automatically. An {@code AtomicDouble} is
 * thread-safe allowing atomic operations on the internal double value. It does,
 * however, inherit from {@link Number} to allow access by tools and utilities
 * that work with numerically based classes.
 */
public class AtomicFloat extends Number {

    @Serial
    private static final long serialVersionUID = 8079395341022325432L;
    private final AtomicInteger intBits;

    /**
     * Default constructor that initialises the value to zero.
     */
    public AtomicFloat() {
        this(0);
    }

    /**
     * Constructs this {@code AtomicDouble} with an initial value.
     *
     * @param initialValue Intial value of this {@code AtomicDouble}
     */
    public AtomicFloat(final float initialValue) {
        intBits = new AtomicInteger(toIntegerBits(initialValue));
    }

    /**
     * Atomically updates the current value with results of the applying the
     * given function to the current given value, returning the updated value.
     *
     * @param x updated value
     * @param accumulatorFunction a function of two arguments
     * @return the updated value
     */
    public final float accumulateAndGet(final float x, final FloatBinaryOperator accumulatorFunction) {
        float prev,next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsFloat(prev, x);
        } while(!compareAndSet(prev,next));
        return next;
    }

    /**
     * Atomically adds the given value/delta to the current value.
     *
     * @param delta value
     * @return the updated value
     */
    public final float addAndGet(final float delta) {
        return accumulateAndGet(delta,Float::sum);
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
    public final boolean compareAndSet(final float expect, final float update) {
        return intBits.compareAndSet(toIntegerBits(expect), toIntegerBits(update));
    }

    /**
     * Decrements the current value by one atomically.
     *
     * @return the updated value
     */
    public final float decrementAndGet() {
        return accumulateAndGet(-1.0f,Float::sum);
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
        return get();
    }

    /**
     * Gets the current value.
     *
     * @return the current value.
     */
    public final float get() {
        return toFloat(intBits.get());
    }

    /**
     * Atomically updates the current value with the results of applying the
     * given function to the current value, returning the previous value.
     *
     * @param x the updated value
     * @param accumulatorFunction function of two arguments
     * @return the previous value
     */
    public final float getAndAccumulate(final float x, FloatBinaryOperator accumulatorFunction) {
        float prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsFloat(prev,x);
        } while(!compareAndSet(prev,next));
        return prev;
    }

    /**
     * Adds the given value to the current value atomically.
     *
     * @param delta the value to add
     * @return the previous value
     */
    public final float getAndAdd(final float delta) {
        return getAndAccumulate(delta,Float::sum);
    }

    /**
     * Decrements the current value by one and returns the previous value
     * atomically.
     *
     * @return the previous value.
     */
    public final float getAndDecrement() {
        return getAndAccumulate(-1.0f,Float::sum);
    }

    /**
     * Increments the current value by one and returns the previous value
     * atomically.
     *
     * @return the previous value.
     */
    public final float getAndIncrement() {
        return getAndAccumulate(1.0f,Float::sum);
    }

    /**
     * Sets the current value and returns the previous value atomically.
     *
     * @return the previous value.
     */
    public final float getAndSet(final float newValue) {
        return toFloat(intBits.getAndSet(toIntegerBits(newValue)));
    }

    /**
     * Updates the current value with the results of the function
     * atomically.
     *
     * @param updateFunction the function
     * @return the previous value
     */
    public final float getAndUpdate(final FloatUnaryOperator updateFunction) {
        float prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsFloat(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    /**
     * Increments the current value by one and returns the current value.
     *
     * @return the updated value.
     */
    public final float incrementAndGet() {
        return accumulateAndGet(1.0f,Float::sum);
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
    public final void lazySet(float newValue) {
        intBits.lazySet(toIntegerBits(newValue));
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
    public final void set(final float newValue) {
        intBits.set(toIntegerBits(newValue));
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
    public final float updateAndGet(final FloatUnaryOperator updateFunction) {
        float prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsFloat(prev);
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
    public final boolean weakCompareAndSet(final float expect, final float update) {
        return intBits.weakCompareAndSetPlain(toIntegerBits(expect), toIntegerBits(update));
    }

    private int toIntegerBits(final float value) {
        return Float.floatToIntBits(value);
    }

    private float toFloat(final int intBits) {
        return Float.intBitsToFloat(intBits);
    }
}
