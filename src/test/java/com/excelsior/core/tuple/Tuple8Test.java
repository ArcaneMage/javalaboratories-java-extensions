package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple8Test {

    private Tuple8 tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3,4,5,6,7,8);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
        assertEquals(2,tuple.value2());
        assertEquals(3,tuple.value3());
        assertEquals(4,tuple.value4());
        assertEquals(5,tuple.value5());
        assertEquals(6,tuple.value6());
        assertEquals(7,tuple.value7());
        assertEquals(8,tuple.value8());
    }

    @Test
    public void testJoin_Pass() {
        Tuple9 aTuple9 = tuple.join(Tuple.of(9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple.join(Tuple.of(9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple.join(Tuple.of(9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple.join(Tuple.of(9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple.join(Tuple.of(9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple.join(Tuple.of(9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple.join(Tuple.of(9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple.join(Tuple.of(9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    public void testToTuple_Pass() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8);

        Nullable<Tuple8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                maybeTuple = Tuple8.toTuple(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3,4,5,6,7,8), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple8.toTuple(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testTruncate_Pass() {
        Tuple1 aTuple1 = tuple.truncate1();
        assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncate2();
        assertEquals(Tuple.of(1,2),aTuple2);

        Tuple3 aTuple3 = tuple.truncate3();
        assertEquals(Tuple.of(1,2,3),aTuple3);

        Tuple4 aTuple4 = tuple.truncate4();
        assertEquals(Tuple.of(1,2,3,4),aTuple4);

        Tuple5 aTuple5 = tuple.truncate5();
        assertEquals(Tuple.of(1,2,3,4,5),aTuple5);

        Tuple6 aTuple6 = tuple.truncate6();
        assertEquals(Tuple.of(1,2,3,4,5,6),aTuple6);

        Tuple7 aTuple7 = tuple.truncate7();
        assertEquals(Tuple.of(1,2,3,4,5,6,7),aTuple7);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTestTransform_Pass() {
        Tuple8 aTuple8;

        aTuple8 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0,2,3,4,5,6,7,8),aTuple8);

        aTuple8 = tuple.transform2(a -> 0);
        assertEquals(Tuple.of(1,0,3,4,5,6,7,8),aTuple8);

        aTuple8 = tuple.transform3(a -> 0);
        assertEquals(Tuple.of(1,2,0,4,5,6,7,8),aTuple8);

        aTuple8 = tuple.transform4(a -> 0);
        assertEquals(Tuple.of(1,2,3,0,5,6,7,8),aTuple8);

        aTuple8 = tuple.transform5(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,0,6,7,8),aTuple8);

        aTuple8 = tuple.transform6(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,0,7,8),aTuple8);

        aTuple8 = tuple.transform7(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,0,8),aTuple8);

        aTuple8 = tuple.transform8(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,0),aTuple8);
     }
}