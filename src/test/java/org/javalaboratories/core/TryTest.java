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

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
        String string1 = aTry1.filter(s -> s.length() > 0)
                             .map(s -> s + " Hello World!")
                             .fold("",Function.identity());

        String string2 = aTry1.filter(s -> s.length() == 0)
                              .getOrElse("Empty string?!");

        int number = aTry2.filter(n -> n > 5)
                    .fold(t -> t instanceof ArithmeticException ? 0 : 1,Function.identity());

        // Then
        assertEquals("This is a test file with some text. Hello World!", string1);
        assertEquals("Empty string?!", string2);
        assertEquals(0,number);
    }

    @Test
    public void testFilterNot_Pass() {
        // When
        String string1 = aTry1.filterNot(s -> s.length() == 0)
                .map(s -> s + " Hello World!")
                .fold("",Function.identity());

        String string2 = aTry1.filterNot(s -> s.length() > 0)
                .getOrElse("Empty string?!");

        int number = aTry2.filterNot(n -> n > 5)
                .fold(t -> t instanceof ArithmeticException ? 0 : 1,Function.identity());

        // Then
        assertEquals("This is a test file with some text. Hello World!", string1);
        assertEquals("Empty string?!", string2);
        assertEquals(0,number);
    }

    @Test
    public void testFlatMap_Pass() {
        // Given
        Try<Try<Integer>> aTry1 = Try.of(() -> Try.of(() -> 10 / 5));
        Try<Try<Integer>> aTry2 = Try.of(() -> Try.of(() -> 10 /0));

        // When
        String string1 = aTry1.flatMap(n -> n)
                            .map(n -> "Result is: "+n)
                            .fold("", Function.identity());

        String string2 = aTry2.flatMap(n -> n)
                            .map(n -> "Result is: "+n)
                            .fold("Error: DivByZero", Function.identity());

        // Then
        assertEquals("Result is: 2", string1);
        assertEquals("Error: DivByZero",string2);
    }

    @Test
    public void testFlatten_Pass() {
        // Given
        Try<Try<Integer>> aTry1 = Try.of(() -> Try.of(() -> 10 / 5));

        // When
        String string = aTry1.flatten()
                             .map(n -> "Result is: " + n)
                             .fold("",Function.identity());

        // Then
        assertEquals("Result is: 2",string);
    }

    @Test
    public void testFold_Pass() {
        // When
        String string = aTry1.fold(t -> t instanceof Exception ? t.getMessage() : "",Function.identity());
        int number = aTry2.fold(t -> t instanceof ArithmeticException ? 0 : 1,Function.identity());

        // Then
        assertEquals("This is a test file with some text.", string);
        assertEquals(0,number);
    }

    @Test
    public void testForEach_Pass() {
        // Given
        int[] count = new int[1];

        // When -- indirectly tests iterator
        aTry1.forEach(s -> count[0]++);

        // Then
        assertEquals(1, count[0]);
    }

    @Test
    public void testMap_Pass() {
        // When
        int size = aTry1.map(String::length)
                        .fold(0,Function.identity());

        // Then
        assertEquals(35,size);
    }

    @Test
    public void testOrElse_Pass() {
        // When
        String string = aTry1.orElse("")
                             .fold("",Function.identity());

        int number = aTry2.orElse(-1)
                          .fold(0, Function.identity());

        // Then
        assertEquals("This is a test file with some text.",string);
        assertEquals(-1,number);
    }

    @Test
    public void testOrElseThrow_Pass() {
        // When
        String string = aTry1.orElseThrow(IllegalArgumentException::new);

        // Then
        assertThrows(IllegalArgumentException.class, () -> aTry2.orElseThrow(IllegalArgumentException::new));
        assertEquals("This is a test file with some text.",string);
    }

    @Test
    public void testPeek_Pass() {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(TryTest.class);

        // When
        String string =  aTry1.peek(logger::info)
             .map(s -> s + " Transformed message!")
             .peek(logger::info)
             .fold("", Function.identity());

        boolean transformed = logCaptor.getInfoLogs().stream()
                                       .anyMatch(s -> s.contains("Transformed message!"));

        // Then
        assertEquals("This is a test file with some text. Transformed message!",string);
        assertTrue(transformed);
    }

    @Test
    public void testRecover_Pass() {
        // Given
        Try<String> aTryString = aTry1.recover(e -> e instanceof FileNotFoundException ? e.getMessage() : "");
        Try<Integer> aTryInteger = aTry2.recover(e -> e instanceof ArithmeticException ? 0 : 1);

        // When
        int size = aTryInteger.fold(-1,Function.identity());
        String string = aTryString.fold("",Function.identity());

        // Then
        assertTrue(aTryInteger.isSuccess());
        assertEquals(0,size);

        assertTrue(aTryString.isSuccess());
        assertEquals("This is a test file with some text.",string);
    }

    @Test
    public void testEither_Pass() {
        // Given
        Either<Throwable,String> either1 = aTry1.toEither();
        Either<Throwable,Integer> either2 = aTry2.toEither();

        // When
        boolean converted1 = either1.contains("This is a test file with some text.");
        boolean converted2 = either2.contains(10);
        Throwable throwable = either2.fold(t -> t, t -> new Throwable());

        // Then
        assertTrue(converted1);
        assertFalse(converted2);

        assertTrue(throwable instanceof ArithmeticException);
    }

    @Test
    public void testStream_Pass() {
        // When
        int size = aTry1.stream()
                .map(String::length)
                .reduce(0,(a,b) -> b);

        // Then
        assertEquals(35,size);
    }

    @Test
    public void testToList_Pass() {
        // Given
        List<String> list1 = aTry1.toList();
        List<Integer> list2 = aTry2.toList();

        // Then
        assertEquals(1,list1.size());
        assertEquals("This is a test file with some text.",list1.get(0));

        assertEquals(0,list2.size());
    }

    @Test
    public void testToMap_Pass() {
        // Given
        Map<String,String> map1 = aTry1.toMap(s -> "key1");
        Map<String,Integer> map2 = aTry2.toMap(s -> "key1");

        // Then
        assertEquals(1,map1.size());
        assertEquals("This is a test file with some text.",map1.get("key1"));

        assertEquals(0,map2.size());
    }

    @Test
    public void testToMaybe_Pass() {
        // Given
        Maybe<String> maybe1 = aTry1.toMaybe();
        Maybe<Integer> maybe2 = aTry2.toMaybe();

        // Then
        assertEquals("This is a test file with some text.",maybe1.get());
        assertEquals(Maybe.empty(),maybe2);
    }

    @Test
    public void testEqualsHashCode_Pass() {
        // Given
        Try<String> aTryString = Try.of(() -> "This is a test file with some text.");

        // Then
        assertEquals(aTryString,aTry1);
    }

    @Test
    public void testApplicable_Pass() {
        // When
        Try<Integer> aTry = aTry2.recover(t -> 10);

        // Given
        Function<Integer,Integer> add = n -> n + 10;

        Try<Integer> value1 = aTry.apply(Try.of(() -> add))
                                  .apply(Try.of(() -> n -> n + 25));

        // Then
        assertEquals(Try.of(() -> 45),value1);
    }

    @Test
    public void testExamples_Pass() {
        String result1 = Try.of(() -> 100 / 0)
                            .recover(t -> t instanceof ArithmeticException ? 100 : 100)
                            .map(n -> n * 10)
                            .filter(n -> n > 500)
                            .fold("",n -> "Result1="+n);

        String result2 = Try.of(() -> 100 / 0)
                            .orElse(100)
                            .map(n -> n * 25)
                            .filter(n -> n > 500)
                            .fold("",n -> "Result2="+n);

        int result3 = Try.of(() -> new String(Files.readAllBytes(Paths.get("does-not-exist.txt"))))
                            .orElse("")
                            .map(String::length)
                            .fold(-1,Function.identity());

        assertEquals("Result1=1000",result1);
        assertEquals("Result2=2500",result2);
        assertEquals(0,result3);
    }
}
