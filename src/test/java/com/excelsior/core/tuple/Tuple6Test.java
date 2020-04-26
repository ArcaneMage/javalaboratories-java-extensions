package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple6Test {

    private Tuple6<Integer,Integer,Integer,Integer,Integer,Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3,4,5,6);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
        assertEquals(2,tuple.value2());
        assertEquals(3,tuple.value3());
        assertEquals(4,tuple.value4());
        assertEquals(5,tuple.value5());
        assertEquals(6,tuple.value6());
    }

    @Test
    public void testJoin_Pass() {
        Tuple7 aTuple7_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple7_1.value7());

        Tuple7 aTuple7_2 = tuple.join(Tuple.of(7));
        assertEquals(aTuple7_2,Tuple.of(1,2,3,4,5,6,7));

        Tuple8 aTuple8 = tuple.join(Tuple.of(7,8));
        assertEquals(aTuple8,Tuple.of(1,2,3,4,5,6,7,8));

        Tuple9 aTuple9 = tuple.join(Tuple.of(7,8,9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple.join(Tuple.of(7,8,9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple.join(Tuple.of(7,8,9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple.join(Tuple.of(7,8,9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple.join(Tuple.of(7,8,9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple.join(Tuple.of(7,8,9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple.join(Tuple.of(7,8,9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple.join(Tuple.of(7,8,9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6);

        Nullable<Tuple6<Integer, Integer, Integer, Integer, Integer, Integer>>
                maybeTuple = Tuple6.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3,4,5,6), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple6.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSplice_Pass() {
        Tuple2<Tuple1<Integer>,Tuple5<Integer,Integer,Integer,Integer,Integer>>
                spliced1 = tuple.splice(1);
        assertEquals(2, spliced1.depth());
        assertEquals(1, spliced1.value1().value1());
        assertEquals(6, spliced1.value2().value5());

        Tuple2<Tuple2<Integer,Integer>,Tuple4<Integer,Integer,Integer,Integer>>
                spliced2 = tuple.splice(2);
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(6, spliced2.value2().value4());

        Tuple2<Tuple3<Integer,Integer,Integer>,Tuple3<Integer,Integer,Integer>>
                spliced3 = tuple.splice(3);
        assertEquals(2, spliced3.depth());
        assertEquals(1, spliced3.value1().value1());
        assertEquals(6, spliced3.value2().value3());

        Tuple2<Tuple4<Integer,Integer,Integer,Integer>,Tuple2<Integer,Integer>>
                spliced4 = tuple.splice(4);
        assertEquals(2, spliced4.depth());
        assertEquals(1, spliced4.value1().value1());
        assertEquals(6, spliced4.value2().value2());

        Tuple2<Tuple5<Integer,Integer,Integer,Integer,Integer>,Tuple1<Integer>>
                spliced5 = tuple.splice(5);
        assertEquals(2, spliced5.depth());
        assertEquals(1, spliced5.value1().value1());
        assertEquals(6, spliced5.value2().value1());
    }

    @Test
    public void testTruncate_Pass() {
        Tuple1 aTuple1 = tuple.truncate(1);
        assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncate(2);
        assertEquals(Tuple.of(1,2),aTuple2);

        Tuple3 aTuple3 = tuple.truncate(3);
        assertEquals(Tuple.of(1,2,3),aTuple3);

        Tuple4 aTuple4 = tuple.truncate(4);
        assertEquals(Tuple.of(1,2,3,4),aTuple4);

        Tuple5 aTuple5 = tuple.truncate(5);
        assertEquals(Tuple.of(1,2,3,4,5),aTuple5);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTestTransform_Pass() {
        Tuple6 aTuple6;

        aTuple6 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0,2,3,4,5,6),aTuple6);

        aTuple6 = tuple.transform2(a -> 0);
        assertEquals(Tuple.of(1,0,3,4,5,6),aTuple6);

        aTuple6 = tuple.transform3(a -> 0);
        assertEquals(Tuple.of(1,2,0,4,5,6),aTuple6);

        aTuple6 = tuple.transform4(a -> 0);
        assertEquals(Tuple.of(1,2,3,0,5,6),aTuple6);

        aTuple6 = tuple.transform5(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,0,6),aTuple6);

        aTuple6 = tuple.transform6(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,0),aTuple6);
    }
}
