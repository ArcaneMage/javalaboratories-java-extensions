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
package org.javalaboratories.core.util.Holders;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongHoldersTest {
    private static final Logger logger = LoggerFactory.getLogger(LongHoldersTest.class);

    @Test
    public void testCollect_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        String result = numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .collect(() -> Holder.of(0L),(a,b) -> a.setGet(v -> v + b),(a,b) -> a.setGet(v -> v + b.fold(0L,n -> n)))
               .map(n -> n / 2)
               .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15",result);
        logger.info(result);
    }

    @Test
    public void testReadOnly_Pass() {
        Holder<Long> holder = LongHolders.readOnly(5L);

        assertThrows(UnsupportedOperationException.class, () -> holder.set(2L));
    }

    @Test
    public void testCollectSumming_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        String result = numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .map(Long::valueOf)
               .collect(LongHolders.summing())
               .map(n -> n / 2)
               .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15",result);
        logger.info(result);
    }

    @Test
    public void testCollectMax_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,11,10,1,2,3,4);
        String result = numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .map(Long::valueOf)
               .collect(LongHolders.max())
               .map(n -> n / 2)
               .fold("",n -> STR."Maximum of even numbers (10) / 2 = \{n}");

        assertEquals("Maximum of even numbers (10) / 2 = 5",result);
        logger.info(result);
    }

    @Test
    public void testCollectMin_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,11,10,1,2,3,4);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .map(Long::valueOf)
                .collect(LongHolders.min())
                .map(n -> n / 2)
                .fold("",n -> STR."Minimum of even numbers (2) / 2 = \{n}");

        assertEquals("Minimum of even numbers (2) / 2 = 1",result);
        logger.info(result);
    }

    @Test
    public void testReduceSumming_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,10,1,2,3,4);
        String result = numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .reduce(Holder.of(0L),LongHolders::sum,LongHolders::sum)
            .map(n -> n / 2)
            .fold("",n -> STR."Mean of even numbers (2,4,6,8,10) / 2 = \{n}");

        assertEquals("Mean of even numbers (2,4,6,8,10) / 2 = 15",result);
        logger.info(result);
    }

    @Test
    public void testReduceMax_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,11,10,1,2,3,4);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .reduce(Holder.of(0L),LongHolders::max,LongHolders::max)
                .map(n -> n / 2)
                .fold("",n -> STR."Maximum of even numbers (10) / 2 = \{n}");

        assertEquals("Maximum of even numbers (10) / 2 = 5",result);
        logger.info(result);
    }

    @Test
    public void testReduceMin_Pass() {
        List<Integer> numbers = Arrays.asList(5,6,7,8,9,11,10,1,2,3,4);
        String result = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .reduce(Holder.of(Long.MAX_VALUE),LongHolders::min,LongHolders::min)
                .map(n -> n / 2)
                .fold("",n -> STR."Minimum of even numbers (2) / 2 = \{n}");

        assertEquals("Minimum of even numbers (2) / 2 = 1",result);
        logger.info(result);
    }

}
