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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class TryTest {

    private static final Logger logger = LoggerFactory.getLogger(TryTest.class);

    private Try<String> aTry1;
    private Try<Integer> aTry2;

    @BeforeEach
    public void setup() {
        aTry1 = Try.of(() -> new String(Files.readAllBytes(Paths.get("target/test-classes/testfile.txt"))));
        aTry2 = Try.of(() -> 10 / 0);
    }

    @Test
    public void testOf_Pass() {
        // When
        String text = aTry1.fold("", Function.identity());
        Integer number = aTry2.fold(t -> t instanceof ArithmeticException ? 0 : 1,Function.identity());

        // Then
        assertEquals("This is a test file with some text.",text);
        assertEquals(0, number);

        assertTrue(aTry1.isSuccess());
        assertTrue(aTry2.isFailure());
    }

    @Test
    public void testFailed_Pass() {
        // Given
        boolean failure1 = aTry1.isFailure();
        boolean failure2 = aTry2.isFailure();

        // When
        Try<Throwable> aTry = aTry1.failed(); // Success-to-Failure
        boolean invertedFailure1 = aTry.isFailure();

        aTry = aTry2.failed();                // Failure-to-Success
        boolean invertedFailure2 = aTry.isFailure();

        //Then
        assertTrue(aTry1 instanceof Try.Success);
        assertTrue(aTry2 instanceof Try.Failure);

        assertTrue(invertedFailure1);
        assertFalse(failure1);

        assertFalse(invertedFailure2);
        assertTrue(failure2);
    }

    @Test
    public void testFilter_Pass() {
        // When
        String string = aTry1.filter(s -> s.length() > 0)
                             .map(s -> s + " Hello World!")
                             .fold("",Function.identity());

        int number = aTry2.filter(n -> n > 5)
                    .fold(t -> t instanceof ArithmeticException ? 0 : 1,Function.identity());

        // Then
        assertEquals("This is a test file with some text. Hello World!", string);
        assertEquals(0,number);
    }



}
