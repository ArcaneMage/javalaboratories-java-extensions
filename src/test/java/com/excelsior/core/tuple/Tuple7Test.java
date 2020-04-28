package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple7Test {

    private Tuple7<Integer,Integer,Integer,Integer,Integer,Integer,Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3,4,5,6,7);
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
    }


    @Test
    public void testAdd_Pass() {
        Tuple8 tuple3 = tuple.add(1,"a");
        assertEquals(Tuple.of("a",1,2,3,4,5,6,7),tuple3);

        Tuple9 tuple4 = tuple3.add(2,"b");
        assertEquals(Tuple.of("a","b",1,2,3,4,5,6,7),tuple4);

        Tuple10 tuple5 = tuple4.add(3,"c");
        assertEquals(Tuple.of("a","b","c",1,2,3,4,5,6,7),tuple5);

        Tuple11 tuple6 = tuple5.add(4,"d");
        assertEquals(Tuple.of("a","b","c","d",1,2,3,4,5,6,7),tuple6);

        Tuple12 tuple7 = tuple6.add(5,"e");
        assertEquals(Tuple.of("a","b","c","d","e",1,2,3,4,5,6,7),tuple7);

        Tuple13 tuple8 = tuple7.add(6,"f");
        assertEquals(Tuple.of("a","b","c","d","e","f",1,2,3,4,5,6,7),tuple8);

        Tuple14 tuple9 = tuple8.add(7,"g");
        assertEquals(Tuple.of("a","b","c","d","e","f","g",1,2,3,4,5,6,7),tuple9);

        Tuple15 tuple10 = tuple9.add(8,"h");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h",1,2,3,4,5,6,7),tuple10);

        Tuple16 tuple11 = tuple10.add(9,"i");
        assertEquals(Tuple.of("a","b","c","d","e","f","g","h","i",1,2,3,4,5,6,7),tuple11);
    }
    
    @Test
    public void testJoin_Pass() {
        Tuple8 aTuple8_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple8_1.value8());

        Tuple8 aTuple8_2 = tuple.join(Tuple.of(8));
        assertEquals(aTuple8_2,Tuple.of(1,2,3,4,5,6,7,8));

        Tuple9 aTuple9 = tuple.join(Tuple.of(8,9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple.join(Tuple.of(8,9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple.join(Tuple.of(8,9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple.join(Tuple.of(8,9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple.join(Tuple.of(8,9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple.join(Tuple.of(8,9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple.join(Tuple.of(8,9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple.join(Tuple.of(8,9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);

        Nullable<Tuple7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                maybeTuple = Tuple7.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3,4,5,6,7), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple7.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSplice_Pass() {
        Tuple2<Tuple0,Tuple7<Integer,Integer,Integer,Integer,Integer,Integer,Integer>>
                spliced1 = tuple.splice(1);
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(7, spliced1.value2().value7());

        Tuple2<Tuple1<Integer>,Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>>
                spliced2 = tuple.splice(2);
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(7, spliced2.value2().value6());

        Tuple2<Tuple2<Integer,Integer>,Tuple5<Integer,Integer,Integer,Integer,Integer>>
                spliced3 = tuple.splice(3);
        assertEquals(2, spliced3.depth());
        assertEquals(1, spliced3.value1().value1());
        assertEquals(7, spliced3.value2().value5());

        Tuple2<Tuple3<Integer,Integer,Integer>,Tuple4<Integer,Integer,Integer,Integer>>
                spliced4 = tuple.splice(4);
        assertEquals(2, spliced4.depth());
        assertEquals(1, spliced4.value1().value1());
        assertEquals(7, spliced4.value2().value4());

        Tuple2<Tuple4<Integer,Integer,Integer,Integer>,Tuple3<Integer,Integer,Integer>>
                spliced5 = tuple.splice(5);
        assertEquals(2, spliced5.depth());
        assertEquals(1, spliced5.value1().value1());
        assertEquals(7, spliced5.value2().value3());

        Tuple2<Tuple5<Integer,Integer,Integer,Integer,Integer>,Tuple2<Integer,Integer>>
                spliced6 = tuple.splice(6);
        assertEquals(2, spliced6.depth());
        assertEquals(1, spliced6.value1().value1());
        assertEquals(7, spliced6.value2().value2());

        Tuple2<Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>,Tuple1<Integer>>
                spliced7 = tuple.splice(7);
        assertEquals(2, spliced7.depth());
        assertEquals(1, spliced7.value1().value1());
        assertEquals(7, spliced7.value2().value1());
    }

    @Test
    public void testTruncate_Pass() {
        Tuple0 aTuple0 = tuple.truncate(1);
        assertEquals(Tuple.of(),aTuple0);

        Tuple1 aTuple1 = tuple.truncate(2);
        assertEquals(Tuple.of(1),aTuple1);

        Tuple2 aTuple2 = tuple.truncate(3);
        assertEquals(Tuple.of(1,2),aTuple2);

        Tuple3 aTuple3 = tuple.truncate(4);
        assertEquals(Tuple.of(1,2,3),aTuple3);

        Tuple4 aTuple4 = tuple.truncate(5);
        assertEquals(Tuple.of(1,2,3,4),aTuple4);

        Tuple5 aTuple5 = tuple.truncate(6);
        assertEquals(Tuple.of(1,2,3,4,5),aTuple5);

        Tuple6 aTuple6 = tuple.truncate(7);
        assertEquals(Tuple.of(1,2,3,4,5,6),aTuple6);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTestTransform_Pass() {
        Tuple7 aTuple7;

        aTuple7 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0,2,3,4,5,6,7),aTuple7);

        aTuple7 = tuple.transform2(a -> 0);
        assertEquals(Tuple.of(1,0,3,4,5,6,7),aTuple7);

        aTuple7 = tuple.transform3(a -> 0);
        assertEquals(Tuple.of(1,2,0,4,5,6,7),aTuple7);

        aTuple7 = tuple.transform4(a -> 0);
        assertEquals(Tuple.of(1,2,3,0,5,6,7),aTuple7);

        aTuple7 = tuple.transform5(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,0,6,7),aTuple7);

        aTuple7 = tuple.transform6(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,0,7),aTuple7);

        aTuple7 = tuple.transform7(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,0),aTuple7);
    }
}
