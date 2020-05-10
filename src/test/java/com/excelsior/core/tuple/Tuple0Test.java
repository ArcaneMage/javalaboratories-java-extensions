package com.excelsior.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.excelsior.core.tuple.Tuple.of;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class Tuple0Test {

    private Tuple0 tuple;
    private Object value;

    @BeforeEach
    public void setup() {
        tuple = of();
        value = new Object();
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
    public void testJoin_Pass() {
        Tuple0 aTuple0 = tuple.join(of());
        assertEquals(of(),aTuple0);

        Tuple1 aTuple1 = tuple.join(of(1));
        assertEquals(aTuple1, of(1));

        Tuple2 aTuple2 = tuple.join(of(1,2));
        assertEquals(aTuple2, of(1,2));

        Tuple3 aTuple3 = tuple.join(of(1,2,3));
        assertEquals(aTuple3, of(1,2,3));

        Tuple6 aTuple6 = aTuple3.join(of(1,2,3));
        assertEquals(of(1,2,3,1,2,3), aTuple6);
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
        assertEquals(0, tuple.compareTo(of()));
    }

    @Test
    public void testCompareTo_Fail() {
        assertThrows(NullPointerException.class, () -> tuple.compareTo(null));
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(of(),tuple);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(of().hashCode(), tuple.hashCode());
    }

    @Test
    public void testIterator_Pass() {
        Iterator<Object> it = tuple.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void testIterator_Fail() {
        Iterator<Object> it = tuple.iterator();
        assertThrows(NoSuchElementException.class, () -> it.next());
    }

    @Test
    public void testValue_Fail() {
        assertThrows(IllegalArgumentException.class, () -> tuple.value(0));
    }
}
