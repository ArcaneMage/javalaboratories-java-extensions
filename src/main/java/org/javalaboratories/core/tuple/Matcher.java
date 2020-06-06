package org.javalaboratories.core.tuple;
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
 * {@link Matcher}. For example, {@link Matcher} has the following pattern
 * {@code ["^John$",43]} will match a tuple with the elements {@code ["John",43]}.
 * String elements in the pattern are regular expressions, all other types are matched
 * with the {@code equals} method. A match is defined when the elements of the tuple
 * satisfy the {@link Matcher} criteria.
 * <p>
 * To create an instance of {@link Matcher}, use the factory methods defined
 * in this interface, together with the {@code TupleX#match(Matcher,
 * Consumer)} method.
 *
 * @see Tuple
 * @see TupleElementMatcher
 * @see MatchableTuple
 * @see AbstractMatcher
 *
 * @author Kevin Henry
 */
public interface Matcher extends MatchableTuple, MatchablePatternAware {

    enum Strategy {MATCH_ALL, MATCH_ANY, MATCH_SET}

    /**
     * Pattern matcher for depth of 1
     * @return (All) Matcher object encapsulating pattern element
     */
    static <T1> Matcher1<T1> allOf(T1 t1) { return Matcher1.allOf(t1); }

    /**
     * Pattern matcher for depth of 2
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2> Matcher2<T1,T2> allOf(T1 t1, T2 t2) { return Matcher2.allOf(t1,t2); }

    /**
     * Pattern matcher for depth of 3
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3> Matcher3<T1,T2,T3> allOf(T1 t1, T2 t2, T3 t3) { return Matcher3.allOf(t1,t2,t3); }

    /**
     * Pattern matcher for depth of 4
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4> Matcher4<T1,T2,T3,T4> allOf(T1 t1, T2 t2, T3 t3, T4 t4) { return Matcher4.allOf(t1,t2,t3,t4); }

    /**
     * Pattern matcher for depth of 5
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return Matcher5.allOf(t1,t2,t3,t4,t5); }

    /**
     * Pattern matcher for depth of 6
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return Matcher6.allOf(t1,t2,t3,t4,t5,t6); }

    /**
     * Pattern matcher for depth of 7
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7> Matcher7<T1,T2,T3,T4,T5,T6,T7> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) { return Matcher7.allOf(t1,t2,t3,t4,t5,t6,t7); }

    /**
     * Pattern matcher for depth of 8
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8> Matcher8<T1,T2,T3,T4,T5,T6,T7,T8> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) { return Matcher8.allOf(t1,t2,t3,t4,t5,t6,t7,t8); }

    /**
     * Pattern matcher for depth of 9
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return Matcher9.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9); }

    /**
     * Pattern matcher for depth of 10
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Matcher10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) { return Matcher10.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10); }

    /**
     * Pattern matcher for depth of 11
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return Matcher11.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }

    /**
     * Pattern matcher for depth of 12
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Matcher12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) { return Matcher12.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12); }

    /**
     * Pattern matcher for depth of 13
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Matcher13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) { return Matcher13.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13); }
    
    /**
     * Pattern matcher for depth of 14
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Matcher14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) { return Matcher14.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14); }

    /**
     * Pattern matcher for depth of 15
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Matcher15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) { return Matcher15.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15); }

    /**
     * Pattern matcher for depth of 16
     * @return (All) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Matcher16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) { return Matcher16.allOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16); }

    /**
     * Pattern matcher for depth of 1
     * @return (Any) Matcher object encapsulating pattern element
     */
    static <T1> Matcher1<T1> anyOf(T1 t1) { return Matcher1.allOf(t1); }

