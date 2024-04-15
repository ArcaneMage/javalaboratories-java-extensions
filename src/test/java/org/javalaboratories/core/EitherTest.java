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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.javalaboratories.core.Either.*;
import static org.junit.jupiter.api.Assertions.*;

public class EitherTest {

    private Either<Exception,Integer> left;
    private Either<Exception,Integer> right;

    @BeforeEach
    public void setup() {
        left = left(new Exception("Something has gone wrong"));
        right = right(100);
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
    public void testEquals_Pass() {
        // Given (setup)
        Either<Exception,Integer> twin = right(100);

        // Then
        assertNotEquals(left,right);
        assertEquals(twin,right);
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

        either2.ifPresent(value -> assertEquals(100,value.orElse(-1)));
    }

    @Test
    public void testFilterOrElse_PredicateEvaluation_Pass() {
        // Given (setup)

        // When
        Either<Exception,Integer> either1 = left.filterOrElse(value -> value > 99,new Exception("What ?"));
        Either<Exception,Integer> either2 = right.filterOrElse(value -> value > 99,new Exception("What ?"));
        Either<Exception,Integer> either3 = right.filterOrElse(value -> value < 100,new Exception("Hey, less than 100?"));

        // Then
        assertEquals("Something has gone wrong",either1.fold(Exception::getMessage, Object::toString));
        assertEquals("100",either2.fold(Exception::getMessage, Object::toString));
        assertEquals("Hey, less than 100?",either3.fold(Exception::getMessage, Object::toString));
    }

    @Test
    public void testFlatten_Pass() {
        // Given
        Either<String,Either<String,Integer>> coffee = left("too-strong-coffee");
        Either<String,Either<String,Integer>> teacup = right(left("weak-tea"));
        Either<String,Either<String,Integer>> value127 = right(right(127));

        // When
        Either<String,Integer> left = coffee
                .flatten();
        Either<String,Integer> right = teacup
                .flatten();
        Either<String,Integer> right2 = value127
                .flatten();
        // Then
        assertEquals("Left[too-strong-coffee]",left.toString());
        assertEquals("Left[weak-tea]",right.toString());
        assertEquals("Right[127]",right2.toString());
    }

    @Test
    public void testFlatMap_MapperEvaluation_Pass() {
        // Given (setup)
        Parser parser = new Parser();

        // When
        String rresult = right
                .flatMap(value -> parser.parse(String.format("[%d] Kevin",value)))
                .map(value -> value + " Henry")
                .orElse("");

        String lresult = left
                .flatMap(value -> left(new Exception("Something else")))
                .fold(Exception::getMessage,value -> null);

        // Then
        assertEquals(rresult,"Hello World! [100] Kevin Henry");
        assertEquals(lresult,"Something has gone wrong"); // Left does not transform
    }

    @Test
    public void testFold_RetrieveValues_Pass() {
        // Given (setup)
        Function<Exception,String> functionA = Exception::getMessage;
        Function<Integer,String> functionB = value -> "Right value equals: "+value;

        // When
        String rresult = right
                .fold(functionA,functionB);
        String lresult = left
                .fold(functionA,functionB);

        // Then
        assertEquals(rresult,"Right value equals: 100");
        assertEquals(lresult,"Something has gone wrong"); // Left does not transform
    }

    @Test
    public void testForAll_PredicateEvaluation_Pass() {
        // Given (setup)

        // When
        boolean lAll = left.forAll(value -> value > 99);
        boolean rAll = right.forAll(value -> value > 99);
        boolean rAll2 = right.forAll(value -> value < 100);

        // Then
        assertTrue(lAll);
        assertTrue(rAll);
        assertFalse(rAll2);
    }

    @Test
    public void testForEach_Iterates_Pass() {
        // Given (setup)
        AtomicInteger ltracker = new AtomicInteger(0);
        AtomicInteger rtracker = new AtomicInteger(0);

        // When
        left.forEach(value -> ltracker.getAndIncrement());
        right.forEach(value -> rtracker.getAndIncrement());

        // Then
        assertEquals(0,ltracker.get());
        assertEquals(1,rtracker.get());
    }

    @Test
    public void testGetOrElse_RetrieveValues_Pass() {
        // Given (setup)

        // When
        int lvalue = left.orElse(17);
        int rvalue = right.orElse(23);

        // Then
        assertEquals(17, lvalue);
        assertEquals(100, rvalue);
    }

    @Test
    public void testIsLeftRight_Verification_Pass() {
        // Given (setup)

        // When
        boolean lIsLeft = left.isLeft();
        boolean lIsRight = left.isRight();
        boolean rIsLeft = right.isLeft();
        boolean rIsRight = right.isRight();

        // Then
        assertTrue(lIsLeft);
        assertFalse(lIsRight);
        assertFalse(rIsLeft);
        assertTrue(rIsRight);
    }

    @Test
    public void testMap_FunctionEvaluation_Pass() {
        // Given (setup)
        Function<Exception,String> functionA = Exception::getMessage;
        Function<Integer,String> functionB = value -> value+"";

        // When
        Either<Exception,Integer> left = this.left.map(value -> value * 25);
        Either<Exception,Integer> right = this.right.map(value -> value * 25);

        // Then
        assertEquals("Something has gone wrong",left.fold(functionA,functionB));
        assertEquals(2500,right.orElse(-1));
    }

    @Test
    public void testOf_Instantiation_Pass() {
        // Given (setup)
        Either<Exception,Integer> twin = of(100);

        // Then
        assertEquals(twin,right);
    }

    @Test
    public void testOrElse_Pass() {
        // Given (setup)

        // When
        Either<Exception,Integer> left = this.left.orElse(right(255));
        Either<Exception,Integer> right = this.right.orElse(right(255));

        // Then
        assertEquals(255,left.orElse(-1));
        assertEquals(100,right.orElse(-1));
    }

    @Test
    public void testOrElseGet_Pass() {
        // Given (setup)

        // When
        Either<Exception,Integer> left = this.left.orElseGet(() -> right(255));
        Either<Exception,Integer> right = this.right.orElseGet(() -> right(255));

        // Then
        assertEquals(255,left.orElse(-1));
        assertEquals(100,right.orElse(-1));
    }

    @Test
    public void testOrElseThrow_Fail() {
        // Given (setup)

        // Then
        assertThrows(IllegalArgumentException.class,() -> this.left.orElseThrow(IllegalArgumentException::new));
        assertEquals(100,this.right.orElseThrow(IllegalArgumentException::new).orElse(-1));
    }

    @Test
    public void testSwap_Pass() {
        // Given (setup)
        Function<Exception,String> functionA = Exception::getMessage;
        Function<Integer,String> functionB = value -> value+"";

        // When
        Either<Integer,Exception> swappedLeft = left.swap();
        Either<Integer,Exception> swappedLeftMapped = swappedLeft.map(value -> new Exception("{Transformed} "+value.getMessage()));
        Either<Integer,Exception> swappedRight = right.swap();

        // Then
        assertEquals("Something has gone wrong",swappedLeft.fold(functionB,functionA));
        assertEquals("{Transformed} Something has gone wrong",swappedLeftMapped.fold(functionB,functionA));
        assertEquals("100",swappedRight.fold(functionB,functionA));
    }

    @Test
    public void testApplicative_Pass() {
        // When
        Either<String,Integer> number = Either.right(0);
        Either<String,Integer> string = Either.left("Frustration afoot!");

        // Given
        Function<Integer,Integer> add = n -> n + 10;

        Either<String,Integer> value = number.apply(Either.right(add))
                .apply(Either.right(add));

        Either<String,Integer> value2 = string.apply(Either.left(add))
                .apply(Either.left(add));

        // Then
        assertEquals(Either.of(20),value);
        assertEquals(Either.left("Frustration afoot!"),value2);
    }

    @Test
    public void testToList_Pass() {
        // Given (setup)

        // When
        List<Integer> llist = left.toList();
        List<Integer> rlist = right.toList();

        // Then
        assertEquals(0,llist.size());
        assertEquals(1,rlist.size());
        assertEquals(100,rlist.get(0));

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> llist.add(100));
        assertThrows(UnsupportedOperationException.class,() -> rlist.add(100));
    }

