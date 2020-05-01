package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tuple10Test {

    private Tuple10<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer> tuple;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of(1,2,3,4,5,6,7,8,9,10);
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
        assertEquals(9,tuple.value9());
        assertEquals(10,tuple.value10());
    }

    @Test
    public void testAdd_Pass() {
        Tuple11 tuple3 = tuple.add(1,"a");
        assertEquals(Tuple.of("a",1,2,3,4,5,6,7,8,9,10),tuple3);

        Tuple12 tuple4 = tuple3.add(2,"b");
        assertEquals(Tuple.of("a","b",1,2,3,4,5,6,7,8,9,10),tuple4);

        Tuple13 tuple5 = tuple4.add(3,"c");
        assertEquals(Tuple.of("a","b","c",1,2,3,4,5,6,7,8,9,10),tuple5);

        Tuple14 tuple6 = tuple5.add(4,"d");
        assertEquals(Tuple.of("a","b","c","d",1,2,3,4,5,6,7,8,9,10),tuple6);

        Tuple15 tuple7 = tuple6.add(5,"e");
        assertEquals(Tuple.of("a","b","c","d","e",1,2,3,4,5,6,7,8,9,10),tuple7);

        Tuple16 tuple8 = tuple7.add(6,"f");
        assertEquals(Tuple.of("a","b","c","d","e","f",1,2,3,4,5,6,7,8,9,10),tuple8);
    }
    
    @Test
    public void testJoin_Pass() {
        Tuple10 aTuple10 = tuple.join(Tuple.of());
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10),aTuple10);

        Tuple11 aTuple11_1 = tuple.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple11_1.value11());

        Tuple11 aTuple11_2 = tuple.join(Tuple.of(11));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11),aTuple11_2);

        Tuple12 aTuple12 = tuple.join(Tuple.of(11,12));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12),aTuple12);

        Tuple13 aTuple13 = tuple.join(Tuple.of(11,12,13));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13),aTuple13);

        Tuple14 aTuple14 = tuple.join(Tuple.of(11,12,13,14));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14),aTuple14);

        Tuple15 aTuple15 = tuple.join(Tuple.of(11,12,13,14,15));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),aTuple15);

        Tuple16 aTuple16 = tuple.join(Tuple.of(11,12,13,14,15,16));
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),aTuple16);
    }

    @Test
    public void testFromIterable_Pass() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        Nullable<Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>
                maybeTuple = Tuple10.fromIterable(list);

        maybeTuple.ifPresentOrElse(tuple -> assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10), tuple),
                Assertions::fail);

        list = Arrays.asList(1);
        maybeTuple = Tuple10.fromIterable(list);

        assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testSplice_Pass() {
        Tuple2<Tuple0,Tuple10<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,
                Integer>> spliced1 = tuple.splice(1);
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(10, spliced1.value2().value10());

        Tuple2<Tuple1<Integer>,Tuple9<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,
                Integer>> spliced2 = tuple.splice(2);
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(10, spliced2.value2().value9());

        Tuple2<Tuple2<Integer,Integer>,Tuple8<Integer,Integer,Integer,Integer,Integer,Integer,Integer,
                Integer>> spliced3 = tuple.splice(3);
        assertEquals(2, spliced3.depth());
        assertEquals(1, spliced3.value1().value1());
        assertEquals(10, spliced3.value2().value8());

        Tuple2<Tuple3<Integer,Integer,Integer>,Tuple7<Integer,Integer,Integer,Integer,Integer,Integer,
                Integer>> spliced4 = tuple.splice(4);
        assertEquals(2, spliced4.depth());
        assertEquals(1, spliced4.value1().value1());
        assertEquals(10, spliced4.value2().value7());

        Tuple2<Tuple4<Integer,Integer,Integer,Integer>,Tuple6<Integer,Integer,Integer,Integer,Integer,
                Integer>> spliced5 = tuple.splice(5);
        assertEquals(2, spliced5.depth());
        assertEquals(1, spliced5.value1().value1());
        assertEquals(10, spliced5.value2().value6());

        Tuple2<Tuple5<Integer,Integer,Integer,Integer,Integer>,Tuple5<Integer,Integer,Integer,Integer,
                Integer>> spliced6 = tuple.splice(6);
        assertEquals(2, spliced6.depth());
        assertEquals(1, spliced6.value1().value1());
        assertEquals(10, spliced6.value2().value5());

        Tuple2<Tuple6<Integer,Integer,Integer,Integer,Integer,Integer>,Tuple4<Integer,Integer,Integer,
                Integer>> spliced7 = tuple.splice(7);
        assertEquals(2, spliced7.depth());
        assertEquals(1, spliced7.value1().value1());
        assertEquals(10, spliced7.value2().value4());

        Tuple2<Tuple7<Integer,Integer,Integer,Integer,Integer,Integer,Integer>,Tuple3<Integer,Integer,
                Integer>> spliced8 = tuple.splice(8);
        assertEquals(2, spliced8.depth());
        assertEquals(1, spliced8.value1().value1());
        assertEquals(10, spliced8.value2().value3());

        Tuple2<Tuple8<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>,Tuple2<Integer,
                Integer>> spliced9 = tuple.splice(9);
        assertEquals(2, spliced9.depth());
        assertEquals(1, spliced9.value1().value1());
        assertEquals(10, spliced9.value2().value2());

        Tuple2<Tuple9<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>,
                Tuple1<Integer>> spliced10 = tuple.splice(10);
        assertEquals(2, spliced10.depth());
        assertEquals(1, spliced10.value1().value1());
        assertEquals(10, spliced10.value2().value1());
    }


    @Test
    public void testRemove_Pass() {
        Tuple9 tuple9 = tuple.remove(10);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9),tuple9);

        Tuple9 tuple9_2 = tuple.remove(7);
        assertEquals(Tuple.of(1,2,3,4,5,6,8,9,10),tuple9_2);

        Tuple9 tuple9_3 = tuple.remove(1);
        assertEquals(Tuple.of(2,3,4,5,6,7,8,9,10),tuple9_3);
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

        Tuple7 aTuple7 = tuple.truncate(8);
        assertEquals(Tuple.of(1,2,3,4,5,6,7),aTuple7);

        Tuple8 aTuple8 = tuple.truncate(9);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8),aTuple8);

        Tuple9 aTuple9 = tuple.truncate(10);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9),aTuple9);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTestTransform_Pass() {
        Tuple10 aTuple10;

        aTuple10 = tuple.transform1(a -> 0);
        assertEquals(Tuple.of(0,2,3,4,5,6,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform2(a -> 0);
        assertEquals(Tuple.of(1,0,3,4,5,6,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform3(a -> 0);
        assertEquals(Tuple.of(1,2,0,4,5,6,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform4(a -> 0);
        assertEquals(Tuple.of(1,2,3,0,5,6,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform5(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,0,6,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform6(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,0,7,8,9,10),aTuple10);

        aTuple10 = tuple.transform7(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,0,8,9,10),aTuple10);

        aTuple10 = tuple.transform8(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,0,9,10),aTuple10);

        aTuple10 = tuple.transform9(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,0,10),aTuple10);

        aTuple10 = tuple.transform10(a -> 0);
        assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,0),aTuple10);
    }
}
