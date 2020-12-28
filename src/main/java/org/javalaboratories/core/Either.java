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

import org.javalaboratories.util.Arguments;
import org.javalaboratories.util.Generics;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings({"UnusedReturnValue","BooleanMethodIsAlwaysInverted"})
public interface Either<A,B> extends Iterable<B> {

    static <A,B> Either<A,B> of(final B value) {
        return right(value);
    }

    static <A,B> Either<A,B> left(final A value) {
        return new Left<>(value);
    }

    static <A,B> Either<A,B> right(final B value) {
        return new Right<>(value);
    }

    boolean contains(B element);

    default boolean exists(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        return false;
    }

    default Maybe<Either<A,B>> filter(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        return null;
    }

    default <C> Either<A,C> flatMap(final Function<? super B,? extends Either<A,C>> mapper) {
        Objects.requireNonNull(mapper,"Expected flatMap mapper");
        return null;
    }

    <C> Either<A,C> flatten();

    default <C> C fold(final Function<? super A,? extends C> fa, final Function<? super B,? extends C> fb) {
        Arguments.requireNonNull(fa,fb,"Expected fold functions");
        return null;
    }

    default boolean forAll(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        return false;
    }

    B getOrElse(final B other);

    boolean isLeft();

    boolean isRight();

    default <C> Either<A,C> map(final Function<? super B,? extends C> mapper) {
        Objects.requireNonNull(mapper,"Expected map function");
        return null;
    }

    Either<A,B> orElse(final Either<? extends A,? extends B> other);

    default Either<A,B> orElseGet(Supplier<? extends Either<? extends A,? extends B>> supplier) {
        Objects.requireNonNull(supplier);
        return null;
    }

    default <E extends Throwable> Either<A,B> orElseThrow(Supplier<? extends E> supplier) throws E {
        Objects.requireNonNull(supplier);
        return null;
    }

    Either<B,A> swap();

    List<B> toList();

    Maybe<B> toMaybe();


    final class Right<A,B> extends AbstractEither<A,B> {
        public Right(B right) {
            super(null, right);
        }

        @Override
        public boolean contains(B element) {
            return getRight().equals(element);
        }

        @Override
        public boolean exists(final Predicate<? super B> predicate) {
            super.exists(predicate);
            return predicate.test(getRight());
        }

        @Override
        public Maybe<Either<A,B>> filter(final Predicate<? super B> predicate)  {
            super.filter(predicate);
            return exists(predicate) ? Maybe.of(this) : Maybe.empty();
        }

        @Override
        public <C> Either<A,C> flatMap(final Function<? super B,? extends Either<A,C>> mapper) {
            super.flatMap(mapper);
            return Generics.unchecked(Objects.requireNonNull(mapper.apply(getRight())));
        }

        @Override
        public <C> Either<A,C> flatten() {
            return this.flatMap(r -> Generics.unchecked(getRight()));
        }

        @Override
        public <C> C fold(final Function<? super A,? extends C> fa, final Function<? super B,? extends C> fb) {
            super.fold(fa,fb);
            return fb.apply(getRight());
        }

        @Override
        public boolean forAll(final Predicate<? super B> predicate) {
            super.forAll(predicate);
            return exists(predicate);
        }

        @Override
        public B getOrElse(final B other) {
            return getRight();
        }

        @Override
        public Iterator<B> iterator() {
            return Collections.singletonList(getRight()).iterator();
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public <C> Either<A,C> map(final Function<? super B,? extends C> mapper) {
            super.map(mapper);
            return new Right<>(Objects.requireNonNull(mapper.apply(getRight())));
        }

        @Override
        public Either<A,B> orElse(final Either<? extends A,? extends B> other) {
            return this;
        }

        @Override
        public Either<A,B> orElseGet(Supplier<? extends Either<? extends A,? extends B>> supplier) {
            super.orElseGet(supplier);
            return this;
        }

        @Override
        public <E extends Throwable> Either<A,B> orElseThrow(Supplier<? extends E> supplier) throws E {
            super.orElseThrow(supplier);
            return this;
        }

        @Override
        public Either<B,A> swap() {
            return left(getRight());
        }

        @Override
        public List<B> toList() {
            B element = getRight();
            List<B> result = element != null ? Collections.singletonList(element) : Collections.emptyList();
            return Collections.unmodifiableList(result);
        }

        @Override
        public Maybe<B> toMaybe() {
            return Maybe.of(getRight());
        }
    }


    final class Left<A,B> extends AbstractEither<A,B> {
        public Left(A left) {
            super(left, null);
        }

        @Override
        public boolean contains(B element) {
            return false;
        }

        @Override
        public boolean exists(final Predicate<? super B> predicate) {
            return false;
        }

        @Override
        public Maybe<Either<A,B>> filter(final Predicate<? super B> predicate) {
            super.filter(predicate);
            return Maybe.of(this);
        }

        @Override
        public <C> Either<A,C> flatMap(final Function<? super B,? extends Either<A,C>> mapper) {
            super.flatMap(mapper);
            return Generics.unchecked(this);
        }

        @Override
        public <C> Either<A,C> flatten() {
            return this.flatMap(r -> Generics.unchecked(this));
        }

        @Override
        public <C> C fold(final Function<? super A,? extends C> fa, final Function<? super B,? extends C> fb) {
            super.fold(fa,fb);
            return fa.apply(getLeft());
        }

        @Override
        public boolean forAll(final Predicate<? super B> predicate) {
            super.forAll(predicate);
            return true;
        }

        @Override
        public B getOrElse(final B other) {
            return other;
        }

        @Override
        public Iterator<B> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public <C> Either<A,C> map(final Function<? super B,? extends C> mapper) {
            super.map(mapper);
            return Generics.unchecked(this);
        }

        @Override
        public Either<A,B> orElse(final Either<? extends A,? extends B> other) {
            return Generics.unchecked(other);
        }

        @Override
        public Either<A,B> orElseGet(Supplier<? extends Either<? extends A,? extends B>> supplier) {
            super.orElseGet(supplier);
            return Generics.unchecked(supplier.get());
        }

        @Override
        public <E extends Throwable> Either<A,B> orElseThrow(Supplier<? extends E> supplier) throws E {
            super.orElseThrow(supplier);
            throw supplier.get();
        }

        @Override
        public Either<B,A> swap() {
            return right(getLeft());
        }

        @Override
        public List<B> toList() {
            return Collections.unmodifiableList(Collections.emptyList());
        }

        @Override
        public Maybe<B> toMaybe() {
            return Maybe.empty();
        }
    }
}
