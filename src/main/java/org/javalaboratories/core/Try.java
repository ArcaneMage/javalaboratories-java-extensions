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

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.handlers.ThrowableSupplier;
import org.javalaboratories.core.util.Arguments;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The {@code Try} class represents a computation/operation that may either result
 * in an exception or a success. It is similar to the {@link Either} class type
 * but it dynamically decides the success/failure state.
 * <p>
 * This implementation of {@code Try} class is inspired by Scala's Try class,
 * and is considered to be a monad as well as a functor, which means the context
 * of the container is transformable via the {@code flatmap} and {@code map}
 * methods.
 * <p>
 * Below are some use case examples demonstrating elegant recovery strategies:
 * <pre>
 *     {@code
 *         // Recovering from arithmetic exceptions: result1="Result1=1000"
 *         String result1 = Try.of(() -> 100 / 0)
 *                             .recover(t -> t instanceof ArithmeticException ? 100 : 100)
 *                             .map(n -> n * 10)
 *                             .filter(n -> n > 500)
 *                             .fold("",n -> "Result1="+n);
 *
 *         // Using orElse to recover: result2="Result2=2500"
 *         String result2 = Try.of(() -> 100 / 0)
 *                             .orElse(100)
 *                             .map(n -> n * 25)
 *                             .filter(n -> n > 500)
 *                             .fold("",n -> "Result2="+n);
 *
 *         // IOExceptions are handled gracefully too: result3=0
 *         int result3 = Try.of(() -> new String(Files.readAllBytes(Paths.get("does-not-exist.txt"))))
 *                             .orElse("")
 *                             .map(String::length)
 *                             .fold(-1,Function.identity());
 *     }
 * </pre>
 * There are many more operations available, the API is documented, so go ahead
 * and explore them. Potentially there is a case to abandon the use of the
 * try-catch block in favour of a more functional programming approach.
 *
 * @param <T> resultant type of computation/operation
 */
public abstract class Try<T> extends Applicative<T> implements Monad<T>, Exportable<T>, Iterable<T>, Serializable {

    private static final long serialVersionUID = -7806171225526615129L;

    private static final IllegalStateException FAILED_TO_RETRIEVE_EXCEPTION = new IllegalStateException("Failed to retrieve exception from Try object");
    private static final NoSuchElementException NO_SUCH_ELEMENT_EXCEPTION = new NoSuchElementException();

