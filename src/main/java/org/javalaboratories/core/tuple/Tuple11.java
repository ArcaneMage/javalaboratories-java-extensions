package org.javalaboratories.core.tuple;


import org.javalaboratories.core.Nullable;
import org.javalaboratories.core.function.Consumer11;
import org.javalaboratories.core.function.Function11;

import java.util.Objects;
import java.util.function.Function;

import static org.javalaboratories.core.tuple.Tuple.*;

/**
 * A tuple with depth of 11
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 * @param <T4> type of 4th element
 * @param <T5> type of 5th element
 * @param <T6> type of 6th element
 * @param <T7> type of 7th element
 * @param <T8> type of 8th element
 * @param <T9> type of 9th element
 * @param <T10> type of 10th element
 * @param <T11> type of 11th element
 *
 * @author Kevin Henry
 */
public final class Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;
    private final T7 t7;
    private final T8 t8;
    private final T9 t9;
    private final T10 t10;
    private final T11 t11;

    public Tuple11(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        super(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
        this.t9 = t9;
        this.t10 = t10;
        this.t11 = t11;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 11 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple11<T,T,T,T,T,T,T,T,T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 11);
    }
    
    public T1 value1() {
        return t1;
    }

    public T2 value2() {
        return t2;
    }

    public T3 value3() {
        return t3;
    }

    public T4 value4() {
        return t4;
    }

    public T5 value5() {
        return t5;
    }

    public T6 value6() {
        return t6;
    }

    public T7 value7() {
        return t7;
    }

    public T8 value8() {
        return t8;
    }

    public T9 value9() {
        return t9;
    }

    public T10 value10() {
        return t10;
    }

    public T11 value11() {
        return t11;
    }

    /**
     * Add value at position 1
     */
    public <T> Tuple12<T,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> addAt1(T value) {
        return add(1,value);
    }

    /**
     * Add value at position 2
     */
    public <T> Tuple12<T1,T,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> addAt2(T value) {
        return add(2,value);
    }

    /**
     * Add value at position 3
     */
    public <T> Tuple12<T1,T2,T,T3,T4,T5,T6,T7,T8,T9,T10,T11> addAt3(T value) {
        return add(3,value);
    }

    /**
     * Add value at position 4
     */
    public <T> Tuple12<T1,T2,T3,T,T4,T5,T6,T7,T8,T9,T10,T11> addAt4(T value) {
        return add(4,value);
    }

    /**
     * Add value at position 5
     */
    public <T> Tuple12<T1,T2,T3,T4,T,T5,T6,T7,T8,T9,T10,T11> addAt5(T value) {
        return add(5,value);
    }

    /**
     * Add value at position 6
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T,T6,T7,T8,T9,T10,T11> addAt6(T value) {
        return add(6,value);
    }

    /**
     * Add value at position 7
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T,T7,T8,T9,T10,T11> addAt7(T value) {
        return add(7,value);
    }

    /**
     * Add value at position 8
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T7,T,T8,T9,T10,T11> addAt8(T value) {
        return add(8,value);
    }

    /**
     * Add value at position 9
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T,T9,T10,T11> addAt9(T value) {
        return add(9,value);
    }

    /**
     * Add value at position 10
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T,T10,T11> addAt10(T value) {
        return add(10,value);
    }

    /**
     * Add value at position 11
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T,T11> addAt11(T value) {
        return add(11,value);
    }

    /**
     * Hop to position/value 1
     */
    public Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> hopTo1() {
        return hop(1);
    }

    /**
     * Hop to position/value 2
     */
    public Tuple10<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> hopTo2() {
        return hop(2);
    }

    /**
     * Hop to position/value 3
     */
    public Tuple9<T3,T4,T5,T6,T7,T8,T9,T10,T11> hopTo3() {
        return hop(3);
    }

    /**
     * Hop to position/value 4
     */
    public Tuple8<T4,T5,T6,T7,T8,T9,T10,T11> hopTo4() {
        return hop(4);
    }

    /**
     * Hop to position/value 5
     */
    public Tuple7<T5,T6,T7,T8,T9,T10,T11> hopTo5() {
        return hop(5);
    }

    /**
     * Hop to position/value 6
     */
    public Tuple6<T6,T7,T8,T9,T10,T11> hopTo6() {
        return hop(6);
    }

    /**
     * Hop to position/value 7
     */
    public Tuple5<T7,T8,T9,T10,T11> hopTo7() {
        return hop(7);
    }

    /**
     * Hop to position/value 8
     */
    public Tuple4<T8,T9,T10,T11> hopTo8() {
        return hop(8);
    }

    /**
     * Hop to position/value 9
     */
    public Tuple3<T9,T10,T11> hopTo9() {
        return hop(9);
    }

    /**
     * Hop to position/value 10
     */
    public Tuple2<T10,T11> hopTo10() {
        return hop(10);
    }

    /**
     * Hop to position/value 11
     */
    public Tuple1<T11> hopTo11() {
        return hop(11);
    }

    /**
     * Joins a tuple to this tuple.
     * @param value a tuple object.
     */
    public <T> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T> join(T value) {
        return join(of(value));
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple0 tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple1<T12> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple2<T12,T13> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple3<T12,T13,T14> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple4<T12,T13,T14,T15> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple5<T12,T13,T14,T15,T16> tuple) {
        return super.join(tuple);
    }

    /**
     * Splices at positions 1
     */
    public Tuple2<Tuple0,Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> spliceAt1() {
        return splice(1);
    }

    /**
     * Splices at positions 2
     */
    public Tuple2<Tuple1<T1>,Tuple10<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> spliceAt2() {
        return splice(2);
    }

    /**
     * Splices at positions 3
     */
    public Tuple2<Tuple2<T1,T2>,Tuple9<T3,T4,T5,T6,T7,T8,T9,T10,T11>> spliceAt3() {
        return splice(3);
    }

    /**
     * Splices at positions 4
     */
    public Tuple2<Tuple3<T1,T2,T3>,Tuple8<T4,T5,T6,T7,T8,T9,T10,T11>> spliceAt4() {
        return splice(4);
    }

    /**
     * Splices at positions 5
     */
    public Tuple2<Tuple4<T1,T2,T3,T4>,Tuple7<T5,T6,T7,T8,T9,T10,T11>> spliceAt5() {
        return splice(5);
    }

    /**
     * Splices at positions 6
     */
    public Tuple2<Tuple5<T1,T2,T3,T4,T5>,Tuple6<T6,T7,T8,T9,T10,T11>> spliceAt6() {
        return splice(6);
    }

    /**
     * Splices at positions 7
     */
    public Tuple2<Tuple6<T1,T2,T3,T4,T5,T6>,Tuple5<T7,T8,T9,T10,T11>> spliceAt7() {
        return splice(7);
    }

    /**
     * Splices at positions 8
     */
    public Tuple2<Tuple7<T1,T2,T3,T4,T5,T6,T7>,Tuple4<T8,T9,T10,T11>> spliceAt8() {
        return splice(8);
    }

    /**
     * Splices at positions 9
     */
    public Tuple2<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>,Tuple3<T9,T10,T11>> spliceAt9() {
        return splice(9);
    }

    /**
     * Splices at positions 10
     */
    public Tuple2<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>,Tuple2<T10,T11>> spliceAt10() {
        return splice(10);
    }

    /**
     * Splices at positions 11
     */
    public Tuple2<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>,Tuple1<T11>> spliceAt11() {
        return splice(11);
    }

    /**
     * Transform this tuple into another object.
     * @param function performs the transformation.
     * @param <R> return tye of the transformed element
     * @return resultant object transformed by this map function.
     */
    public <R> R map(Function11<? super T1,? super T2,? super T3,? super T4,? super T5,? super T6,? super T7,? super T8,? super T9,? super T10,? super T11,? extends R> function) {
        return function.apply(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<R,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> mapAt1(Function<? super T1,? extends R> function) {
        return new Tuple11<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,R,T3,T4,T5,T6,T7,T8,T9,T10,T11> mapAt2(Function<? super T2,? extends R> function) {
        return new Tuple11<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,R,T4,T5,T6,T7,T8,T9,T10,T11> mapAt3(Function<? super T3,? extends R> function) {
        return new Tuple11<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,R,T5,T6,T7,T8,T9,T10,T11> mapAt4(Function<? super T4,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,R,T6,T7,T8,T9,T10,T11> mapAt5(Function<? super T5,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,R,T7,T8,T9,T10,T11> mapAt6(Function<? super T6,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,T6,R,T8,T9,T10,T11> mapAt7(Function<? super T7,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,T6,T7,R,T9,T10,T11> mapAt8(Function<? super T8,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8),t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,R,T10,T11> mapAt9(Function<? super T9,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,function.apply(t9),t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,R,T11> mapAt10(Function<? super T10,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,function.apply(t10),t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R> mapAt11(Function<? super T11,? extends R> function) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,function.apply(t11));
    }

    /**
     * Tests whether given {@code tuple} is equal to this {@code tuple}, and if
     * true, the {@code consumer} function is executed.
     * <p>
     * This is particularly useful if the tuple contents are unknown and when
     * discovered, the {@code consumer} function is performed.
     * <pre>
     * {@code
     *      tuple
     *        .match(when("John","Wellington"), (a,b,c,d,e,f,g,h,i,j,k,l) -> System.out.println(a))
     *        .match(when("Alex","Wall",23), (a,b,c,d,e,f,g,h,i,j,k,l) -> System.out.println(b))
     * }
     * </pre>
     * @param matcher object to use with this tuple.
     * @param consumer function to execute if match is found.
     * @param <Q> type of matcher.
     * @return this tuple -- useful for multiple matches.
     */
    public <Q extends Matcher> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> match(final Q matcher, final Consumer11<? super T1,? super T2,? super T3,? super T4,? super T5,? super T6,? super T7,? super T8,? super T9,? super T10,? super T11> consumer) {
        Objects.requireNonNull(consumer);
        Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> result = this;
        if (matcher.match(this))
            consumer.accept(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);

        return result;
    }

    /**
     * Remove element at position 1
     */
    public Tuple10<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> removeAt1() {
        return remove(1);
    }

    /**
     * Remove element at position 2
     */
    public Tuple10<T1,T3,T4,T5,T6,T7,T8,T9,T10,T11> removeAt2() {
        return remove(2);
    }

    /**
     * Remove element at position 3
     */
    public Tuple10<T1,T2,T4,T5,T6,T7,T8,T9,T10,T11> removeAt3() {
        return remove(3);
    }

    /**
     * Remove element at position 4
     */
    public Tuple10<T1,T2,T3,T5,T6,T7,T8,T9,T10,T11> removeAt4() {
        return remove(4);
    }

    /**
     * Remove element at position 5
     */
    public Tuple10<T1,T2,T3,T4,T6,T7,T8,T9,T10,T11> removeAt5() {
        return remove(5);
    }

    /**
     * Remove element at position 6
     */
    public Tuple10<T1,T2,T3,T4,T5,T7,T8,T9,T10,T11> removeAt6() {
        return remove(6);
    }

    /**
     * Remove element at position 7
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T8,T9,T10,T11> removeAt7() {
        return remove(7);
    }

    /**
     * Remove element at position 8
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T9,T10,T11> removeAt8() {
        return remove(8);
    }

    /**
     * Remove element at position 9
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T10,T11> removeAt9() {
        return remove(9);
    }

    /**
     * Remove element at position 10
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T11> removeAt10() {
        return remove(10);
    }

    /**
     * Remove element at position 11
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> removeAt11() {
        return remove(11);
    }

    /**
     * Rotates this tuple 1 time to the right
     */
    public Tuple11<T11,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> rotateRight1() {
        return rotateRight(1);
    }

    /**
     * Rotates this tuple 2 times to the right
     */
    public Tuple11<T10,T11,T1,T2,T3,T4,T5,T6,T7,T8,T9> rotateRight2() {
        return rotateRight(2);
    }

    /**
     * Rotates this tuple 3 times to the right
     */
    public Tuple11<T9,T10,T11,T1,T2,T3,T4,T5,T6,T7,T8> rotateRight3() {
        return rotateRight(3);
    }

    /**
     * Rotates this tuple 4 times to the right
     */
    public Tuple11<T8,T9,T10,T11,T1,T2,T3,T4,T5,T6,T7> rotateRight4() {
        return rotateRight(4);
    }

    /**
     * Rotates this tuple 5 times to the right
     */
    public Tuple11<T7,T8,T9,T10,T11,T1,T2,T3,T4,T5,T6> rotateRight5() {
        return rotateRight(5);
    }

    /**
     * Rotates this tuple 6 times to the right
     */
    public Tuple11<T6,T7,T8,T9,T10,T11,T1,T2,T3,T4,T5> rotateRight6() {
        return rotateRight(6);
    }

    /**
     * Rotates this tuple 7 times to the right
     */
    public Tuple11<T5,T6,T7,T8,T9,T10,T11,T1,T2,T3,T4> rotateRight7() {
        return rotateRight(7);
    }

    /**
     * Rotates this tuple 8 times to the right
     */
    public Tuple11<T4,T5,T6,T7,T8,T9,T10,T11,T1,T2,T3> rotateRight8() {
        return rotateRight(8);
    }

    /**
     * Rotates this tuple 9 times to the right
     */
    public Tuple11<T3,T4,T5,T6,T7,T8,T9,T10,T11,T1,T2> rotateRight9() {
        return rotateRight(9);
    }

    /**
     * Rotates this tuple 10 times to the right
     */
    public Tuple11<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T1> rotateRight10() {
        return rotateRight(10);
    }

    /**
     * Rotates this tuple 1 time to the left
     */
    public Tuple11<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T1> rotateLeft1() {
        return rotateLeft(1);
    }

    /**
     * Rotates this tuple 2 times to the left
     */
    public Tuple11<T3,T4,T5,T6,T7,T8,T9,T10,T11,T1,T2> rotateLeft2() {
        return rotateLeft(2);
    }

    /**
     * Rotates this tuple 3 times to the left
     */
    public Tuple11<T4,T5,T6,T7,T8,T9,T10,T11,T1,T2,T3> rotateLeft3() {
        return rotateLeft(3);
    }

    /**
     * Rotates this tuple 4 times to the left
     */
    public Tuple11<T5,T6,T7,T8,T9,T10,T11,T1,T2,T3,T4> rotateLeft4() {
        return rotateLeft(4);
    }

    /**
     * Rotates this tuple 5 times to the left
     */
    public Tuple11<T6,T7,T8,T9,T10,T11,T1,T2,T3,T4,T5> rotateLeft5() {
        return rotateLeft(5);
    }

    /**
     * Rotates this tuple 6 times to the left
     */
    public Tuple11<T7,T8,T9,T10,T11,T1,T2,T3,T4,T5,T6> rotateLeft6() {
        return rotateLeft(6);
    }

    /**
     * Rotates this tuple 7 times to the left
     */
    public Tuple11<T8,T9,T10,T11,T1,T2,T3,T4,T5,T6,T7> rotateLeft7() {
        return rotateLeft(7);
    }

    /**
     * Rotates this tuple 8 times to the left
     */
    public Tuple11<T9,T10,T11,T1,T2,T3,T4,T5,T6,T7,T8> rotateLeft8() {
        return rotateLeft(8);
    }

    /**
     * Rotates this tuple 9 times to the left
     */
    public Tuple11<T10,T11,T1,T2,T3,T4,T5,T6,T7,T8,T9> rotateLeft9() {
        return rotateLeft(9);
    }

    /**
     * Rotates this tuple 10 times to the left
     */
    public Tuple11<T11,T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> rotateLeft10() {
        return rotateLeft(10);
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

    /**
     * Truncates tuples at position 3
     */
    public Tuple2<T1,T2> truncateAt3() {
        return truncate(3);
    }

    /**
     * Truncates tuples at position 4
     */
    public Tuple3<T1,T2,T3> truncateAt4() {
        return truncate(4);
    }

    /**
     * Truncates tuples at position 5
     */
    public Tuple4<T1,T2,T3,T4> truncateAt5() {
        return truncate(5);
    }

    /**
     * Truncates tuples at position 6
     */
    public Tuple5<T1,T2,T3,T4,T5> truncateAt6() {
        return truncate(6);
    }

    /**
     * Truncates tuples at position 7
     */
    public Tuple6<T1,T2,T3,T4,T5,T6> truncateAt7() {
        return truncate(7);
    }

    /**
     * Truncates tuples at position 8
     */
    public Tuple7<T1,T2,T3,T4,T5,T6,T7> truncateAt8() {
        return truncate(8);
    }

    /**
     * Truncates tuples at position 9
     */
    public Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> truncateAt9() {
        return truncate(9);
    }

    /**
     * Truncates tuples at position 10
     */
    public Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> truncateAt10() {
        return truncate(10);
    }

    /**
     * Truncates tuples at position 11
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> truncateAt11() {
        return truncate(11);
    }
}
