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
package org.javalaboratories.core;

import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.core.util.Generics;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code Try} monad represents a computation/operation that may either
 * results in an exception, or return successfully. It is similar to the
 * {@link Either} class type.
 * <p>
 * This implementation of {@code Try} class is inspired by Scala's Try class.
 *
 * @param <T> resultant type of computation/operation
 */
public abstract class Try<T> extends Applicative<T> implements Monad<T>, Iterable<T>, Serializable {

    /**
     * Factory method of Try object.
     * <p>
     * The returned type mayby of {@link Success} or {@link Failure} depending
     * on computation behaviour.
     *
     * @param supplier function encapsulating computation/operation.
     * @param <T> resultant type of computation.
     * @return Try object.
     */
    public static <T> Try<T> of(final Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        Try<T> result;
        try {
            result = success(supplier.get());
        } catch (Throwable e) {
            result = failure(e);
        }
        return result;
    }

    /**
     * Factory method to create a {@link Try} object, {@link Failure} type.
     * <p>
     * Very rarely the client would need to call this method as this is often
     * decided bu the {@link Try#of} factory method.
     *
     * @param throwable exception type.
     * @param <T> type of resultant computation.
     * @return Try object, {@link Failure}.
     */
    public static <T> Try<T> failure(final Throwable throwable) {
        Objects.requireNonNull(throwable, "Throwable object required");
        return new Failure<>(throwable);
    }

    /**
     * Factory method to create a {@link Try} object, {@link Success} type.
     * <p>
     * Very rarely the client would need to call this method as this is often
     * decided bu the {@link Try#of} factory method.
     *
     * @param value resultant value of type.
     * @param <T> type of resultant computation.
     * @return Try object, {@link Success}.
     */
    public static <T> Try<T> success(final T value) {
        Objects.requireNonNull(value, "Value object required");
        return new Success<>(value);
    }

    /**
     * {@inheritDoc}
     */
    public <R> Try<R> apply(final Applicative<Function<? super T, ? extends R>> applicative) {
        return (Try<R>) super.apply(applicative);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getOrElse(final T value) {
        return isSuccess() ? get() : value;
    }

    /**
     * Inverts this {@code Try}.
     * <p>
     * If success, {@link Failure} is returned with
     * {@link UnsupportedOperationException}, otherwise {@link Success} is
     * returned with encapsulated exception.
     *
     * @return inverted {@link Try}
     */
    public Try<Throwable> failed() {
        if (isSuccess()) {
            return failure(new UnsupportedOperationException());
        } else {
            return success(getThrowableValue(this).fold(null,Function.identity()));
        }
    }

    /**
     * Converts this to a {@link Failure} if {@code predicate} is not satisfied,
     * otherwise {@link Success} is returned.
     * <p>
     * It is important to note {@code predicate} is not executed for {@link
     * Failure} objects, but rather {@code this} is returned in such cases.
     *
     * @param predicate function, {@code false} will result in a {@link Failure};
     *                  {@code true} returns {@code this}.
     * @return Try object.
     */
    public Try<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Expected predicate function");
        Try<T> result;
        if (isSuccess()) {
            if (predicate.test(get()))
                result = this;
            else
                result = failure(new UnsupportedOperationException());
        } else {
            result = this;
        }
        return result;
    }