    /**
     * Pattern matcher for depth of 2
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2> Matcher2<T1,T2> anyOf(T1 t1, T2 t2) { return Matcher2.anyOf(t1,t2); }

    /**
     * Pattern matcher for depth of 3
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3> Matcher3<T1,T2,T3> anyOf(T1 t1, T2 t2, T3 t3) { return Matcher3.anyOf(t1,t2,t3); }

    /**
     * Pattern matcher for depth of 4
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4> Matcher4<T1,T2,T3,T4> anyOf(T1 t1, T2 t2, T3 t3, T4 t4) { return Matcher4.anyOf(t1,t2,t3,t4); }

    /**
     * Pattern matcher for depth of 5
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return Matcher5.anyOf(t1,t2,t3,t4,t5); }

    /**
     * Pattern matcher for depth of 6
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return Matcher6.anyOf(t1,t2,t3,t4,t5,t6); }

    /**
     * Pattern matcher for depth of 7
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7> Matcher7<T1,T2,T3,T4,T5,T6,T7> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) { return Matcher7.anyOf(t1,t2,t3,t4,t5,t6,t7); }

    /**
     * Pattern matcher for depth of 8
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8> Matcher8<T1,T2,T3,T4,T5,T6,T7,T8> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) { return Matcher8.anyOf(t1,t2,t3,t4,t5,t6,t7,t8); }

    /**
     * Pattern matcher for depth of 9
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return Matcher9.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9); }

    /**
     * Pattern matcher for depth of 10
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Matcher10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) { return Matcher10.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10); }

    /**
     * Pattern matcher for depth of 11
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return Matcher11.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }

    /**
     * Pattern matcher for depth of 12
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Matcher12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) { return Matcher12.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12); }

    /**
     * Pattern matcher for depth of 13
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Matcher13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) { return Matcher13.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13); }

    /**
     * Pattern matcher for depth of 14
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Matcher14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) { return Matcher14.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14); }

    /**
     * Pattern matcher for depth of 15
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Matcher15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) { return Matcher15.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15); }

    /**
     * Pattern matcher for depth of 16
     * @return (Any) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Matcher16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) { return Matcher16.anyOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16); }

    /**
     * Pattern matcher for depth of 1
     * @return (Set) Matcher object encapsulating pattern element
     */
    static <T1> Matcher1<T1> setOf(T1 t1) { return Matcher1.setOf(t1); }

    /**
     * Pattern matcher for depth of 2
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2> Matcher2<T1,T2> setOf(T1 t1, T2 t2) { return Matcher2.setOf(t1,t2); }

    /**
     * Pattern matcher for depth of 3
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3> Matcher3<T1,T2,T3> setOf(T1 t1, T2 t2, T3 t3) { return Matcher3.setOf(t1,t2,t3); }

    /**
     * Pattern matcher for depth of 4
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4> Matcher4<T1,T2,T3,T4> setOf(T1 t1, T2 t2, T3 t3, T4 t4) { return Matcher4.setOf(t1,t2,t3,t4); }

    /**
     * Pattern matcher for depth of 5
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return Matcher5.setOf(t1,t2,t3,t4,t5); }

    /**
     * Pattern matcher for depth of 6
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return Matcher6.setOf(t1,t2,t3,t4,t5,t6); }

    /**
     * Pattern matcher for depth of 7
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7> Matcher7<T1,T2,T3,T4,T5,T6,T7> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) { return Matcher7.setOf(t1,t2,t3,t4,t5,t6,t7); }

    /**
     * Pattern matcher for depth of 8
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8> Matcher8<T1,T2,T3,T4,T5,T6,T7,T8> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) { return Matcher8.setOf(t1,t2,t3,t4,t5,t6,t7,t8); }

    /**
     * Pattern matcher for depth of 9
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return Matcher9.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9); }

    /**
     * Pattern matcher for depth of 10
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Matcher10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) { return Matcher10.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10); }

    /**
     * Pattern matcher for depth of 11
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return Matcher11.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }

    /**
     * Pattern matcher for depth of 12
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Matcher12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) { return Matcher12.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12); }

    /**
     * Pattern matcher for depth of 13
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Matcher13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) { return Matcher13.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13); }

    /**
     * Pattern matcher for depth of 14
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Matcher14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) { return Matcher14.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14); }

    /**
     * Pattern matcher for depth of 15
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Matcher15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) { return Matcher15.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15); }

    /**
     * Pattern matcher for depth of 16
     * @return (Set) Matcher object encapsulating pattern elements
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Matcher16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) { return Matcher16.setOf(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16); }

    /**
     * @return Current strategy employed by this {@link Matcher} object.
     */
    Strategy getStrategy();

    /**
     * Matches this {@link Matcher} with {@code tuple} object.
     *
     * @param tuple object match pattern against.
     * @param <T> type of tuple
     * @return {@code true} all all elements of the pattern match the tuple's
     * corresponding elements.
     */
    <T extends Tuple> boolean match(T tuple);

}
