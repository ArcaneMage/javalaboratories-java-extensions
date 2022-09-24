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
import org.javalaboratories.core.util.Arguments;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code Either} object represents one of two possible types (a disjoint
 * union).
 * <p>
 * It is commonly used to model both the "happy" and "exceptional" paths, but
 * most importantly, like the {@link java.util.Optional} and {@link Maybe}
 * objects, it is a {@code Monad}. Simply put, it implements both the {code map}
 * and {@code flatMap} methods to enable functional programming techniques.
 * <p>
 * Two implementations of the {@code Either} are provided, namely {@link Left}
 * and {@link Right} classes, and are instantiable via the following {@code
 * factory methods}: {@link Either#of(Object)}, {@link Either#left(Object)} and
 * {@link Either#right(Object)} methods.
 * <pre>
 *     {@code
 *          Either<Exception,Integer> randomise(int bound) {
 *              if (bound > 5) {    // Happy path :)
 *                  Random random = new Random();
 *                  return Either.right(random.nextInt(bound));
 *              } else {            // Exceptional path
 *                  return Either.left(new Exception("Insufficient bound: "+bound));
 *              }
 *          }
 *          ...
 *          ...
 *          Either<String,String> readFromDatabase(int identifier) {
 *              try {
 *                  return Either.right(readFromDatabase(identifier));
 *              } catch (IOException e) {
 *                  return Either.left(e.getMessage);
 *              }
 *          }
 *          ...
 *          ...
 *          String name = randomise(10)
 *              .flatMap(this::readFromDatabase)
 *              .fold(s -> "Error encountered: "+s, s -> "Random name from database: "+s);
 *          System.out.println(name);
 *     }
 * </pre>
 * The convention is that the {@link Left} object is considered the state of the
 * {@code exceptional} path; and the {@link Right} object is the state of the
 * {@code happy path} but the {@link Either} object will never have both states
 * encapsulated, hence the {@code disjointed union} concept. Furthermore, the
 * implementation provided is {@code right-biased}, that is to say operations like
 * {@link Either#map(Function)} and {@link Either#flatMap(Function)} methods are
 * applicable to the {@link Right} object, and that they will have no impact on the
 * the {@link Left} object: only the {@code left} value is returned.
 * <p>
 * Scala 2.13, Haskell and scholarly functional programming articles have provided
 * insights for the design of the {@link Either} classes.
 *
 * @param <A> Type of the left value.
 * @param <B> Type of the right value.
 * @see Left
 * @see Right
 */
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@Getter
public abstract class Either<A,B> extends Applicative<B> implements Monad<B>, Exportable<B>, Iterable<B>  {

    @Getter(value=AccessLevel.PACKAGE)
    private final A left;

    @Getter(value=AccessLevel.PACKAGE)
    private final B right;

    /**
     * Factory method to create an instance of {@link Right} implementation
     * of {@link Either}.
     * <p>
     * Convention is that the {@link Right} implementation encapsulates the
     * resultant value derived from a successful execution of logic/process.
     *
     * @param value right value to contain the resultant value of a process or
     *              logic.
     * @param <A> Type of the left value
     * @param <B> Type of the right value
     * @return an {@link Either} object with the {@link Right} implementation.
     * @see Either#right(Object)
     */
    public static <A,B> Either<A,B> of(final B value) {
        return right(value);
    }

    /**
     * Factory method to create an instance of the {@link Left} implementation
     * of {@link Either}
     * <p>
     * Often represents the state of the {@code erroneous} logic.
     *
     * @param value right value to contain the resultant value of a process or
     *              logic.
     * @param <A> Type of the left value
     * @param <B> Type of the right value
     * @return an {@link Either} object with the {@link Right} implementation.
     */
    public static <A,B> Either<A,B> left(final A value) {
        return new Left<>(value);
    }

    /**
     * Factory method to create an instance of {@link Right} implementation
     * of {@link Either}.
     * <p>
     * Convention is that the {@link Right} implementation encapsulates the
     * resultant value derived from a successful execution of logic/process.
     *
     * @param value right value to contain the resultant value of a process or
     *              logic.
     * @param <A> Type of the left value
     * @param <B> Type of the right value
     * @return an {@link Either} object with the {@link Right} implementation.
     */
    public static <A,B> Either<A,B> right(final B value) {
        return new Right<>(value);
    }

    /**
     * @return {@code true} is this conforms to the {@code Left} behaviour.
     */
    public abstract boolean isLeft();

    /**
     * @return {@code true} is this conforms to the {@code Right} behaviour.
     */
    public abstract boolean isRight();

    /**
     * Provides the ability to perform a sequence functorial computations on
     * the {@code applicable functor} container.
     *
     * @param applicative to apply computation.
     * @param <C> Type of value transformed having applied the function.
     * @return a new applicative with resultant value having applied the
     * encapsulated function.
     * @throws NullPointerException if function is null;
     */
    public <C> Either<A,C> apply(final Applicative<Function<? super B,? extends C>> applicative) {
        @SuppressWarnings("unchecked")
        Either<A,C> self = (Either<A,C>) this;
        return isLeft() ? self : (Either<A,C>) super.apply(applicative);
    }

    /**
     * Returns {@code true} if this is a {@link Right} implementation and the
     * {@code element} is {@code equal} to the {@code right} value, otherwise
     * {@code false} is returned.
     * <p>
     * @param element with which to perform equality test (resultant value of
     * {@code equals} method).
     *
     * @return {@code true} if {@code element} passes equality test.
     */
    public boolean contains(B element) {
        return !isLeft() && getRight().equals(element);
    }

    /**
     * Returns the resultant value of the executed {@link Predicate} function
     * or {@code false} if this is a {@link Left} implementation.
     * <p>
     * Default implementation is to validate existence of {@link Predicate}
     * function. Implementations should call this method first to enable
     * validation.
     * <p>
     * @param predicate the predicate function.
     * @return resultant value of {@link Predicate} or {@code false} if
     * this is a {@link Left} implementation.
     * @throws NullPointerException if no {@code predicate} is null.
     */
    public boolean exists(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return !isLeft() && predicate.test(getRight());
    }

    /**
     * Returns {@link Either} encapsulated in a {@link Maybe} object if the
     * {@link Predicate} holds {@code true} for the {@link Right} implementation,
     * otherwise an {@code empty} {@link Maybe} is returned.
     * <p>
     * In the case of the {@link Left} implementation, the {@code left} value
     * is returned in the {@code Maybe} object: the {@code predicate} has no
     * effect in this context.
     * <p>
     * Default implementation is to validate existence of {@link Predicate}
     * function. Implementations should call this method first to enable
     * validation.
     * <p>
     * @param predicate function to execute
     * @return maybe object containing possible {@code either} object.
     * @throws NullPointerException if no {@code predicate} is null.
     */
    public Maybe<Either<A,B>> filter(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return isLeft() ? Maybe.of(this) : exists(predicate) ? Maybe.of(this) : Maybe.empty();
    }

    /**
     * Returns {@code Right} {@link Either} with existing value of {@code Right}
     * if {@code this} is {@code Right} and satisfies the {@code predicate}.
     * <p>
     * {@code Left} {@code other} is returned if {@code Right} but does not
     * satisfy the {@code predicate}; current value of {@code Left} is returned
     * if {@code this} is a {@code Left}.
     * <p>
     * Default implementation is to validate existence of {@link Predicate}
     * function and {@code other} parameters. Implementations should call this
     * method first to enable validation.
     *
     * @param predicate criteria to test current value of {@link Right}
     * @param other to return if {@link Right} does not satisfy {@code predicate}
     * @return resultant {@link Either}.
     */
    public Either<A,B> filterOrElse(final Predicate<? super B> predicate, A other) {
        Arguments.requireNonNull(predicate, other);
        return isLeft() ? this : filter(predicate).isPresent() ? this : Either.left(other);
    }

    /**
     * The {@link Right} implementation performs a transformation by executing
     * the function {@code mapper} function.
     * <p>
     * In the case of the {@link Left} implementation, the {@code left} value
     * is returned: the {@code mapper} function has no effect in this context.
     * <p>
     * Default implementation is to validate existence of {@link Function}
     * object. Implementations should call this method first to enable
     * validation.
     * <p>
     * @param mapper function to execute
     * @param <D> Type of the right value (transformed)
     * @return the transformed {@link Either} object.
     * @throws NullPointerException if no {@code predicate} is null.
     */
    @Override
    public <D> Either<A,D> flatMap(final Function<? super B,? extends Monad<D>> mapper) {
        @SuppressWarnings("unchecked")
        Either<A,D> self = (Either<A,D>) this;
        return isLeft() ? self : (Either<A,D>) Monad.super.flatMap(mapper);
    }

    /**
     * The {@link Right} implementation performs a transformation by executing
     * the function {@code mapper} function.
     * <p>
     * In the case of the {@link Left} implementation, the {@code left} value
     * is returned: the {@code mapper} function has no effect in this context.
     * <p>
     * Default implementation is to validate existence of {@link Function}
     * object. Implementations should call this method first to enable
     * validation.
     * <p>
     * @param <D> Type of the right value (transformed)
     * @return the transformed {@link Either} object.
     * @throws NullPointerException if no {@code predicate} is null.
     */
    @Override
    public <D> Either<A,D> flatten() {
        @SuppressWarnings("unchecked")
        Either<A,D> self = (Either<A,D>) this;
        return isLeft() ? self : (Either<A,D>) Monad.super.flatten();
    }

    /**
     * Folds internal {@code values} as dictated by {@code fa} and {@code fb}
     * functions.
     * <p>
     * Default implementation is to validate existence of {@link Function}
     * object. Implementations should call this method first to enable
     * validation.
     *
     * @param fa applies function if this is a {@link Left} implementation.
     * @param fb applies function if this is a {@link Right} implementation.
     * @param <C> Type of returned value.
     * @return folded object returned from respective applied function.
     * @throws NullPointerException if either of the {@code function} parameters
     * is null.
     */
    public <C> C fold(final Function<? super A, ? extends C> fa, final Function<? super B, ? extends C> fb) {
        Arguments.requireNonNull(fa, fb);
        return isLeft() ? fa.apply(getLeft()) : fb.apply(getRight());
    }

    /**
     * For the {@link Right} implementation, returns the result from the
     * {@link Predicate} function.
     * <p>
     * In the case of the {@link Left} implementation, the {@code true} value
     * is returned: the {@code predicate} function has no effect in this
     * context.
     * <p>
     * Default implementation is to validate existence of {@link Predicate}
     * object. Implementations should call this method first to enable
     * validation.
     * <p>
     * @param predicate function to execute for the {@code Right} value.
     * @return the transformed {@link Either} object.
     * @throws NullPointerException if no {@code predicate} is null.
     */
    public boolean forAll(final Predicate<? super B> predicate) {
        Objects.requireNonNull(predicate);
        return isLeft() || exists(predicate);
    }

    /**
     * Returns the given {@link Right} value or the {@code other} for the
     * {@link Left} implementation.
     *
     * @param other alternative value for the {@link Left} implementation.
     * @return the given {@link Right} value or {@code other} for the
     * {@link Left} implementation.
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
     * Transforms the {@code right} value and returns a new {@link Either}
     * object that encapsulates it.
     * <p>
     * The {@code mapper} is applied only if this is a {@link Right}
     * implementation.
     * <p>
     * Default implementation is to validate existence of {@link Function}
     * object. Implementations should call this method first to enable
     * validation.
     *
     * @param mapper mapping function to apply to the {@code right} value.
     * @param <C> Type of transformed value.
     * @return a new {@link Either} object encapsulating transformed
     * {@code right} value.
     */
    @Override
    public <C> Either<A,C> map(final Function<? super B,? extends C> mapper) {
        @SuppressWarnings("unchecked")
        Either<A,C> self = (Either<A,C>) this;
        return isLeft() ? self : (Either<A,C>) super.<C>map(mapper);
    }

    /**
     * For {@link Right} implementation, returns this object or the given
     * {@code other} parameter if it's a {@link Left} implementation.
     * <p>
     * @param other alternative {@link Either} object returned for {@link Left}
     *              implementation.
     * @return current value of right-biased implementations.
     */
    public Either<A,B> orElse(final Either<? super A,? super B> other) {
        if (isLeft()) {
            @SuppressWarnings("unchecked")
            Either<A,B> result = (Either<A,B>) other;
            return result;
        } else {
            return this;
        }
    }

    /**
     * For {@link Right} implementation, returns this object or derives it from
     * the {@code supplier} function if it's a {@link Left} implementation.
     * <p>
     * Default implementation is to validate existence of {@link Supplier}
     * function. Implementations should call this method first to enable
     * validation.
     *
     * @param supplier function executed by {@link Left} implementation.
     * @return this {@code Right} or derives it from the {@code supplier}
     */
    public Either<A,B> orElseGet(final Supplier<? extends Either<? super A,? super B>> supplier) {
        Objects.requireNonNull(supplier);
        if (isLeft()) {
            @SuppressWarnings("unchecked")
            Either<A,B> result = (Either<A,B>) supplier.get();
            return result;
        } else {
            return this;
        }
    }

    /**
     * For {@link Right} implementation, returns this object or derives the {@code
     * exception} object from the supplier and throws it but only if it's the
     * {@link Left} implementation.
     * <p>
     * Default implementation is to validate existence of {@link Supplier}
     * function. Implementations should call this method first to enable
     * validation.
     * @param supplier function executed by {@link Left} implementation to derive
     *                 the {@code exception} object to throw.
     * @param <E> type of exception.
     * @return this {@code Right} or derives it from the {@code supplier}
     * @throws NullPointerException if no {@code predicate} is null.
     * @throws E type of exception.
     */
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
    public Either<A,B> peek(final Consumer<? super B> consumer) {
        return (Either<A,B>) Monad.super.peek(consumer);
    }

    /**
     * Swaps the internal values and therefore switching biases for this
     * {@link Either} object.
     * <p>
     * If this is a {@link Left} implementation, then left value in {@link
     * Right} and vice versa.
     * @return new swapped {@link Either} object.
     */
    public Either<B,A> swap() {
        return isLeft() ? Either.right(getLeft()) : Either.left(getRight());
    }

    /**
     * For {@link Right} implementation, returns an immutable list of the
     * {@code right} value, if available; {@link Left} implementations return
     * an empty list.
     * <p>
     * @return a collection of a single {@code right} value, if it exists.
     */
    @Override
    public List<B> toList() {
        B right = getRight();
        return isLeft()
                ? Collections.emptyList() : Collections.singletonList(right);
    }

    /**
     * For {@link Right} implementation, returns an immutable map of the
     * {@code right} value, if available; {@link Left} implementations return
     * an empty map.
     * <p>
     * @return a map of a single {@code right} value, if it exists.
     */
    @Override
    public <K> Map<K,B> toMap(final Function<? super B, ? extends K> keyMapper) {
        Objects.requireNonNull(keyMapper);
        B right = getRight();
        return isLeft()
                ? Collections.emptyMap() : Collections.singletonMap(keyMapper.apply(right),right);
    }

    /**
     * For {@link Right} implementation, returns a {@link Maybe} of the
     * {@code right} value, if available; {@link Left} implementations return an
     * empty {@link Maybe}.
     *
     * @return Maybe object encapsulating possible value.
     */
    public Maybe<B> toMaybe() {
        return isLeft() ? Maybe.empty() : Maybe.of(getRight());
    }

    /**
     * @return an immutable {@link Set} that represents {@code this} {@link
     * Either}. For {@link Left} instances, an empty Set is return; {@link Right}
     * instances, if the value exists, it will be encapsulated in the {@link Set}.
     */
    @Override
    public Set<B> toSet() {
        B right = getRight();
        return isLeft()
                ? Collections.emptySet() : Collections.singleton(right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return isRight() ? String.format("Right[%s]", getRight()) : String.format("Left[%s]", getLeft());
    }

    /**
     * Implements the {@link Either} interface, conforming to {@code Right}
     * biased behaviour.
     * <p>
     * Operations that perform transformation on the {@code right} value are
     * applicable to this implementation. For example, {@code map}, {@code
     * flatMap} are fully implemented.
     * <p>
     * Use the {@link Either#left(Object)} or {@link Either#right(Object)} to
     * create an instance of this object.
     * <p>
     * @param <A> Type of left value.
     * @param <B> Type of right value.
     */
    public final static class Right<A,B> extends Either<A,B> {
        /**
         * Constructs this {@link Either} object.
         * <p>
         * @param right value to be backed by this object.
         */
        public Right(B right) {
            super(null, right);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRight() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLeft() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <C> Right<A,C> pure(final C value) {
            return new Right<>(value);
        }
    }

    /**
     * Implements the {@link Either} interface, conforming to {@code Left}
     * behaviour.
     * <p>
     * Operations that perform transformation on the {@code right} value are
     * NOT applicable to this implementation. For example, {@code map}, {@code
     * flatMap} are fully implemented. Instead, this object is returned,
     * therefore such operations have no effect on the {@code left} value.
     * <p>
     * Use the {@link Either#left(Object)} or {@link Either#right(Object)} to
     * create an instance of this object.
     * <p>
     * @param <A> Type of left value.
     * @param <B> Type of right value.
     */
    public final static class Left<A,B> extends Either<A,B> {
        /**
         * Constructs this {@link Either} object.
         * <p>
         * @param left value to be backed by this object.
         */
        public Left(A left) {
            super(left, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRight() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLeft() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <C> Left<A,C> pure(final C value) {
            return new Left<>(getLeft());
        }
    }
}