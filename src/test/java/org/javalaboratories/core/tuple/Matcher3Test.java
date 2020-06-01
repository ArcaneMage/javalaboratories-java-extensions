package org.javalaboratories.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matcher3Test {
    private Matcher3<Integer,Integer,Integer> matcher;

    @BeforeEach
    public void setup() {
        matcher = Matcher.allOf(1,2,3);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1, matcher.value1());
        assertEquals(2, matcher.value2());
        assertEquals(3, matcher.value3());
    }
}
