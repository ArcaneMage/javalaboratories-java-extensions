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
package org.javalaboratories.core.concurrency;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Maybe;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Implements the {@code PromiseLater} strategy for the {@link Eval}
 * interface.
 * <p>
 * The evaluation of the {@code value} occurs asynchronously. The evaluation
 * of the {@code value} is performed once and cached.
 *
 * @param <T> Type of lazily computed {@code value}.
 */
@EqualsAndHashCode(callSuper = true)
public class AsyncEval<T> extends Eval<T> {

    private transient final Promise<T> promise;
    private final Eval<T> delegate;
    private final Object lock = new Object();
    private Exception exception;

    /**
     * Constructs implementation of {@link Eval} with the {@code Later}
     * strategy.
     *
     * @param function function that computes the {@code value}.
     */
    AsyncEval(final Supplier<T> function) {
        promise = Promises.newPromise(PrimaryAction.of(function,this::handle));
        delegate = Eval.later(this::asyncValue);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isComplete() {
        return promise.getState() != Promise.States.PENDING;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFulfilled() {
        return promise.getState() == Promise.States.FULFILLED;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return promise.getState() == Promise.States.REJECTED;
    }

    /**
     * {@inheritDoc}
     */
    public Maybe<Exception> getException() {
        synchronized (lock) {
            return Maybe.ofNullable(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eval<T> reserve() {
        return flatMap(Eval::eager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <U> Eval<U> pure(final U value) {
        return new Later<>((Supplier<U>) () -> value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation evaluates the {@code value} by blocking for the
     * asynchronous process to complete -- evaluation only occurs once.
     *
     * @throws NoSuchElementException evaluation failure in asynchronous task.
     */
    @Override
    protected T value() {
        return delegate.get();
    }

    private T asyncValue() {
        Maybe<T> maybe = promise.getResult();
        if (isRejected()) {
            throw new NoSuchElementException("Evaluation not possible due" +
                    " to asynchronous exception");
        }
        return maybe.orElse(null);
    }


    private void handle(final T value, final Throwable e) {
        synchronized(lock) {
            exception = (Exception) e;
        }
    }
}
