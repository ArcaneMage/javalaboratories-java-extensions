/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LRUCacheMapTest {

    private LRUCacheMap<Integer,String> cache;

    @BeforeEach
    public void setup() {
        cache = new LRUCacheMap<>(3);
        cache.put(1,"Alan");
        cache.put(2,"Brian");
    }

    @Test
    public void testConstructor_Pass() {
        // Given: BeforeEach

        // Then
        assertEquals(3, cache.capacity());
        assertEquals(2, cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("Alan",cache.peekAt(1));
    }

    @Test
    public void testPut_Pass() {
        // Given
        cache.put(3,"James");

        // Then
        assertEquals(3, cache.size());
        assertEquals("James",cache.peekAt(0));
        assertEquals("Brian",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
    }

    @Test
    public void testPut_Eviction_Pass() {
        // Given
        cache.put(3,"James");
        cache.put(4,"Andy");

        // Then
        assertEquals(3, cache.size());
        assertEquals("Andy",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Brian",cache.peekAt(2));
    }

    @Test
    public void testGet_Pass() {
        // Given
        cache.put(3,"James");
        cache.get(2); // Retrieve Brian

        // Then
        assertEquals(3, cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
    }

    @Test
    public void testGet_NoMapping_Pass() {
        // Given
        String value = cache.get(99); // None existent

        // Then
        assertNull(value);
        assertEquals(2, cache.size());
    }

    @Test
    public void testToString_Pass() {
        // Given
        String s = cache.toString();

        // Then
        assertEquals("[(2 -> Brian),(1 -> Alan)]",s);
    }

    @Test
    public void testPeek_Pass() {
        // Given
        cache.put(3,"James");
        cache.peek(2); // "Peek" at Brian: queue position unchanged

        // Then
        assertEquals(3, cache.size());
        assertEquals("James",cache.peekAt(0));
        assertEquals("Brian",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
    }

    @Test
    public void testEqualsHashCode_Pass() {
        // Given
        LRUCacheMap<Integer,String> cache2 = new LRUCacheMap<>(3);
        cache2.put(1,"Alan");
        cache2.put(2,"Brian");

        LRUCacheMap<Integer,Integer> cache3 = new LRUCacheMap<>(3);
        cache3.put(1,10);
        cache3.put(2,20);


        // Then
        assertEquals(cache,cache2);
        assertEquals(cache.hashCode(),cache2.hashCode());

        assertNotEquals(cache,cache3);
    }
}
