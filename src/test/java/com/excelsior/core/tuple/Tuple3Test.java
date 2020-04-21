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

    private Tuple3 tuple;

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
    public void testJoin_Pass() {
        Tuple4 aTuple4 = tuple.join(Tuple.of(4));
        assertEquals(aTuple4,Tuple.of(1,2,3,4));

        Tuple5 aTuple5 = tuple.join(Tuple.of(4,5));
        assertEquals(aTuple5,Tuple.of(1,2,3,4,5));

        Tuple6 aTuple6 = tuple.join(Tuple.of(4,5,6));
        assertEquals(aTuple6,Tuple.of(1,2,3,4,5,6));

        Tuple7 aTuple7 = tuple.join(Tuple.of(4,5,6,7));
        assertEquals(aTuple7,Tuple.of(1,2,3,4,5,6,7));

        Tuple8 aTuple8 = tuple.join(Tuple.of(4,5,6,7,8));
        assertEquals(aTuple8,Tuple.of(1,2,3,4,5,6,7,8));

        Tuple9 aTuple9 = tuple.join(Tuple.of(4,5,6,7,8,9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple.join(Tuple.of(4,5,6,7,8,9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple.join(Tuple.of(4,5,6,7,8,9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    public void testToTuple_Pass() {
        List<Integer> list = Arrays.asList(1,2,3);

        Nullable<Tuple3<Integer, Integer, Integer>>
                maybeTuple = Tuple3.toTuple(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple3.toTuple(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testTruncate_Pass() {
        Tuple1 aTuple1 = tuple.truncate1();
        assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncate2();
        assertEquals(Tuple.of(1,2),aTuple2);
    }

    @Test
    @SuppressWarnings("unchecked")
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
