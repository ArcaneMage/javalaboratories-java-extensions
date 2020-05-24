package org.javalaboratories.core.tuple;

import java.util.function.Consumer;

/**
 * Matcher is a special kind of tuple container that has pattern matching
 * capabilities.
 * <p>
 * The {@link Matcher#match(Tuple)} is used to test whether the tuple's
 * elements match the encapsulated pattern, returning (@code true) to indicate
 * the pattern's elements and/or regular expression match the tuple.
 * <p>
 * It is possible to match specific elements in a tuple against pattern elements;
 * in the case of strings, they are matched with regular expressions declared in the
 * pattern. For example, {@link Matcher} has the following pattern
 * {@code ["^John$",43]} will match a tuple with the elements {@code ["John",43]}.
 * String elements in the pattern are regular expressions, all other types are matched
 * with the {@code equals} method. A match is defined when all elements of the
 * {@link Matcher} match against the corresponding {@link Tuple} elements.
 * <p>
 * To create an instance of {@link Matcher}, use the factory methods defined
 * in this interface, together with the {@link Tuple#match(Matcher,
 * Consumer)} method.
 *
 * @see Tuple
 * @author Kevin Henry
 */
public interface Matcher {

    /**
     * Pattern matcher for depth of 1
     * @return Matcher object encapsulating pattern element
     */
    static <T1> Matcher1<T1> when(T1 t1) { return new Matcher1<>(t1); }

    /**
     * Pattern matcher for depth of 2
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2> Matcher2<T1,T2> when(T1 t1, T2 t2) { return new Matcher2<>(t1,t2); }

    /**
     * Pattern matcher for depth of 3
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3> Matcher3<T1,T2,T3> when(T1 t1, T2 t2, T3 t3) { return new Matcher3<>(t1,t2,t3); }

    /**
     * Pattern matcher for depth of 4
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4> Matcher4<T1,T2,T3,T4> when(T1 t1, T2 t2, T3 t3, T4 t4) { return new Matcher4<>(t1,t2,t3,t4); }

    /**
     * Pattern matcher for depth of 5
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return new Matcher5<>(t1,t2,t3,t4,t5); }

    /**
     * Pattern matcher for depth of 6
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return new Matcher6<>(t1,t2,t3,t4,t5,t6); }

    /**
     * Pattern matcher for depth of 7
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7> Matcher7<T1,T2,T3,T4,T5,T6,T7> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) { return new Matcher7<>(t1,t2,t3,t4,t5,t6,t7); }

    /**
     * Pattern matcher for depth of 8
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8> Matcher8<T1,T2,T3,T4,T5,T6,T7,T8> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) { return new Matcher8<>(t1,t2,t3,t4,t5,t6,t7,t8); }

    /**
     * Pattern matcher for depth of 9
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return new Matcher9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9); }

    /**
     * Pattern matcher for depth of 10
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Matcher10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) { return new Matcher10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10); }

    /**
     * Pattern matcher for depth of 11
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return new Matcher11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }

    /**
     * Pattern matcher for depth of 12
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Matcher12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) { return new Matcher12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12); }


    /**
     * Pattern matcher for depth of 13
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Matcher13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) { return new Matcher13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13); }
    
    /**
     * Pattern matcher for depth of 14
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Matcher14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) { return new Matcher14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14); }

    /**
     * Pattern matcher for depth of 15
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Matcher15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) { return new Matcher15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15); }

    /**
     * Pattern matcher for depth of 16
     * @return Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Matcher16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> when(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) { return new Matcher16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16); }

    /**
     * Matches this {@link Matcher} with {@code tuple} object.
     *
     * @param tuple object match pattern against.
     * @param <T> type of tuple
     * @return {@code true} when all elements of the pattern match the tuple's
     * corresponding elements.
     */
    <T extends Tuple> boolean match(T tuple);
}
