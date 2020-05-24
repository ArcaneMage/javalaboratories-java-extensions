package org.javalaboratories.core.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Matcher1Test {
    private Matcher1<Integer> matcher;

    @BeforeEach
    public void setup() {
        matcher = Matcher.when(1);
    }

    @Test
    public void testValue_Pass() {
        assertEquals(1, matcher.value1());
    }
}
