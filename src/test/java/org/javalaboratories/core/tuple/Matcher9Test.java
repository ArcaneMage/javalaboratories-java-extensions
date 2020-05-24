package org.javalaboratories.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matcher9Test {
    private Matcher9<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer,
            Integer> matcher;

    @BeforeEach
    public void setup() {
        matcher = Matcher.when(1,2,3,4,5,6,7,8,9);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1, matcher.value1());
        assertEquals(2, matcher.value2());
        assertEquals(3, matcher.value3());
        assertEquals(4, matcher.value4());
        assertEquals(5, matcher.value5());
        assertEquals(6, matcher.value6());
        assertEquals(7, matcher.value7());
        assertEquals(8, matcher.value8());
        assertEquals(9, matcher.value9());
    }
}
