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

    private LRUCacheMap<Integer,String> cache, cache2;

    @BeforeEach
    public void setup() {
        cache = new LRUCacheMap<>(3);
        cache.put(1,"Alan");
        cache.put(2,"Brian");

        cache2 = new LRUCacheMap<>(3);
        cache2.put(1,null);
        cache2.put(2,"Ameca");
    }

    @Test
    public void testConstructor_Pass() {
        // Given
        LRUCacheMap<Integer,String> cache2 = new LRUCacheMap<>();
        LRUCacheMap<Integer,String> cache3 = new LRUCacheMap<>(cache); // Shallow copy

        // Then
        assertEquals(cache,cache3);
        assertEquals(3,cache.capacity());
        assertEquals(2,cache.size());
        assertEquals("Brian",cache.peekAt(0));
        assertEquals("Alan",cache.peekAt(1));

        assertEquals(16,cache2.capacity()); // Default Capacity

        assertThrows(IllegalArgumentException.class, () -> new LRUCacheMap<>(-1));
    }

    @Test
    public void testClone_Pass() {
        @SuppressWarnings("unchecked")
        LRUCacheMap<Integer,String> cache3 = (LRUCacheMap<Integer, String>) cache.clone(); // Shallow Copy

        assertEquals(cache,cache3);
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
        cache.put(4,"Andy"); // Alan is evicted

        // Then
        assertEquals(3, cache.size());
        assertEquals("Andy",cache.peekAt(0));
        assertEquals("James",cache.peekAt(1));
        assertEquals("Brian",cache.peekAt(2));
    }

    @Test
    public void testPut_Modify_Pass() {
        // Given
        String oldValue = cache.put(1,"Alania"); // Alan -> Alania

        // Then
        assertEquals("Alan", oldValue);
        assertEquals(2, cache.size());
        assertEquals("Alania",cache.peekAt(0));
        assertEquals("Brian",cache.peekAt(1));
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
    public void testNudge_Pass() {
        // Given
        cache.put(3,"James");
        boolean nudge = cache.nudge(2); // Nudge Brian

        // Then
        assertTrue(nudge);
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
        assertEquals("[[2 -> Brian],[1 -> Alan]]",s);
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
    public void testPeekAt_Pass() {
        // Given
        String brian = cache.peekAt(0);
        String alan = cache.peekAt(1);

        // Then
        assertEquals(2, cache.size());
        assertEquals("Brian",brian);
        assertEquals("Alan",alan);
        assertThrows(IndexOutOfBoundsException.class,() -> cache.peekAt(-1));
        assertThrows(IndexOutOfBoundsException.class,() -> cache.peekAt(9));
    }

    @Test
    public void testRemove_Pass() {
        // Given
        String removed1 = cache.remove(2); // Remove Brian
        String removed2 = cache.remove(99); // Doesn't exist

        // Then
        assertEquals(1,cache.size());
        assertEquals(3,cache.capacity());
        assertEquals("Brian",removed1);
        assertNull(removed2);
    }

    @Test
    public void testResetKeys_Pass() {
        // Given
        LRUCacheMap<Integer,String> cache2 = cache.resetKeys((k,v) -> k + 1);

        // Then
        assertEquals("[[2 -> Brian],[1 -> Alan]]",cache.toString());
        assertEquals("[[3 -> Brian],[2 -> Alan]]",cache2.toString());
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
        cache3.put(3,30);

        LRUCacheMap<Integer,String> cache4 = new LRUCacheMap<>(3);
        cache4.put(1,"Alan");
        cache4.put(2,null);

        LRUCacheMap<Integer,Integer> cache5 = new LRUCacheMap<>(3);
        cache3.put(1,10);
        cache3.put(2,20);

        String cache6 = "This is not LRUCacheMap";

        // Then
        assertEquals(cache,cache2);
        assertEquals(cache.hashCode(),cache2.hashCode());

        assertNotEquals(cache,cache3);
        assertNotEquals(cache,cache4);
        assertNotEquals(this.cache2,cache4);
        assertNotEquals(cache,cache5);
        assertNotEquals(cache,cache6);
    }

    @Test
    public void testClear_Pass() {
        // Given
        cache.clear();

        // Then
        assertEquals(0,cache.size());
        assertEquals(3,cache.capacity());
    }
}
