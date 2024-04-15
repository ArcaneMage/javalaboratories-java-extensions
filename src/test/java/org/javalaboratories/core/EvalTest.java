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

import nl.altindag.log.LogCaptor;
import org.javalaboratories.core.concurrency.AbstractConcurrencyTest;
import org.javalaboratories.core.concurrency.AsyncEval;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EvalTest extends AbstractConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(EvalTest.class);

    private Eval<Integer> always,alwaysR,eager,later,laterR;
    private AsyncEval<Integer> asyncEval, asyncFailure;

    @BeforeEach
    public void setup() {
        AtomicInteger intValue = new AtomicInteger(60);
        eager = Eval.eager(12);
        later = Eval.later(() -> 12 * 5);
        always = Eval.always(() -> {
            int value =  intValue.getAndIncrement() % 64;
            logger.debug("Next calculated value = {}",value);
            return value;
        }); // Round robin evaluation

        alwaysR = Eval.alwaysRecursive(fibonacci(10));
        laterR = Eval.laterRecursive(fibonacci(10));
        asyncEval = AsyncEval.asyncLater(() -> doLongRunningTask("Eval.asyncLater()"));
        asyncFailure = AsyncEval.asyncLater(() -> 100 / 0);
    }

    @AfterEach
    public void teardown() {
        asyncEval.resolve();
        //asyncFailure.resolve();
    }

    @Test
    public void testNew_Pass() {
        // Given (setup)

        // Then
        assertTrue(eager instanceof Eval.Eager);
        assertTrue(later instanceof Eval.Later);
        assertTrue(always instanceof Eval.Always);
        assertNotNull(asyncEval);

        assertEquals("Always[unset]",always.toString());
        assertEquals("Always[unset]", alwaysR.toString());
        assertEquals("Eager[12]",eager.toString());
        assertEquals("Later[unset]",later.toString());
        assertEquals("AsyncEval[unset]", asyncEval.toString());
    }

    @Test
    public void testEqual_Pass() {
        // Given (setup)
        Eval<Integer> asyncLater = AsyncEval.asyncLater(() -> 12).resolve();
        Eval<Integer> later = Eval.later(() -> 12).resolve();
        Eval<Integer> always = Eval.always(() -> 12).resolve();

        // Then
        assertNotEquals(eager,asyncLater);
        assertNotEquals(asyncLater,later);
        assertNotEquals(later,always);
        assertEquals(eager,Eval.eager(12));
        assertEquals(always,Eval.always(() -> 12).resolve());
        assertEquals(later,Eval.later(() -> 12).resolve());
    }

    @Test
    public void testConstants_Pass() {
        assertEquals(1,Eval.ONE.get());
        assertEquals(0,Eval.ZERO.get());
        assertEquals(true,Eval.TRUE.get());
        assertEquals(false,Eval.FALSE.get());
        assertEquals("",Eval.EMPTY.get());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testFilter_Pass() {
        // Given (setup)

        // Then
        assertThrows(IllegalStateException.class, () -> eager
                .filter(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value")));

        assertThrows(IllegalStateException.class, () -> later
                .filter(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value")));

        assertThrows(IllegalStateException.class, () -> always
                .filter(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value")));

        Eval<Integer> eval1 = eager
                .filter(v -> v > 11)
                .get();
        Eval<Integer> eval2 = later
                .filter(v -> v > 11)
                .get();
        Eval<Integer> eval3 = always
                .filter(v -> v > 11)
                .get();
        assertEquals(12, eval1.get());
        assertEquals(60, eval2.get());
        assertEquals(62, eval3.get());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testFilterNot_Pass() {
        // Given (setup)

        // Then
        eager
                .filterNot(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value"));

        later
                .filterNot(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value"));

        always
                .filterNot(v -> v < 12)
                .orElseThrow(() -> new IllegalStateException("Illegal value"));

        assertThrows(NoSuchElementException.class,() -> eager
                .filterNot(v -> v > 11)
                .get());
        assertThrows(NoSuchElementException.class,() -> later
                .filterNot(v -> v > 11)
                .get());
        assertThrows(NoSuchElementException.class,() -> always
                .filterNot(v -> v > 11)
                .get());
    }

    @Test
    public void testFlatMap_Pass() {
        // Given (setup)
        Eval<Integer> intValue = Eval.eager(100);

        // When
        String result = later
                .flatMap(v -> Eval.eager(v * intValue.get()))
                .map(v -> v+"")
                .get();

        // Then
        assertEquals("6000",result);
    }

    @Test
    public void testFlatten_Pass() {
        // Given (setup)
        Eval<Eval<Integer>> value = Eval.eager(Eval.eager(105));

        // When
        String result = value
                .<Integer>flatten()
                .fold("", Object::toString);

        // Then
        assertEquals("105",result);
    }

    @Test
    public void testPeek_Pass() {
        // Given (setup)
        LogCaptor logCaptor = LogCaptor.forClass(EvalTest.class);

        // When
        String eagerResult = eager
                .map(v -> v + "")
                .peek(v -> logger.info("(Eager) peek value as string \"{}\"",v))
                .get();
        String laterResult = later
                .map(v -> v + "")
                .peek(v -> logger.info("(Later) peek value as string \"{}\"",v))
                .get();
        String alwaysResult = always
                .map(v -> v + "")
                .peek(v -> logger.info("(Always) peek value as string \"{}\"",v))
                .get();

        // Then
        assertEquals("12",eagerResult);
        assertEquals("60",laterResult);
        assertEquals("60",alwaysResult);
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(s -> s.equals("(Eager) peek value as string \"12\"")));
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(s -> s.equals("(Later) peek value as string \"60\"")));
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(s -> s.equals("(Always) peek value as string \"60\"")));
    }

    @Test
    public void testCPeek_Pass() {
        // Given (setup)
        List<Integer> numbers = Arrays.asList(10,20,40,80,160);
        LogCaptor logCaptor = LogCaptor.forClass(EvalTest.class);

        // When
        numbers.forEach(Eval.cpeek(value -> logger.info("Eval.map: {}",value.map(v -> v + 5).fold(0,Function.identity()))));
        numbers.forEach(Eval.cpeek(value -> value.fold(0,Function.identity()) > 5, value -> logger.info("{}",value)));

        long logCount = logCaptor.getInfoLogs().stream()
                .filter(s -> s.equals("Eval.map: 15") ||
                             s.equals("Eval.map: 25") ||
                             s.equals("Eval.map: 45") ||
                             s.equals("Eval.map: 85") ||
                             s.equals("Eval.map: 165"))
                .count();
        long logActions = logCaptor.getInfoLogs().stream()
                .filter(s -> s.equals("Eager[10]") ||
                             s.equals("Eager[20]") ||
                             s.equals("Eager[40]") ||
                             s.equals("Eager[80]") ||
                             s.equals("Eager[160]"))
                .count();

        // Then
        assertEquals(5,logCount);
        assertEquals(5,logActions);
    }

    @Test
    public void testToList_Pass() {
        // Given (setup)

        // When
        List<Integer> list1 = eager.toList();
        List<Integer> list2 = later.toList();
        List<Integer> list3 = always.toList();

        // Then
        assertEquals(1, list1.size());
        assertEquals(1, list2.size());
        assertEquals(1, list3.size());

        assertEquals(12, list1.get(0));
        assertEquals(60, list2.get(0));
        assertEquals(60, list3.get(0));

        // Then -- Immutability?
        assertThrows(UnsupportedOperationException.class, () -> list1.add(10));
        assertThrows(UnsupportedOperationException.class, () -> list2.add(10));
        assertThrows(UnsupportedOperationException.class, () -> list3.add(10));
    }

    @Test
    public void testToMap_Pass() {
        // Given (setup)

        // When
        Map<String,Integer> map1 = eager.toMap(n -> "key1");
        Map<String,Integer> map2 = later.toMap(n -> "key1");
        Map<String,Integer> map3 = always.toMap(n -> "key1");

        // Then
        assertEquals(1, map1.size());
        assertEquals(1, map2.size());
        assertEquals(1, map3.size());

        assertEquals(12, map1.get("key1"));
        assertEquals(60, map2.get("key1"));
        assertEquals(60, map3.get("key1"));

        // Then -- Immutability?
        assertThrows(UnsupportedOperationException.class, () -> map1.put("key2",10));
        assertThrows(UnsupportedOperationException.class, () -> map2.put("key2",10));
        assertThrows(UnsupportedOperationException.class, () -> map3.put("key2",10));
    }


    @Test
    public void testToSet_Pass() {
        // When
        Set<Integer> set1 = eager.toSet();
        Set<Integer> set2 = later.toSet();
        Set<Integer> set3 = always.toSet();

        // Then
        assertTrue(set1.contains(12));
        assertTrue(set2.contains(60));
        assertTrue(set3.contains(60));

        // Immutability?
        assertThrows(UnsupportedOperationException.class, () -> set1.add(10));
        assertThrows(UnsupportedOperationException.class, () -> set2.add(10));
        assertThrows(UnsupportedOperationException.class, () -> set3.add(10));
    }

    @Test
    public void testToMaybe_Pass() {
        // Given (setup)

        // When
        Maybe<Integer> maybe1 = eager.toMaybe();
        Maybe<Integer> maybe2 = later.map(v -> (Integer) null).toMaybe();

        // Then
        assertFalse(maybe1.isEmpty());
        assertTrue(maybe2.isEmpty());
    }

    @Test
    public void testForEach_Pass() {
        // Given (setup)

        // When
        AtomicInteger count = new AtomicInteger(0);
        always.forEach(c -> count.getAndIncrement());
        alwaysR.forEach(c -> count.getAndIncrement());
        eager.forEach(c -> count.getAndIncrement());
        later.forEach(c -> count.getAndIncrement());
        laterR.forEach(c -> count.getAndIncrement());

        // Then
        assertEquals(5,count.get());
    }

    @Test
    public void testReserve_Pass() {
        // Given (setup)

        // When
        int value = always.get();
        Eval<Integer> eval = always.reserve();
        int value2 = eval.get();
        int value3 = eval.get();

        // Then
        assertEquals(60,value);
        assertEquals(61,value2);
        assertEquals(61,value3);
    }

    @Test
    public void testFlatMap_MonadLaws_Pass() {
        // Given
        Eval<Integer> eagerEval = Eval.eager(-1);
        Eval<Integer> laterEval = Eval.later(() -> -1);
        Eval<Integer> alwaysEval = Eval.always(() -> -1);

        // Then
        assertTrue(verifyMonadLaws(eagerEval, x -> Eval.eager(x * 2),Eval::eager));
        assertTrue(verifyMonadLaws(laterEval, x -> Eval.later(() -> x * 2), x -> Eval.later(() -> x).resolve()));
        assertTrue(verifyMonadLaws(alwaysEval, x -> Eval.always(() -> x * 2), x -> Eval.always(() -> x).resolve()));
    }

    @Test
    public void testMap_FunctorLaws_Pass() {
        // Given
        Eval<Integer> eagerEval = Eval.eager(-1);
        Eval<Integer> laterEval = Eval.later(() -> -1);
        Eval<Integer> alwaysEval = Eval.always(() -> -1);

        // Then
        assertTrue(verifyFunctorLaws(eagerEval));
        assertTrue(verifyFunctorLaws(laterEval));
        assertTrue(verifyFunctorLaws(alwaysEval));
    }

    @Test
    public void testMap_Pass() {
        // Given (setup)

        // When
        int result = alwaysR.map(value -> value + 10)
                .get();

        // Then
        assertEquals(65, result);
    }

    @Test
    public void testEval_Asynchronous_Pass() {
        // Given (setup)

        // When
        int result = assertTimeout(Duration.ofMillis(2560),() -> asyncEval.map(value -> value * 2)
                    .get());

        // Then
        assertEquals(254,result);
        assertTrue(asyncEval.isCompleted());
        assertTrue(asyncEval.isFulfilled());
        assertFalse(asyncEval.isRejected());
    }

    @Test
    public void testEval_AsynchronousError_Fail() {
        // Given (setup)

        // When
        assertThrows(NoSuchElementException.class, () -> asyncFailure.get());

        // Then
        assertTrue(asyncFailure.isCompleted());
        assertFalse(asyncFailure.isFulfilled());
        assertTrue(asyncFailure.isRejected());
        assertFalse(asyncFailure.getException().isEmpty());
        asyncFailure.getException()
                .ifPresent(e -> assertEquals("java.lang.ArithmeticException: / by zero",e.getMessage()));
    }

    @Test
    public void testEval_Comparable_Pass() {
        List<Eval<Integer>> list = Arrays.asList(Eval.eager(9),Eval.eager(5),Eval.eager(3),Eval.eager(8));

        String sorted = list.stream()
                .sorted()
                .peek(c -> logger.info(String.valueOf(c)))
                .map(e -> e.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }

    @Test
    public void testEval_ComparableWithMixedStrategies_Pass() {
        List<Eval<Integer>> list = Arrays.asList(Eval.later(() -> 9),Eval.eager(5),Eval.later(() -> 3),Eval.always(() -> 8));

        String sorted = list.stream()
                .sorted()
                .peek(c -> logger.info(String.valueOf(c)))
                .map(e -> e.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }

    @Test
    public void testEval_Applicative_Pass() {
        // When
        Eval<Integer> number1 = Eval.later(() -> 0);
        Eval<Integer> number2 = Eval.eager(0);

        // Given
        Function<Integer,Integer> add = n -> n + 10;

        Eval<Integer> value1 = number1.apply(Eval.later(() -> add))
                                      .apply(Eval.later(() -> add));

        Eval<Integer> value2 = number2.apply(Eval.eager(add))
                                      .apply(Eval.eager(add));

        // Then
        assertEquals(20,value1.get());
        assertEquals(20,value2.get());
    }

    private boolean verifyMonadLaws(final Eval<Integer> value,
                                    Function<Integer,Eval<Integer>> leftIdent,
                                    Function<Integer,Eval<Integer>> rightIdent) {
        return
                // (1) Left Identity law
                value.flatMap(leftIdent).equals(leftIdent.apply(-1))
                // (2) Right Identity law
                && value.flatMap(rightIdent).equals(value)
                // (3) Associative law
                && value.flatMap(leftIdent).flatMap(rightIdent).equals(leftIdent.apply(value.get()).flatMap(rightIdent));
   }

    private boolean verifyFunctorLaws(final Eval<Integer> value) {
        return
                // (1) Identity law
                value.equals(value.map(Function.identity()))
                && value.equals(value.map(x -> x))
                // (2) If a function composition (g), (h), then the resulting functor should be the
                // same as calling f with (h) and then with (g)
                && value.map(x -> (x + 1) * 2).equals(value.map(x -> x + 1).map(x -> x * 2));
    }

    private Trampoline<Integer>  fibonacci(int count) {
        return fibonacci(count,0,1);
    }

    private Trampoline<Integer> fibonacci(int count, int current, int next) {
        if (count == 0) {
            return Trampoline.finish(current);
        } else {
            return Trampoline.more(() -> fibonacci(count -1, next, current + next));
        }
    }

    private Trampoline<Integer> sum(int first, int last) {
        if (first == last) {
            return Trampoline.finish(last);
        }
        else {
            return Trampoline.more(() -> sum(first + 1,last));
        }
    }
}
