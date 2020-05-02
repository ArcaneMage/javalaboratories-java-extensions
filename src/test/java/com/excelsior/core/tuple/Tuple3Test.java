package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple3Test {

    private Tuple3<Integer,Integer,Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
        assertEquals(2,tuple.value2());
        assertEquals(3,tuple.value3());
    }

    @Test
    public void testAddAt_Pass() {
        Tuple4 tuple4 = tuple.addAt1("a");
        assertEquals(Tuple.of("a",1,2,3),tuple4);

        tuple4 = tuple.addAt2("a");
        assertEquals(Tuple.of(1,"a",2,3),tuple4);

        tuple4 = tuple.addAt3("a");
        assertEquals(Tuple.of(1,2,"a",3),tuple4);
    }

    @Test
    public void testHopTo_Pass() {
        Tuple3 tuple3 = tuple.hopTo1();
        assertEquals(Tuple.of(1,2,3), tuple3);

        Tuple2 tuple2 = tuple.hopTo2();
        assertEquals(Tuple.of(2,3), tuple2);

        Tuple1 tuple1 = tuple.hopTo3();
        assertEquals(Tuple.of(3), tuple1);
    }

    @Test
    public void testJoin_Pass() {
        Tuple3 aTuple3 = tuple.join(Tuple.of());
        assertEquals(Tuple.of(1,2,3), aTuple3);

        Tuple4 aTuple4_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple4_1.value4());

        Tuple4 aTuple4_2 = tuple.join(Tuple.of(4));
        assertEquals(Tuple.of(1,2,3,4),aTuple4_2);

        Tuple5 aTuple5 = tuple.join(Tuple.of(4,5));
        assertEquals(Tuple.of(1,2,3,4,5),aTuple5);

        Tuple6 aTuple6 = tuple.join(Tuple.of(4,5,6));
        assertEquals(Tuple.of(1,2,3,4,5,6),aTuple6);

        Tuple7 aTuple7 = tuple.join(Tuple.of(4,5,6,7));
        assertEquals(Tuple.of(1,2,3,4,5,6,7),aTuple7);

        Tuple8 aTuple8 = tuple.join(Tuple.of(4,5,6,7,8));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8),aTuple8);

        Tuple9 aTuple9 = tuple.join(Tuple.of(4,5,6,7,8,9));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9),aTuple9);

        Tuple10 aTuple10 = tuple.join(Tuple.of(4,5,6,7,8,9,10));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10),aTuple10);

        Tuple11 aTuple11 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11),aTuple11);

        Tuple12 aTuple12 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12),aTuple12);

        Tuple13 aTuple13 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13),aTuple13);

        Tuple14 aTuple14 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14),aTuple14);

        Tuple15 aTuple15 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14,15));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),aTuple15);

        Tuple16 aTuple16 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14,15,16));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),aTuple16);
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1,2,3);

        Nullable<Tuple3<Integer, Integer, Integer>>
                maybeTuple = Tuple3.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple3.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSpliceAt_Pass() {
        Tuple2<Tuple0,Tuple3<Integer,Integer,Integer>>
                spliced1 = tuple.spliceAt1();
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(3, spliced1.value2().value3());

        Tuple2<Tuple1<Integer>,Tuple2<Integer,Integer>>
                spliced2 = tuple.spliceAt2();
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(3, spliced2.value2().value2());

        Tuple2<Tuple2<Integer,Integer>,Tuple1<Integer>>
                spliced3 = tuple.spliceAt3();
        assertEquals(2, spliced3.depth());
        assertEquals(1, spliced3.value1().value1());
        assertEquals(3, spliced3.value2().value1());
    }

    @Test
    public void testRemove_Pass() {
        Tuple2 tuple2 = tuple.remove(3);
        assertEquals(Tuple.of(1,2),tuple2);

        Tuple2 tuple2_2 = tuple.remove(2);
        assertEquals(Tuple.of(1,3),tuple2_2);

        Tuple2 tuple2_3 = tuple.remove(1);
        assertEquals(Tuple.of(2,3),tuple2_3);
    }
    
    @Test
    public void testTruncate_Pass() {
        Tuple0 aTuple0 = tuple.truncate(1);
        assertEquals(Tuple.of(),aTuple0);

        Tuple1 aTuple1 = tuple.truncate(2);
        assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncate(3);
        assertEquals(Tuple.of(1,2),aTuple2);
    }

    @Test
    public void testTestTransform_Pass() {
        Tuple3 aTuple3;

        aTuple3 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0,2,3),aTuple3);

        aTuple3 = tuple.transform2(a -> 0);
        assertEquals(Tuple.of(1,0,3),aTuple3);

        aTuple3 = tuple.transform3(a -> 0);
        assertEquals(Tuple.of(1,2,0),aTuple3);
    }
}
