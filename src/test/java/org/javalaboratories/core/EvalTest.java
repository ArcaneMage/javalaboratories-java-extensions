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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvalTest {

    private Eval<Integer> always,eager,later;

    @BeforeEach
    public void setup() {
        eager = Eval.eager(12);
        later = Eval.later(() -> 12 * 5);
    }

    @Test
    public void testMapFn_StackSafeRecursion_Pass() {
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
