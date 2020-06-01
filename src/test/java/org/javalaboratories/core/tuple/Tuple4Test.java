package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.javalaboratories.core.tuple.Matcher.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple4Test {

    private Tuple4<Integer,Integer,Integer,Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3,4);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
        assertEquals(2,tuple.value2());
        assertEquals(3,tuple.value3());
        assertEquals(4,tuple.value4());
    }

    @Test
    public void testAddAt_Pass() {
        Tuple5 tuple5 = tuple.addAt1("a");
        Assertions.assertEquals(Tuple.of("a",1,2,3,4),tuple5);

        tuple5 = tuple.addAt2("a");
        Assertions.assertEquals(Tuple.of(1,"a",2,3,4),tuple5);

        tuple5 = tuple.addAt3("a");
        Assertions.assertEquals(Tuple.of(1,2,"a",3,4),tuple5);

        tuple5 = tuple.addAt4("a");
        Assertions.assertEquals(Tuple.of(1,2,3,"a",4),tuple5);
    }

    @Test
    public void testHopTo_Pass() {
        Tuple4 tuple4 = tuple.hopTo1();
        Assertions.assertEquals(Tuple.of(1,2,3,4), tuple4);

        Tuple3 tuple3 = tuple.hopTo2();
        Assertions.assertEquals(Tuple.of(2,3,4), tuple3);

        Tuple2 tuple2 = tuple.hopTo3();
        Assertions.assertEquals(Tuple.of(3,4), tuple2);

        Tuple1 tuple1 = tuple.hopTo4();
        Assertions.assertEquals(Tuple.of(4), tuple1);
    }
    
    @Test
    public void testJoin_Pass() {
        Tuple4 aTuple4 = tuple.join(Tuple.of());
        Assertions.assertEquals(Tuple.of(1,2,3,4),aTuple4);

        Tuple5 aTuple5_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple5_1.value5());

        Tuple5 aTuple5_2 = tuple.join(Tuple.of(5));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5),aTuple5_2);

        Tuple6 aTuple6 = tuple.join(Tuple.of(5,6));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6),aTuple6);

        Tuple7 aTuple7 = tuple.join(Tuple.of(5,6,7));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7),aTuple7);

        Tuple8 aTuple8 = tuple.join(Tuple.of(5,6,7,8));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8),aTuple8);

        Tuple9 aTuple9 = tuple.join(Tuple.of(5,6,7,8,9));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9),aTuple9);

        Tuple10 aTuple10 = tuple.join(Tuple.of(5,6,7,8,9,10));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10),aTuple10);

        Tuple11 aTuple11 = tuple.join(Tuple.of(5,6,7,8,9,10,11));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11),aTuple11);

        Tuple12 aTuple12 = tuple.join(Tuple.of(5,6,7,8,9,10,11,12));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12),aTuple12);

        Tuple13 aTuple13 = tuple.join(Tuple.of(5,6,7,8,9,10,11,12,13));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13),aTuple13);

        Tuple14 aTuple14 = tuple.join(Tuple.of(5,6,7,8,9,10,11,12,13,14));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14),aTuple14);

        Tuple15 aTuple15 = tuple.join(Tuple.of(5,6,7,8,9,10,11,12,13,14,15));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),aTuple15);

        Tuple16 aTuple16 = tuple.join(Tuple.of(5,6,7,8,9,10,11,12,13,14,15,16));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),aTuple16);
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1,2,3,4);

        Nullable<Tuple4<Integer, Integer, Integer, Integer>>
                maybeTuple = Tuple4.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> Assertions.assertEquals(Tuple.of(1,2,3,4), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple4.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSpliceAt_Pass() {
        Tuple2<Tuple0,Tuple4<Integer,Integer,Integer,Integer>>
                spliced1 = tuple.spliceAt1();
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(4, spliced1.value2().value4());

        Tuple2<Tuple1<Integer>,Tuple3<Integer,Integer,Integer>>
                spliced2 = tuple.spliceAt2();
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(4, spliced2.value2().value3());

        Tuple2<Tuple2<Integer,Integer>,Tuple2<Integer,Integer>>
                spliced3 = tuple.spliceAt3();
        assertEquals(2, spliced3.depth());
        assertEquals(1, spliced3.value1().value1());
        assertEquals(4, spliced3.value2().value2());

        Tuple2<Tuple3<Integer,Integer,Integer>,Tuple1<Integer>>
                spliced4 = tuple.spliceAt4();
        assertEquals(2, spliced4.depth());
        assertEquals(1, spliced4.value1().value1());
        assertEquals(4, spliced4.value2().value1());
    }

    @Test
    public void testRemove_Pass() {
        Tuple3 tuple3 = tuple.removeAt1();
        Assertions.assertEquals(Tuple.of(2,3,4),tuple3);

        tuple3 = tuple.removeAt2();
        Assertions.assertEquals(Tuple.of(1,3,4),tuple3);

        tuple3 = tuple.removeAt3();
        Assertions.assertEquals(Tuple.of(1,2,4),tuple3);

        tuple3 = tuple.removeAt4();
        Assertions.assertEquals(Tuple.of(1,2,3),tuple3);
    }

    @Test
    public void testRotateRight_Pass() {
        Tuple4 tuple4;

        tuple4 = tuple.rotateRight1();
        assertEquals(Tuple.of(4,1,2,3),tuple4);

        tuple4 = tuple.rotateRight2();
        assertEquals(Tuple.of(3,4,1,2),tuple4);

        tuple4 = tuple.rotateRight3();
        assertEquals(Tuple.of(2,3,4,1),tuple4);
    }

    @Test
    public void testRotateLeft_Pass() {
        Tuple4 tuple4 = tuple.rotateLeft1();
        assertEquals(Tuple.of(2,3,4,1),tuple4);

        tuple4 = tuple.rotateLeft2();
        assertEquals(Tuple.of(3,4,1,2),tuple4);

        tuple4 = tuple.rotateLeft3();
        assertEquals(Tuple.of(4,1,2,3),tuple4);
    }
    
    @Test
    public void testTruncateAt_Pass() {
        Tuple0 aTuple0 = tuple.truncateAt1();
        Assertions.assertEquals(Tuple.of(),aTuple0);

        Tuple1 aTuple1 = tuple.truncateAt2();
        Assertions.assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncateAt3();
        Assertions.assertEquals(Tuple.of(1,2),aTuple2);

        Tuple3 aTuple3 = tuple.truncateAt4();
        Assertions.assertEquals(Tuple.of(1,2,3),aTuple3);
    }

    @Test
    public void testMap_Pass() {
        String mapped = tuple.map((a,b,c,d) -> String.format("(%d,%d,%d,%d)", a,b,c,d));
        assertEquals("(1,2,3,4)",mapped);
    }

    @Test
    public void testMatch_Pass() {
        AtomicInteger index = new AtomicInteger();
        index.set(0);
        tuple.match(allOf(1,2,3,4), (a, b, c, d) -> index.incrementAndGet());
        tuple.match(anyOf(0,0,0,4), (a, b, c, d) -> index.incrementAndGet());
        tuple.match(setOf(4,3,2,1), (a, b, c, d) -> index.incrementAndGet());

        assertEquals(3,index.get());
    }

    @Test
    public void testMapAt_Pass() {
        Tuple4 aTuple4;

        aTuple4 = tuple.mapAt1(a -> 0);
        Assertions.assertEquals(Tuple.of(0,2,3,4),aTuple4);

        aTuple4 = tuple.mapAt2(a -> 0);
        Assertions.assertEquals(Tuple.of(1,0,3,4),aTuple4);

        aTuple4 = tuple.mapAt3(a -> 0);
        Assertions.assertEquals(Tuple.of(1,2,0,4),aTuple4);

        aTuple4 = tuple.mapAt4(a -> 0);
        Assertions.assertEquals(Tuple.of(1,2,3,0),aTuple4);
    }
}
