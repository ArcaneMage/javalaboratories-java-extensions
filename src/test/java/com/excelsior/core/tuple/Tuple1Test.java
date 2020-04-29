package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tuple1Test {

    private Tuple1<Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1,tuple.value1());
    }

    @Test
    public void testAdd_Pass() {
        Tuple2 tuple2 = tuple.add(1,"a");
        assertEquals(Tuple.of("a",1),tuple2);

        Tuple3 tuple3 = tuple2.add(2,"b");
        assertEquals(Tuple.of("a","b",1),tuple3);

        Tuple4 tuple4 = tuple3.add(3,"c");
        assertEquals(Tuple.of("a","b","c",1),tuple4);

        Tuple5 tuple5 = tuple4.add(4,"d");
        assertEquals(Tuple.of("a","b","c","d",1),tuple5);

        Tuple6 tuple6 = tuple5.add(5,"e");
        assertEquals(Tuple.of("a","b","c","d","e",1),tuple6);

        Tuple7 tuple7 = tuple6.add(6,"f");
        assertEquals(Tuple.of("a","b","c","d","e","f",1),tuple7);

        Tuple8 tuple8 = tuple7.add(7,"g");
        assertEquals(Tuple.of("a","b","c","d","e","f","g",1),tuple8);

        Tuple9 tuple9 = tuple8.add(8,"h");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h",1),tuple9);

        Tuple10 tuple10 = tuple9.add(9,"i");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i",1),tuple10);

        Tuple11 tuple11 = tuple10.add(10,"j");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j",1),tuple11);

        Tuple12 tuple12 = tuple11.add(11,"k");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j","k",1),tuple12);

        Tuple13 tuple13 = tuple12.add(12,"l");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j","k","l",1),tuple13);

        Tuple14 tuple14 = tuple13.add(13,"m");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j","k","l","m",1),tuple14);

        Tuple15 tuple15 = tuple14.add(14,"n");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n",1),tuple15);

        Tuple16 tuple16 = tuple15.add(15,"o");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o",1),tuple16);
    }


    @Test
    public void testJoin_Pass() {
        Tuple2 aTuple2_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple2_1.value2());

        Tuple2 aTuple2_2 = tuple.join(Tuple.of(2));
        assertEquals(aTuple2_2,Tuple.of(1,2));

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
    public void testTruncate_Pass() {
        Tuple0 aTuple0 = tuple.truncate(1);
        assertEquals(Tuple.of(),aTuple0);
    }

    @Test
    public void testRemove_Pass() {
        Tuple0 tuple1 = tuple.remove(1);
        assertEquals(Tuple.of(), tuple1);
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1);

        Nullable<Tuple1<Integer>>
                maybeTuple = Tuple1.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1), tuple),
                Assertions::fail);

        list = Collections.emptyList();
        maybeTuple = Tuple1.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSplice_Pass() {
        Tuple2<Tuple0,Tuple1<Integer>>
                spliced1 = tuple.splice(1);
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(1, spliced1.value2().value1());
    }

    @Test
    public void testSplice_Fail() {
        assertThrows(IllegalArgumentException.class, () -> tuple.splice(2));
    }

    @Test
    public void testTestTransform_Pass() {
        Tuple1 aTuple1;

        String s = tuple.transform(a -> "Item :"+a);

        assertEquals("Item :1",s);

        aTuple1 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0),aTuple1);
    }
}
