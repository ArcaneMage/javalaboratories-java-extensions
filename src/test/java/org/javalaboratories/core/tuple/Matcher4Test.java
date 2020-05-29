package org.javalaboratories.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matcher4Test {
    private Matcher4<Integer,Integer,Integer,Integer> matcher;

    @BeforeEach
    public void setup() {
        matcher = Matcher.all(1,2,3,4);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1, matcher.value1());
        assertEquals(2, matcher.value2());
        assertEquals(3, matcher.value3());
        assertEquals(4, matcher.value4());
    }
}
