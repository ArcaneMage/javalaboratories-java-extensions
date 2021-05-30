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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Try<T> extends Applicative<T> implements Monad<T>, Iterable<T>, Serializable {

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

    public static <T> Try<T> failure(final Throwable throwable) {
        Objects.requireNonNull(throwable,"Throwable object required");
        return new Failure<>(throwable);
    }

    public static <T> Try<T> success(final T value) {
        Objects.requireNonNull(value,"Value object required");
        return new Success<>(value);
    }

    public <R> Try<R> apply(final Applicative<Function<? super T,? extends R>> applicative)  {
        return (Try<R>) super.apply(applicative);
    }

    @Override
    public T getOrElse(final T value) {
        return isSuccess() ? get() : value;
    }

    public Try<Throwable> failed() {
        if ( isSuccess() ) {
            return failure(new Exception());
        } else {
            @SuppressWarnings("unchecked")
            Try<Throwable> self = (Try<Throwable>) this;
            return self;
        }
    }

    public Try<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        Try<T> result;
        if ( isSuccess() ) {
            if ( predicate.test(get()) )
                result = this;
            else
                result = failure(new Exception());
        } else {
            result = this;
        }
        return result;
    }

    public Try<T> filterNot(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        Try<T> result;
        if ( isSuccess() ) {
            if ( !predicate.test(get()) )
                result = this;
            else
                result = failure(new Exception());
        } else {
            result = this;
        }
        return result;
    }

    @Override
    public <U> Try<U> flatMap(final Function<? super T, ? extends Monad<U>> mapper) {
        Objects.requireNonNull(mapper,"Function expected");
        @SuppressWarnings("unchecked")
        Try<U> self = (Try<U>) this;
        return isSuccess()
                ? (Try<U>) Monad.super.flatMap(mapper)
                : self;
    }

    @Override
    public <U> Try<U> flatten() {
        return (Try<U>) Monad.super.flatten();
    }

    public <U> U fold(final Function<? super Throwable, ? extends U> fa, final Function<? super T, ? extends U> fb) {
        Arguments.requireNonNull("Expected functions",fa,fb);
        U result;
        if (isSuccess()) {
            result = fb.apply(get());
        } else {
            Throwable t = getThrowableValue(this).fold(null, Function.identity());
            result = fa.apply(t);
        }
        return result;
    }

    public abstract boolean isSuccess();

    public abstract boolean isFailure();

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    @Override
    public <U> Try<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Function expected");
        @SuppressWarnings("unchecked")
        Try<U> self = (Try<U>) this;
        return isSuccess()
                ? Generics.unchecked(super.map(mapper))
                : self;
    }

    public <U> Try<U> orElse(final U other) {
        @SuppressWarnings("unchecked")
        Try<U> result = (Try<U>) this;
        if (!isSuccess()) {
            result = success(other);
        }
        return result;
    }

    public <E extends Throwable> T orElseThrow(final Supplier<? extends E> supplier) throws E {
        if ( isSuccess() ) {
            return get();
        } else {
            throw supplier.get();
        }
    }

    @Override
    public Try<T> peek(final Consumer<? super T> consumer) {
        return (Try<T>) Monad.super.peek(consumer);
    }

    public <U> Try<U> recover(final Function<? super Throwable,? extends U> fn) {
        Objects.requireNonNull(fn,"Expected recover function");
        if (isFailure()) {
            Throwable t = getThrowableValue(this).fold(null,Function.identity());
            return success(fn.apply(t));
        } else {
            @SuppressWarnings("unchecked")
            Try<U> result = (Try<U>) this;
            return result;
        }
    }

    public List<T> toList() {
        return isSuccess()
                ? Collections.singletonList(get())
                : Collections.emptyList();
    }

    public Maybe<T> toMaybe() {
        return isSuccess()
                ? Maybe.of(get())
                : Maybe.empty();
    }

    public Either<Throwable,T> toEither() {
        return isSuccess()
                ? Either.right(get())
                : Either.left(getThrowableValue(this).fold(null,Function.identity()));
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

    public final static class Failure<T> extends Try<T> {

        private final Throwable throwable;

        private Failure(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public T get() {
            throw new RuntimeException(throwable);
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        protected <U> Applicative<U> pure(U value) {
            return new Failure<>(throwable);
        }
    }

    public final static class Success<T> extends Try<T> {
        private final T value;

        private Success(T value) {
            super();
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        protected <U> Applicative<U> pure(U value) {
            return new Success<>(value);
        }
    }

}
