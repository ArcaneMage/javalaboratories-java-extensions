package com.excelsior.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class Tuple0Test {

    private Tuple0 tuple;
    private Object value;

    @BeforeEach
    public void setup() {
        tuple = Tuple.of();
        value = new Object();
    }

    @Test
    public void testAdd_Fail() {
        assertThrows(UnsupportedOperationException.class, () -> tuple.add(0,value));
    }

    @Test
    public void testContains_Pass() {
        assertFalse(tuple.contains(value));
    }

    @Test
    public void testDepth_Pass() {
        assertEquals(0,tuple.depth());
    }

    @Test
    public void testHop_Fail() {
        assertThrows(UnsupportedOperationException.class, () -> tuple.hop(1));
    }

    @Test
    public void testJoin_Pass() {
        Tuple1 aTuple1 = tuple.join(Tuple.of(1));
        assertEquals(aTuple1,Tuple.of(1));

        Tuple2 aTuple2 = tuple.join(Tuple.of(1,2));
        assertEquals(aTuple2,Tuple.of(1,2));

        Tuple3 aTuple3 = tuple.join(Tuple.of(1,2,3));
        assertEquals(aTuple3,Tuple.of(1,2,3));

        Tuple6 aTuple6 = aTuple3.join(Tuple.of(1,2,3));
        assertEquals(Tuple.of(1,2,3,1,2,3), aTuple6);
    }

    @Test
    public void testTruncate_Fail() {
        assertThrows(UnsupportedOperationException.class, () -> tuple.truncate(1));
    }

    @Test
    public void testSplice_Fail() {
        assertThrows(UnsupportedOperationException.class, () -> tuple.splice(0));
    }

    @Test
    public void testPositionOf_Pass() {
        assertEquals(0,tuple.positionOf(value));
    }

    @Test
    public void testToArray_Pass() {
        assertEquals(0, tuple.toArray().length);
    }

    @Test
    public void testToMap_Pass() {
        assertEquals(0, tuple.toMap(k -> "index"+k).size());
    }

    @Test
    public void testToList_Pass() {
        assertEquals(0, tuple.toList().size());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Tuple0=[]",tuple.toString());
    }

    @Test
    public void testCompareTo_Pass() {
        assertEquals(0, tuple.compareTo(Tuple.of()));
    }

    @Test
    public void testCompareTo_Fail() {
        assertThrows(NullPointerException.class, () -> tuple.compareTo(null));
        assertThrows(ClassCastException.class, () -> tuple.compareTo(Tuple.of(1)));
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Tuple.of(),tuple);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Tuple.of().hashCode(), tuple.hashCode());
    }

    @Test
    public void testIterator_Pass() {
        Iterator<Object> iter = tuple.iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testIterator_Fail() {
        Iterator<Object> iter = tuple.iterator();
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

}
