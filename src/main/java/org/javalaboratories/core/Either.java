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

import org.javalaboratories.core.tuple.Pair;
import org.javalaboratories.util.Arguments;
import org.javalaboratories.util.Generics;

import java.util.*;
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
@SuppressWarnings("UnusedReturnValue")
public interface Either<A,B> extends Monad<B>, Iterable<B> {

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
    static <A,B> Either<A,B> of(final B value) {
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
    static <A,B> Either<A,B> left(final A value) {
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
    static <A,B> Either<A,B> right(final B value) {
        return new Right<>(value);
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
    boolean contains(B element);

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
    boolean exists(final Predicate<? super B> predicate);

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
    Maybe<Either<A,B>> filter(final Predicate<? super B> predicate);

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
    Either<A,B> filterOrElse(final Predicate<? super B> predicate, final A other);

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
    default <D> Either<A,D> flatMap(final Function<? super B,? extends Monad<D>> mapper) {
        Either<A,D> self = Generics.unchecked(this);
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
    default <D> Either<A,D> flatten() {
        Either<A,D> self = Generics.unchecked(this);
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
    <C> C fold(final Function<? super A,? extends C> fa, final Function<? super B,? extends C> fb);

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
    boolean forAll(final Predicate<? super B> predicate);

    /**
     * Returns the given {@link Right} value or the {@code other} for the
     * {@link Left} implementation.
     *
     * @param other alternative value for the {@link Left} implementation.
     * @return the given {@link Right} value or {@code other} for the
     * {@link Left} implementation.
     */
    B getOrElse(final B other);

    /**
     * @return {@code true} is this conforms to the {@code Left} behaviour.
     */
    boolean isLeft();

    /**
     * @return {@code true} is this conforms to the {@code Right} behaviour.
     */
    boolean isRight();

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
    <C> Either<A,C> map(final Function<? super B,? extends C> mapper);

    /**
     * For {@link Right} implementation, returns this object or the given
     * {@code other} parameter if it's a {@link Left} implementation.
     * <p>
     * @param other alternative {@link Either} object returned for {@link Left}
     *              implementation.
     * @return current value of right-biased implementations.
     */
    Either<A,B> orElse(final Either<? super A,? super B> other);

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
    Either<A,B> orElseGet(Supplier<? extends Either<? super A,? super B>> supplier);

    /**
     * For {@link Right} implementation, returns this object or derives the {@code
     * exception} object from the supplier and throws it but only if it's the
     * {@link Left} implementation.
     * <p>
     * Default implementation is to validate existence of {@link Supplier}
     * function. Implementations should call this method first to enable
     * validation.
     *
     * @param supplier function executed by {@link Left} implementation to derive
     *                 the {@code exception} object to throw.
     * @return this {@code Right} or derives it from the {@code supplier}
     * @throws NullPointerException if no {@code predicate} is null.
     */
    <E extends Throwable> Either<A,B> orElseThrow(Supplier<? extends E> supplier) throws E;

    /**
     * {@inheritDoc}
     */
    @Override
    default Either<A,B> peek(final Consumer<? super B> consumer) {
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
    Either<B,A> swap();

    /**
     * For {@link Right} implementation, returns an immutable list of the
     * {@code right} value, if available; {@link Left} implementations return
     * an empty list.
     * <p>
     * @return a collection of a single {@code right} value, if it exists.
     */
    List<B> toList();

    /**
     * For {@link Right} implementation, returns an immutable map of the
     * {@code right} value, if available; {@link Left} implementations return
     * an empty map.
     * <p>
     * @return a map of a single {@code right} value, if it exists.
     */
    <K> Map<K,B> toMap(final Function<? super B, ? extends K> keyMapper);

    /**
     * For {@link Right} implementation, returns a {@link Maybe} of the
     * {@code right} value, if available; {@link Left} implementations return an
     * empty {@link Maybe}.
     *
     * @return Maybe object encapsulating possible value.
     */
    Maybe<B> toMaybe();

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
    final class Right<A,B> extends AbstractEither<A,B> {

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
        <C,D> Either<C,D> newLeft(C value) {
            return new Left<>(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        <C,D> Either<C,D> newRight(D value) {
            return new Right<>(value);
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
    final class Left<A,B> extends AbstractEither<A,B> {

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
        <C,D> Either<C,D> newLeft(C value) {
            return new Left<>(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        <C,D> Either<C,D> newRight(D value) {
            return new Right<>(value);
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
    }
}