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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.javalaboratories.util.Arguments;
import org.javalaboratories.util.Generics;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EqualsAndHashCode
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
abstract class AbstractEither<A,B> implements Either<A,B> {
    private final A left;
    private final B right;

    /**
     * @return the left {@code value} in this {@code Either} container.
     */
    A getLeft() {
        return left;
    }

    /**
     * @return the right {@code value} in this {@code Either} container.
     */
    B getRight() {
        return right;
    }

    /**
     * Creates an instance of an Either with a {@code left} value.
     * <p>
     * The {@code right} value will be null.
     *
     * @param value to be contained in the {@code Either} container.
     * @param <C> Type of left value.
     * @param <D> Type of right value.
     * @return an instance of an {@code Either} implementation.
     */
    abstract <C,D> Either<C,D> newLeft(final C value);

    /**
     * Creates an instance of an Either with a {@code right} value.
     * <p>
     * The {@code left} value will be null.
     *
     * @param value to be contained in the {@code Either} container.
     * @param <C> Type of left value.
     * @param <D> Type of right value.
     * @return an instance of an {@code Either} implementation.
     */
    abstract <C,D> Either<C,D> newRight(final D value);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(B element) {
        return !isLeft() && getRight().equals(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return !isLeft() && predicate.test(getRight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maybe<Either<A,B>> filter(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return isLeft() ? Maybe.of(this) : exists(predicate) ? Maybe.of(this) : Maybe.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<A,B> filterOrElse(final Predicate<? super B> predicate, A other) {
        Arguments.requireNonNull(predicate, other);
        return isLeft() ? this : filter(predicate).isPresent() ? this : newLeft(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> C fold(final Function<? super A, ? extends C> fa, final Function<? super B, ? extends C> fb) {
        Arguments.requireNonNull(fa, fb);
        return isLeft() ? fa.apply(getLeft()) : fb.apply(getRight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean forAll(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return isLeft() || exists(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public B getOrElse(final B other) {
        return isLeft() ? other : getRight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<B> iterator() {
        return toList().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> Either<A,C> map(final Function<? super B, ? extends C> mapper) {
        Objects.requireNonNull(mapper);
        Either<A,C> self = Generics.unchecked(this);
        return isLeft() ? self : newRight(Objects.requireNonNull(mapper.apply(getRight())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<A,B> orElse(final Either<? super A,? super B> other) {
        return isLeft() ? Generics.unchecked(other) : this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<A,B> orElseGet(final Supplier<? extends Either<? super A,? super B>> supplier) {
        Objects.requireNonNull(supplier);
        return isLeft() ? Generics.unchecked(supplier.get()) : this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E extends Throwable> Either<A,B> orElseThrow(final Supplier<? extends E> supplier) throws E {
        Objects.requireNonNull(supplier);
        if (isRight())
            return this;
        throw supplier.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<B,A> swap() {
        return isLeft() ? newRight(getLeft()) : newLeft(getRight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<B> toList() {
        B right = getRight();
        return isLeft()
                ? Collections.unmodifiableList(Collections.emptyList()) : right != null
                  ? Collections.singletonList(right) : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> Map<K,B> toMap(final Function<? super B, ? extends K> keyMapper) {
        Objects.requireNonNull(keyMapper);
        B right = getRight();
        return isLeft()
                ? Collections.emptyMap() : right != null
                  ? Collections.singletonMap(keyMapper.apply(right),right) : Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maybe<B> toMaybe() {
        return isLeft() ? Maybe.empty() : Maybe.of(getRight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return isRight() ? String.format("Right[%s]", getRight()) : String.format("Left[%s]", getLeft());
    }
}