    @Test
    public void testToMap_Pass() {
        // Given (setup)

        // When
        Map<String,Integer> lmap = left.toMap(v -> "LeftKey");
        Map<String,Integer> rmap = right.toMap(v -> "RightKey");

        // Then
        assertNull(lmap.get("LeftKey"));
        assertEquals(100,rmap.get("RightKey"));

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> lmap.put("LeftKey",110));
        assertThrows(UnsupportedOperationException.class,() -> rmap.put("RightKey",120));
    }

    @Test
    public void testToSet_Pass() {
        // Given (setup)

        // When
        Set<Integer> lset = left.toSet();
        Set<Integer> rset = right.toSet();

        // Then
        assertEquals(0,lset.size());
        assertTrue(rset.contains(100));

        // Immutability
        assertThrows(UnsupportedOperationException.class,() -> lset.add(110));
        assertThrows(UnsupportedOperationException.class,() -> rset.add(120));
    }

    @Test
    public void testToMaybe_Pass() {
        // Given (setup)

        // When
        Maybe<Integer> maybe1 = left.toMaybe();
        Maybe<Integer> maybe2 = right.toMaybe();

        // Then
        assertTrue(maybe1.isEmpty());
        assertFalse(maybe2.isEmpty());
        assertEquals(100,maybe2.get());
    }

    @Test
    public void testExamples_Pass() {
        // Given (setup)
        File file = new File("~/henryk/filesystem");
        Parser parser = new Parser();
        JsonObject jsonObject = new JsonObject();

        // When
        String string = parser.readFromFile(file)
                .flatMap(parser::parse)
                .map(jsonObject::marshal)
                .fold(Exception::getMessage,s -> s);

        assertEquals("Cannot open \"~/henryk/filesystem\"",string);
    }

    // Some contrived classes for testing
    //
    private static class Parser {
        public Either<Exception,String> parse(String value) {
            return right("Hello World! "+value);
        }
        public Either<Exception,String> readFromFile(File file) {
            return left(new FileNotFoundException("Cannot open \""+file+"\""));
        }
    }

    private static class JsonObject {
        private String marshal(String string) {
            return String.format("{\"name:\" \"%s\"}",string);
        }
    }
}
