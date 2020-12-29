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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class EitherTest {

    private static final Logger logger = LoggerFactory.getLogger(EitherTest.class);

    private Either<Exception,Integer> left;
    private Either<Exception,Integer> right;
    private static final Consumer<?> DO_NOTHING_CONSUMER = (a) -> {};


    @BeforeEach
    public void setup() {
        left = Either.left(new Exception("Something has gone wrong"));
        right = Either.right(100);
    }

    @Test
    public void testContains_Pass() {
        // Given (setup)

        // Then
        assertFalse(left.contains(10));

        assertTrue(right.contains(100));
        assertFalse(right.contains(10));
    }

    @Test
    public void testExists_PredicateEvaluation_Pass() {
        // Given (setup)

        // Then
        assertFalse(left.exists(value -> value > 99));

        assertTrue(right.exists(value -> value > 99));
        assertFalse(right.exists(value -> value < 100));
    }

    @Test
    public void testFilter_PredicateEvaluation_Pass() {
        // Given (setup)

        // When
        Maybe<Either<Exception,Integer>> either1 = left.filter(value -> value > 99);
        Maybe<Either<Exception,Integer>> either2 = right.filter(value -> value > 99);
        Maybe<Either<Exception,Integer>> either3 = right.filter(value -> value < 100);

        // Then
        assertFalse(either1.isEmpty());
        assertTrue(either2.isPresent());
        assertTrue(either3.isEmpty());

        either2.ifPresent(value -> assertEquals(100,value.getOrElse(-1)));
    }

    @Test
    public void testFlatMap_MapperEvaluation_Pass() {
        // Given (setup)
        Parser parser = new Parser();

        // When
        String rresult = right
                .flatMap(value -> parser.parse(String.format("[%d] Kevin",value)))
                .flatMap(value -> Either.right(value + " Henry"))
                .getOrElse(null);

        String lresult = left
                .flatMap(value -> Either.left(new Exception("Something else")))
                .fold(Exception::getMessage,value -> null);

        // Then
        assertEquals(rresult,"Hello World! [100] Kevin Henry");
        assertEquals(lresult,"Something has gone wrong"); // Left does not transform
    }

    @Test
    public void testFold_Values_Pass() {
        // Given (setup)
        Function<Exception,String> functionA = (Exception::getMessage);
        Function<Integer,String> functionB = (value -> "Right value equals: "+value);

        // When
        String rresult = right
                .fold(functionA,functionB);
        String lresult = left
                .fold(functionA,functionB);

        // Then
        assertEquals(rresult,"Right value equals: 100");
        assertEquals(lresult,"Something has gone wrong"); // Left does not transform
    }


    // Some contrived use case
    private static class Parser {
        public Either<Exception,String> parse(String value) {
            return Either.right("Hello World! "+value);
        }
    }
}
