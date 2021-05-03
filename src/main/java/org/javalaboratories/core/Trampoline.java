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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Function interface supports stack-safe recursion of methods.
 * <p>
 * This is essentially the {@code Trampoline} design pattern. Simply put, it
 * provides {@code recursion} of a method without running out of stack memory.
 * The {@code pattern} supports {@code Tail Trampoline}, providing two methods,
 * one for the termination condition and the other the {@code tail call}.
 * <ol>
 *     <li>Trampoline.finish(T result)</li>
 *     <li>Trampoline.more(function)</li>
 * </ol>
 * Review the following code snippet which illustrates the typical use case
 * of the {@link Trampoline} interface.
 * <pre>
 *     {@code
 *         ...
 *         ...
 *         public Trampoline<Integer> sum(int first, int last) {
 *           if (first == last)
 *               return Trampoline.finish(last);
 *           else {
 *               return Trampoline.more(() -> sum(first + 1,last));
 *           }
 *         }
 *         ...
 *         ...
 *         System.out.println(sum(1,10).result());
 *     }
 * </pre>
 * @param <T> Type of resultant value from recursive function.
 */
@FunctionalInterface
public interface Trampoline<T> {

    /**
     * @return {@code value} from {@code this} {@link Trampoline} object.
     */
    T get();

    /**
     * Provides the next {@code function} to recurse (to call).
     * <p>
     * The default implementation is return {@code this} instance.
     *
     * @return next function to call.
     */
    default Trampoline<T> next() {
        return this;
    }

    /**
     * Returns the {@code result}, the resultant value of the {@code function}.
     * <p>
     * The default implementation is to return {@code this} implementation.
     *
     * @return next function to call.
     */
    default T result() {
        return get();
    }

    /**
     * Returns {@code true} to indicate that the current function has finished
     * {@code recursing.}
     * <p>
     * The default implementation is to return {@code true}.
     *
     * @return {@code true} if recursion has concluded otherwise {@code false}.
     */
    default boolean done() {
        return true;
    }

    default <U> Trampoline<U> map(final Function<? super T, ? extends U> mapper) {
        return Trampoline.finish(mapper.apply(result()));
    }

    default <U> Trampoline<U> flatMap(final Function<? super T, ? extends Trampoline<U>> mapper) {
        return mapper.apply(result());
    }

    /**
     * Call this method to return the final value and terminate the {@code
     * recursion}.
     *
     * @param result the resu, ltant computed {@code value} of the function.
     * @param <T> Type of resultant value.
     * @return a new {@link Trampoline} implementation containing the resultant
     * value.
     */
    static <T> Trampoline<T> finish(final T result) {
        return () -> result;
    }

    /**
     * Call this method to create an implementation of {@link Trampoline}
     * function that will lazily execute the lambda {@code function} until it
     * encounters the final {@link Trampoline} implementation (Trampoline.finish()).
     * <p>
     * Trampoline is performed with a loop, thus ensuring stack-safety.
     *
     * @param function recursive function to execute.
     * @param <T> Type of resultant value from function.
     * @return the final function implementation with the resultant value.
     */
    static <T> Trampoline<T> more(final Trampoline<Trampoline<T>> function) {
        Objects.requireNonNull(function,"Expected recursive function");
        return new Trampoline<T>() {
            @Override
            public boolean done() {
                return false;
            }
            @Override
            public <U> Trampoline<U> map(final Function<? super T, ? extends U> mapper) {
                return reduce(value -> value.map(mapper));
            }
            @Override
            public Trampoline<T> next() {
                return function.result();
            }
            @Override
            public T get() {
                return iterate(this);
            }
            @Override
            public <U> Trampoline<U> flatMap(final Function<? super T, ? extends Trampoline<U>> mapper) {
                return reduce(value -> value.flatMap(mapper));
            }
            public T iterate(final Trampoline<T> function) {
                Trampoline<T> f = function;
                while(!f.done()) {
                    f = f.next();
                }
                return f.result();
            }
        };
    }

    default <R> R reduce(final Function<? super Trampoline<T>, ? extends R> more) {
        return more.apply(next());
    }
}
