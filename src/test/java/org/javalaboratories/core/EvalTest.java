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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class EvalTest {

    private static final Logger logger = LoggerFactory.getLogger(EvalTest.class);

    private Eval<Integer> always,eager,later;

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
    }

    @Test
    public void testNew_Pass() {
        // Given (setup)

        // Then
        assertTrue(always instanceof Eval.Always);
        assertTrue(later instanceof Eval.Later);

        assertEquals("Always[unset]",always.toString());
        assertEquals("Always[unset]",eager.toString());
        assertEquals("Later[unset]",later.toString());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
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
        assertEquals(61, eval3.get());
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
    public void testList_Pass() {
        // Given (setup)

        // When
        List<Integer> list1 = eager.toList();
        List<Integer> list2 = later.toList();
        //List<Integer> list3 = always.map()
    }

    @Test
    public void testMapFn_Recursion_Pass() {
        // Given (setup)

        // When
        int result = eager.mapFn(this::fibonacci)
                .get();

        // Then
        System.out.println("\n"+result);
    }

    private Recursion<Integer> fibonacci(int count) {
        return fibonacci(count,0,1);
    }

    private Recursion<Integer> fibonacci(int count, int current, int next) {
        if ( count == 0) {
            return Recursion.finish(current);
        } else {
            return Recursion.more(() -> fibonacci(count -1, next, current + next));
        }
    }

    private Recursion<Integer> sum(int first, int last) {
        if (first == last)
            return Recursion.finish(last);
        else {
            return Recursion.more(() -> sum(first + 1,last));
        }
    }
}
