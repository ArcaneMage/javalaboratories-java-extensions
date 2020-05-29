package org.javalaboratories.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matcher2Test {
    private Matcher2<Integer,Integer> matcher;

    @BeforeEach
    public void setup() {
        matcher = Matcher.all(1,2);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1, matcher.value1());
        assertEquals(2, matcher.value2());
    }
}
