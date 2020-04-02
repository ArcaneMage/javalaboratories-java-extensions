package com.excelsior.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HolderTest {
    private Holder<String> holder;

    @BeforeEach
    public void setup() {
        holder = new Holder<>("Hello World");
    }

    @Test
    public void testSet_Pass() {
        holder.set("Hello Galaxy");
        assertEquals("Hello Galaxy",holder.get());
    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World",holder.get());
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(new Holder<>("Hello World"), holder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals((new Holder<>("Hello World")).hashCode(),holder.hashCode());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", holder.toString());
    }

}
