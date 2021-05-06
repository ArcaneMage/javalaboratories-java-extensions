package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Maybe;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.javalaboratories.core.tuple.Tuple.of;


/**
 * A tuple with depth of 2
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 *
 * @author Kevin Henry
 */
public final class Tuple2<T1,T2> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        super(t1,t2);
        this.t1 = t1;
        this.t2 = t2;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 2 from iterable object. If there is
     * insufficient elements, then {@link Maybe} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Maybe} object container.
     */
    public static <T> Maybe<Tuple2<T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 2);
    }

    public T1 value1() {
        return t1;
    }

    public T2 value2() {
        return t2;
    }

    /**
     * Add value at position 1
     */
    public <T> Tuple3<T,T1,T2> addAt1(T value) {
        return add(1,value);
    }

    /**
     * Add value at position 2
     */
    public <T> Tuple3<T1,T,T2> addAt2(T value) {
        return add(2,value);
    }

    /**
     * @return {@link Pair} object representing this {@link Tuple}
     */
    public Pair<T1,T2> asPair() {
        return new Pair<T1,T2>() {
            @Override
            public T1 _1() {
                return t1;
            }
            @Override
            public T2 _2() {
                return t2;
            }
            @Override
            public String toString() {
                return String.format("Pair[%s,%s]",_1(),_2());
            }
        };
    }

    /**
     * Hop to position/value 1
     */
    public Tuple2<T1,T2> hopTo1() {
        return hop(1);
    }

    /**
     * Hop to position/value 2
     */
    public Tuple1<T2> hopTo2() {
        return hop(2);
    }

    /**
     * Joins a tuple to this tuple.
     * @param value a tuple object.
     */
    public <T> Tuple3<T1,T2,T> join(T value) {
        return join(of(value));
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public Tuple2<T1,T2> join(Tuple0 tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3> Tuple3<T1,T2,T3> join(Tuple1<T3> tuple) {
        return super.join(tuple);
    }


    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4> Tuple4<T1,T2,T3,T4> join(Tuple2<T3,T4> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> join(Tuple3<T3,T4,T5> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> join(Tuple4<T3,T4,T5,T6> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> join(Tuple5<T3,T4,T5,T6,T7> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8> Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> join(Tuple6<T3,T4,T5,T6,T7,T8> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> join(Tuple7<T3,T4,T5,T6,T7,T8,T9> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple8<T3,T4,T5,T6,T7,T8,T9,T10> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple9<T3,T4,T5,T6,T7,T8,T9,T10,T11> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple10<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple11<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple12<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple13<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple14<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> tuple) {
        return super.join(tuple);
    }

    /**
     * Splices at positions 1
     */
    public Tuple2<Tuple0,Tuple2<T1,T2>> spliceAt1() {
        return splice(1);
    }

    /**
     * Splices at positions 2
     */
    public Tuple2<Tuple1<T1>,Tuple1<T2>> spliceAt2() {
        return splice(2);
    }

    /**
     * Transform this tuple into another object
     * @param function function that performs the transformation
     * @param <R> return type of transformation
     * @return transformed object.
     */
    public <R> R map(BiFunction<? super T1,? super T2,? extends R> function) {
        return function.apply(t1,t2);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple2<R,T2> mapAt1(Function<? super T1,? extends R> function) {
        return new Tuple2<>(function.apply(t1),t2);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple2<T1,R> mapAt2(Function<? super T2,? extends R> function) {
        return new Tuple2<>(t1,function.apply(t2));
    }

    /**
     * Tests whether given {@code tuple} is equal to this {@code tuple}, and if
     * true, the {@code consumer} function is executed.
     * <p>
     * This is particularly useful if the tuple contents are unknown and all
     * discovered, the {@code consumer} function is performed.
     * <pre>
     * {@code
     *      tuple
     *        .match(all("John","Wellington"), (a,b) -> System.out.println(a))
     *        .match(all("Alex","Wall",23), (a,b) -> System.out.println(b))
     * }
     * </pre>
     * @param matcher object to use with this tuple.
     * @param consumer function to execute if match is found.
     * @param <Q> type of matcher.
     * @return this tuple -- useful for multiple matches.
     */
    public <Q extends Matcher> Tuple2<T1,T2> match(final Q matcher, final BiConsumer<? super T1,? super T2> consumer) {
        Objects.requireNonNull(consumer);
        Tuple2<T1,T2> result = this;
        if (matcher.match(this))
            consumer.accept(t1,t2);

        return result;
    }

    /**
     * Remove element at position 1
     */
    public Tuple1<T2> removeAt1() {
        return remove(1);
    }

    /**
     * Remove element at position 2
     */
    public Tuple1<T1> removeAt2() {
        return remove(2);
    }

    /**
     * Rotates this tuple 1 times to the right
     */
    public Tuple2<T2,T1> rotateRight1() {
        return rotateRight(1);
    }

    /**
     * Rotates this tuple 1 time to the left
     */
    public Tuple2<T2,T1> rotateLeft1() {
        return rotateLeft(1);
    }

    /**
     * Truncates tuples at position 1
     */
    public Tuple0 truncateAt1() {
        return truncate(1);
    }

    /**
     * Truncates tuples at position 2
     */
    public Tuple1<T1> truncateAt2() {
        return truncate(2);
    }
}
