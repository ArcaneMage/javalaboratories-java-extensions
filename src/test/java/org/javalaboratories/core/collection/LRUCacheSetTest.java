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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LRUCacheSetTest {
    private LRUCacheSet<String> cache;

    @BeforeEach
    public void setup() {
        cache = new LRUCacheSet<>(3);
        cache.add("Alan");
        cache.add("Brian");
    }

    @Test
    public void testConstructor_Pass() {
        // Given
        LRUCacheSet<String> cache2 = new LRUCacheSet<>();
        LRUCacheSet<String> copy = new LRUCacheSet<>(cache);

        // Then
        assertEquals(cache,copy);
        assertEquals(3,cache.capacity());
        assertEquals(2,cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("Alan",cache.peekAt(1));

        assertEquals(16,cache2.capacity()); // Default Capacity

        assertThrows(IllegalArgumentException.class, () -> new LRUCacheSet<>(-1));
    }

    @Test
    public void testAdd_Eviction_Pass() {
        // Given
        cache.add("James");
        cache.add("Andy");

        // Then
        assertEquals(3,cache.size());
        assertEquals("Andy",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Brian",cache.peekAt(2));
    }

    @Test
    public void testClone_Pass() {
        LRUCacheSet<String> copy = (LRUCacheSet<String>) cache.clone(); // Shallow Copy

        assertEquals(cache,copy);
    }

    @Test
    public void testAdd_Modify_Pass() {
        // Given
        cache.add("James");
        cache.add("Brian"); // "Nudge" Brian, promote to top.

        // Then
        assertEquals(3,cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
    }

    @Test
    public void testEqualsHashCode_Pass() {
        // Given
        LRUCacheSet<String> cache2 = new LRUCacheSet<>(3);
        cache2.add("Alan");
        cache2.add("Brian");

        LRUCacheSet<Integer> cache3 = new LRUCacheSet<>(3);
        cache3.add(10);
        cache3.add(20);
        cache3.add(30);

        LRUCacheSet<String> cache4 = new LRUCacheSet<>(3);
        cache4.add("Alan");
        cache4.add(null);

        // Then
        assertEquals(cache,cache2);
        assertEquals(cache.hashCode(),cache2.hashCode());

        assertNotEquals(cache,cache3);
        assertNotEquals(cache,cache4);
    }

    @Test
    public void testNudge_Pass() {
        // Given
        cache.add("James");
        cache.nudge("Brian"); // Nudge Brian, promote to top.

        // Then
        assertEquals(3, cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
    }

    @Test
    public void testGet_Pass() {
        // Given
        cache.add("James");
        String value = cache.get("Brian"); // Get and nudge Brian, promote to top.

        // Then
        assertEquals(3, cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Alan",cache.peekAt(2));
        assertEquals("Brian",value);
    }

    @Test
    public void testPeekAt_Pass() {
        // Given: BeforeEach

        // Then
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("Alan",cache.peekAt(1));
        assertThrows(IndexOutOfBoundsException.class, () -> cache.peekAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> cache.peekAt(99));
    }

    @Test
    public void testToString_Pass() {
        // Given:
        String s = cache.toString();

        // Then
        assertEquals("[Brian,Alan]",s);
    }
}
