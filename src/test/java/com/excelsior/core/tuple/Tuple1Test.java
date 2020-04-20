package com.excelsior.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tuple1Test {

    private Tuple1 tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
    }

    @Test
    public void testJoin_Pass() {
        Tuple2 aTuple2 = tuple.join(Tuple.of(2));
        assertEquals(aTuple2,Tuple.of(1,2));

        Tuple3 aTuple3 = tuple.join(Tuple.of(2,3));
        assertEquals(aTuple3,Tuple.of(1,2,3));

        Tuple4 aTuple4 = tuple.join(Tuple.of(2,3,4));
        assertEquals(aTuple4,Tuple.of(1,2,3,4));

        Tuple5 aTuple5 = tuple.join(Tuple.of(2,3,4,5));
        assertEquals(aTuple5,Tuple.of(1,2,3,4,5));

        Tuple6 aTuple6 = tuple.join(Tuple.of(2,3,4,5,6));
        assertEquals(aTuple6,Tuple.of(1,2,3,4,5,6));

        Tuple7 aTuple7 = tuple.join(Tuple.of(2,3,4,5,6,7));
        assertEquals(aTuple7,Tuple.of(1,2,3,4,5,6,7));

        Tuple8 aTuple8 = tuple.join(Tuple.of(2,3,4,5,6,7,8));
        assertEquals(aTuple8,Tuple.of(1,2,3,4,5,6,7,8));

        Tuple9 aTuple9 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple.join(Tuple.of(2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTestTransform_Pass() {
        Tuple1 aTuple1;

        aTuple1 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0),aTuple1);
    }
}