    /**
     * Converts this to a {@link Failure} if {@code predicate} is satisfied,
     * otherwise {@link Success} is returned.
     * <p>
     * It is important to note {@code predicate} is not executed for {@link
     * Failure} objects, but rather {@code this} is returned in such cases.
     *
     * @param predicate function, {@code true} will result in a {@link Failure};
     *                  {@code false} returns {@code this}.
     * @return Try object.
     */
    public Try<T> filterNot(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Expected predicate function");
        Try<T> result;
        if (isSuccess()) {
            if (!predicate.test(get()))
                result = this;
            else
                result = failure(new UnsupportedOperationException());
        } else {
            result = this;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * @return resultant {@link Try} having applied the given function to that
     * value.
     */
    @Override
    public <U> Try<U> flatMap(final Function<? super T, ? extends Monad<U>> mapper) {
        Objects.requireNonNull(mapper, "Function expected");
        @SuppressWarnings("unchecked")
        Try<U> self = (Try<U>) this;
        return isSuccess()
                ? (Try<U>) Monad.super.flatMap(mapper)
                : self;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Try<U> flatten() {
        return (Try<U>) Monad.super.flatten();
    }

    /**
     * Applies {@code fa} if this is a {@link Failure} or {@code fb} if this is
     * a {@link Success}.
     *
     * @param fa the function to apply if this is a {@link Failure}
     * @param fb the function to apply if this is a {@link Success}
     * @param <U> resultant type having applied {@code fa} or {@code fb}
     *           function.
     * @return the results of applying the function.
     */
    public <U> U fold(final Function<? super Throwable, ? extends U> fa, final Function<? super T, ? extends U> fb) {
        Arguments.requireNonNull("Expected functions", fa, fb);
        U result;
        if (isSuccess()) {
            result = fb.apply(get());
        } else {
            Throwable t = getThrowableValue(this).fold(null, Function.identity());
            result = fa.apply(t);
        }
        return result;
    }

    /**
     * @return {@code true} if the {@code Try} is a {@link Failure}, otherwise
     * {@code false}.
     */
    public abstract boolean isFailure();

    /**
     * @return {@code true} if the {@code Try} is a {@link Success}, otherwise
     * {@code false}.
     */
    public abstract boolean isSuccess();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    /**
     * {@inheritDoc}
     * <p>
     * @return resultant {@link Try} having applied the given function to that
     * value.
     */
    @Override
    public <U> Try<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Function expected");
        @SuppressWarnings("unchecked")
        Try<U> self = (Try<U>) this;
        return isSuccess()
                ? Generics.unchecked(super.map(mapper))
                : self;
    }

    /**
     * Returns this {@link Try} if it's a {@link Success} or {@code other} if
     * it's a {@link Failure}.
     *
     * @param other default value if this is a {@link Failure}
     * @param <U> type of underlying value.
     * @return {@link Try} object.
     */
    public <U> Try<U> orElse(final U other) {
        @SuppressWarnings("unchecked")
        Try<U> result = (Try<U>) this;
        if (!isSuccess()) {
            result = success(other);
        }
        return result;
    }

    /**
     * Returns {@code this} value if this is a {@link Success}, otherwise supplied
     * exception is thrown.
     *
     * @param supplier supplies exception to be thrown if this is a {@link
     * Failure}
     * @param <E> type of exception.
     * @return value of this {@link Try} object.
     * @throws E exception.
     */
    public <E extends Throwable> T orElseThrow(final Supplier<? extends E> supplier) throws E {
        if (isSuccess()) {
            return get();
        } else {
            throw supplier.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<T> peek(final Consumer<? super T> consumer) {
        return (Try<T>) Monad.super.peek(consumer);
    }

    /**
     * Recovers this {@link Failure} object by applying the function {@code fn}.
     * <p>
     * The function is not applied for {@link Success} objects, but {@code this}
     * is returned.
     *
     * @param fn function to be applied to enable recovery.
     * @param <U> type of recovered value.
     * @return {@link Try} object, maybe recovered from {@link Failure} object.
     */
    public <U> Try<U> recover(final Function<? super Throwable, ? extends U> fn) {
        Objects.requireNonNull(fn, "Expected recover function");
        if (isFailure()) {
            Throwable t = getThrowableValue(this).fold(null, Function.identity());
            return success(fn.apply(t));
        } else {
            @SuppressWarnings("unchecked")
            Try<U> result = (Try<U>) this;
            return result;
        }
    }

    /**
     * Returns an immutable list of containing {@code this} nonempty, {@code
     * value}.
     * <p>
     * @return a {@link List} object containing a {@code value} from {@code
     * this} object, if available. Otherwise, an {@code empty} {@code List} is
     * returned.
     */
    public List<T> toList() {
        return isSuccess()
                ? Collections.singletonList(get())
                : Collections.emptyList();
    }

    /**
     * @return an {@link Either} object: {@link Either.Right} encapsulating value;
     * {@link Either.Left} encapsulating exception.
     */
    public Either<Throwable, T> toEither() {
        return isSuccess()
                ? Either.right(get())
                : Either.left(getThrowableValue(this).fold(null, Function.identity()));
    }

    /**
     * Returns {@link Map} containing {@code this} value if nonempty, otherwise
     * an empty {@code Map} collection is returned.
     *
     * @param keyMapper function to derive unique key with which to insert the
     * {@code value}
     * @param <K>   Type of {@code map} key
     * @return a map containing {@code this} nonempty value, or an {@code empty}
     * map.
     */
    public <K> Map<K, T> toMap(final Function<? super T, ? extends K> keyMapper) {
        Objects.requireNonNull(keyMapper);
        K key = keyMapper.apply(get());
        return fold(Collections.emptyMap(), value -> Collections.singletonMap(key, value));
    }

    /**
     * @return {@link Maybe} object with encapsulated {@code value}.
     */
    public Maybe<T> toMaybe() {
        return isSuccess()
                ? Maybe.of(get())
                : Maybe.empty();
    }

    private <U extends Throwable> Maybe<U> getThrowableValue(Try<T> context) {
        Maybe<U> result = Maybe.empty();
        try {
            context.get();
        } catch (Throwable t) {
            @SuppressWarnings("unchecked")
            U value = (U) t;
            result = Maybe.of(value);
        }
        return result;
    }

    /**
     * Represents the failure state of a computation/operation.
     *
     * @param <T> type of underling value.
     */
    public final static class Failure<T> extends Try<T> {

        private final Throwable throwable;

        private Failure(Throwable throwable) {
            this.throwable = throwable;
        }

        /**
         * {@inheritDoc}
         *
         * This implementation returns the underlying exception encapsulated in
         * {@link RuntimeException}.
         */
        @Override
        public T get() {
            throw new RuntimeException(throwable);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFailure() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSuccess() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Applicative<U> pure(U value) {
            return new Failure<>(throwable);
        }
    }

    /**
     * Represents the success state of a computation/operation.
     *
     * @param <T> type of underlying value.
     */
    public final static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            super();
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isFailure() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSuccess() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <U> Applicative<U> pure(U value) {
            return new Success<>(value);
        }
    }

}
