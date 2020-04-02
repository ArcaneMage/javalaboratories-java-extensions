package com.excelsior.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HolderTest {
    private Holder<String> mutableHolder;
    private Holder<String> immutableHolder;
    private Holder<String> synchronizedHolder;

    @BeforeEach
    public void setup() {
        mutableHolder = Holders.mutableHolder("Hello World");
        immutableHolder = Holders.immutableHolder("Hello World");
        synchronizedHolder = Holders.synchronizedHolder("Hello World");
    }

    @Test
    public void testSet_Pass() {
        mutableHolder.set("Hello Galaxy");
        synchronizedHolder.set("Hello Galaxy");
        assertThrows(UnsupportedOperationException.class, () -> immutableHolder.set("Hello Galaxy"));
        assertEquals("Hello Galaxy", mutableHolder.get());
        assertEquals("Hello Galaxy", synchronizedHolder.get());

    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World", mutableHolder.get());
        assertEquals("Hello World", immutableHolder.get());
        assertEquals("Hello World", synchronizedHolder.get());
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Holders.mutableHolder("Hello World"), mutableHolder);
        assertEquals(Holders.immutableHolder("Hello World"), immutableHolder);
        assertEquals(Holders.synchronizedHolder("Hello World"), synchronizedHolder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Holders.mutableHolder("Hello World").hashCode(), mutableHolder.hashCode());
        assertEquals(Holders.mutableHolder("Hello World").hashCode(), immutableHolder.hashCode());
        assertEquals(Holders.mutableHolder("Hello World").hashCode(), synchronizedHolder.hashCode());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", mutableHolder.toString());
        assertEquals("Holder[value=Hello World]", immutableHolder.toString());
        assertEquals("Holder[value=Hello World]", synchronizedHolder.toString());
    }

}