    /**
     * Factory method of Try object.
     * <p>
     * The returned type maybe of {@link Success} or {@link Failure} depending
     * on computation behaviour.
     *
     * @param supplier function encapsulating computation/operation.
     * @param <T>      resultant type of computation.
     * @return Try object.
     */
    public static <T, E extends Throwable> Try<T> of(final ThrowableSupplier<T, E> supplier) {
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
     * @param <T>       type of resultant computation.
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
     * @param <T>   type of resultant computation.
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
            return failure(NO_SUCH_ELEMENT_EXCEPTION);
        } else {
            return success(getThrowableValue()
                    .orElseThrow(() -> FAILED_TO_RETRIEVE_EXCEPTION));
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
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Try<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Expected predicate function");
        Try<T> result;
        if (isSuccess()) {
            if (predicate.test(get()))
                result = this;
            else
                result = failure(NO_SUCH_ELEMENT_EXCEPTION);
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
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Try<T> filterNot(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "Expected predicate function");
        return filter(predicate.negate());
    }

    /**
     * {@inheritDoc}
     *
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
     * @param fa  the function to apply if this is a {@link Failure}
     * @param fb  the function to apply if this is a {@link Success}
     * @param <U> resultant type having applied {@code fa} or {@code fb}
     *            function.
     * @return the results of applying the function.
     */
    public <U> U fold(final Function<? super Throwable, ? extends U> fa, final Function<? super T, ? extends U> fb) {
        Arguments.requireNonNull("Expected functions", fa, fb);
        U result;
        if (isSuccess()) {
            result = fb.apply(get());
        } else {
            Throwable t = getThrowableValue()
                            .orElseThrow(() -> FAILED_TO_RETRIEVE_EXCEPTION);
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
     *
     * @return resultant {@link Try} having applied the given function to that
     * value.
     */
    @Override
    public <U> Try<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Function expected");
        if (isSuccess()) {
            return (Try<U>) super.<U>map(mapper);
        } else {
            return failure(getThrowableValue().get());
        }
    }

    /**
     * In the event of a failure, call the {@code Consumer} function, passing
     * the exception object.
     *
     * @param consumer function that accepts the exception object.
     * @param <E> type of exception.
     * @return this try object.
     */
    public <E extends Throwable> Try<T> onFailure(final Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer);
        if(isFailure()) {
            @SuppressWarnings("unchecked")
            E t = (E) getThrowableValue().get();
            consumer.accept(t);
        }
        return this;
    }

    /**
     * In the event of success, call the {@code Consumer} function, passing
     * the computed value.
     *
     * @param consumer function that accepts the computed value.
     * @return this try object.
     */
    public Try<T> onSuccess(final Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        if(isSuccess()) {
            consumer.accept(get());
        }
        return this;
    }

    /**
     * Returns this {@link Try} if it's a {@link Success} or {@code other} if
     * it's a {@link Failure}.
     *
     * @param other default value if this is a {@link Failure}
     * @return {@link Try} object.
     */
    public Try<T> orElse(final T other) {
        Try<T> result =  this;
        if (!isSuccess()) {
            result = success(other);
        }
        return result;
    }

    /**
     * Returns {@code this} value if this is a {@link Success}, otherwise
     * supplied exception is thrown.
     *
     * @param supplier supplies exception to be thrown if this is a {@link
     *                 Failure}
     * @param <E>      type of exception.
     * @return value of this {@link Try} object.
     * @throws E exception.
     */
    public <E extends Throwable> T orElseThrow(final Supplier<? extends E> supplier) throws E {
        E error = Objects.requireNonNull(supplier.get(),"Expected function");
        if (isSuccess()) {
            return get();
        } else {
            throw error;
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
     * @param fn  function to be applied to enable recovery.
     * @return {@link Try} object, maybe recovered from {@link Failure} object.
     */
    public Try<T> recover(final Function<? super Throwable, ? extends T> fn) {
        Objects.requireNonNull(fn, "Expected recover function");
        if (isFailure()) {
            Throwable t = getThrowableValue()
                            .orElseThrow(() -> FAILED_TO_RETRIEVE_EXCEPTION);
            return success(fn.apply(t));
        } else {
            return this;
        }
    }

    /**
     * Converts this {@code value} to a stream for processing.
     * <p>
     * If {@code this} is a {@link Failure}, an empty {@code stream} is returned;
     * otherwise {@link Success} is returned.
     *
     * @return a stream object is return, empty if {@link Failure}.
     */
    public Stream<T> stream() {
        return fold(Stream.of(),Stream::of);
    }

    /**
     * @return an {@link Either} object: {@link Either.Right} encapsulating value;
     * {@link Either.Left} encapsulating exception.
     */
    public Either<Throwable, T> toEither() {
        return isSuccess()
                ? Either.right(get())
                : Either.left(getThrowableValue()
                                .orElseThrow(() -> FAILED_TO_RETRIEVE_EXCEPTION));
    }

    /**
     * Returns an immutable list of containing {@code this} nonempty, {@code
     * value}.
     * <p>
     *
     * @return a {@link List} object containing a {@code value} from {@code
     * this} object, if available. Otherwise, an {@code empty} {@code List} is
     * returned.
     */
    @Override
    public List<T> toList() {
        return isSuccess()
                ? Collections.singletonList(get())
                : Collections.emptyList();
    }

    /**
     * Returns {@link Map} containing {@code this} value if nonempty, otherwise
     * an empty {@code Map} collection is returned.
     *
     * @param keyMapper function to derive unique key with which to insert the
     *                  {@code value}
     * @param <K>       Type of {@code map} key
     * @return a map containing {@code this} nonempty value, or an {@code empty}
     * map.
     */
    @Override
    public <K> Map<K, T> toMap(final Function<? super T, ? extends K> keyMapper) {
        Objects.requireNonNull(keyMapper);
        K k = null;
        if (isSuccess()) {
            k = keyMapper.apply(get());
        }
        K key = k;
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

    /**
     * Returns a {@link Set} containing {@code this} value if nonempty.
     *
     * @return a set of this context.
     */
    @Override
    public Set<T> toSet() {
        return isSuccess()
                ? Collections.singleton(get())
                : Collections.emptySet();
    }

    private <U extends Throwable> Maybe<U> getThrowableValue() {
        Maybe<U> result = Maybe.empty();
        Try<T> context = this;
        try {
            context.get();
        } catch (Throwable t) {
            // All exceptions are encased in RuntimeException object so they
            // need to be 'unpacked' for analysis.
            @SuppressWarnings("unchecked")
            U value = (U) t.getCause();
            result = Maybe.of(value);
        }
        return result;
    }

    /**
     * Represents the failure state of a computation/operation.
     *
     * @param <T> type of underling value.
     */
    @EqualsAndHashCode(callSuper = false)
    public final static class Failure<T> extends Try<T> {

        private final Throwable throwable;

        private Failure(Throwable throwable) {
            this.throwable = throwable;
        }

        /**
         * {@inheritDoc}
         * <p>
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
    @EqualsAndHashCode(callSuper = false)
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
