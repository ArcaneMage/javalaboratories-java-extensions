package com.excelsior.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ReducersTest {

    @Test
    public void testJoining_Pass() {
        List<String> strings = Arrays.asList("Kevin","James","Alex");

        strings.stream()
                .collect(Reducers.joining())
               .ifPresent(s -> assertEquals("KevinJamesAlex",s));

        strings.parallelStream()
                .collect(Reducers.joining(","))
                .ifPresent(s -> assertEquals("Kevin,James,Alex",s));
    }

    @Test
    public void testCounting_Pass() {
        List<String> strings = Arrays.asList("Kevin","James","Alex","John");

        strings.stream()
                .collect(Reducers.counting())
                .ifPresent(s -> assertEquals((Long)4L,s));

        strings.parallelStream()
                .collect(Reducers.counting())
                .ifPresent(s -> assertEquals((Long)4L,s));
    }

    @Test
    public void testAveragingDouble_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.averagingDouble(Double::parseDouble))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        strings.parallelStream()
                .collect(Reducers.averagingDouble(Double::parseDouble))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.averagingDouble(Double::parseDouble))
                .ifPresent(r -> assertEquals((Double) Double.NaN, r));
    }

    @Test
    public void testAveragingLong_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.averagingLong(Long::parseLong))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        strings.parallelStream()
                .collect(Reducers.averagingLong(Long::parseLong))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.averagingLong(Long::parseLong))
                .ifPresent(r -> assertEquals((Double) Double.NaN, r));
    }

    @Test
    public void testAveragingInt_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.averagingInt(Integer::parseInt))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        strings.parallelStream()
                .collect(Reducers.averagingInt(Integer::parseInt))
                .ifPresent(r -> assertEquals((Double) 4.0, r));

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.averagingInt(Integer::parseInt))
                .ifPresent(r -> assertEquals((Double) Double.NaN, r));
    }

    @Test
    public void testSummarizingInt_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.summarizingInt(Integer::parseInt))
                .ifPresent(r -> {
                    assertEquals(3, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3 ,r.getMin());
                    assertEquals(5, r.getMax());
                    assertEquals(12, r.getSum());
                });

        strings.parallelStream()
                .collect(Reducers.summarizingInt(Integer::parseInt))
                .ifPresent(r -> {
                    assertEquals(3, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3 ,r.getMin());
                    assertEquals(5, r.getMax());
                    assertEquals(12, r.getSum());
                });

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.summarizingInt(Integer::parseInt))
                .ifPresent(r -> assertEquals(0, r.getCount()));
    }

    @Test
    public void testSummarizingLong_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.summarizingLong(Long::parseLong))
                .ifPresent(r -> {
                    assertEquals(3L, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3L ,r.getMin());
                    assertEquals(5L, r.getMax());
                    assertEquals(12L, r.getSum());
                });

        strings.parallelStream()
                .collect(Reducers.summarizingLong(Long::parseLong))
                .ifPresent(r -> {
                    assertEquals(3L, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3L ,r.getMin());
                    assertEquals(5L, r.getMax());
                    assertEquals(12L, r.getSum());
                });

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.summarizingLong(Long::parseLong))
                .ifPresent(r -> assertEquals(0L, r.getCount()));
    }

    @Test
    public void testSummarizingDouble_Pass() {
        List<String> strings = Arrays.asList("3","4","5");

        strings.stream()
                .collect(Reducers.summarizingDouble(Double::parseDouble))
                .ifPresent(r -> {
                    assertEquals(3L, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3.0 ,r.getMin());
                    assertEquals(5.0, r.getMax());
                    assertEquals(12.0, r.getSum());
                });

        strings.parallelStream()
                .collect(Reducers.summarizingDouble(Double::parseDouble))
                .ifPresent(r -> {
                    assertEquals(3L, r.getCount());
                    assertEquals(4.0 ,r.getAverage());
                    assertEquals(3.0 ,r.getMin());
                    assertEquals(5.0, r.getMax());
                    assertEquals(12.0, r.getSum());
                });

        List<String> zero = Collections.emptyList();
        zero.parallelStream()
                .collect(Reducers.summarizingDouble(Double::parseDouble))
                .ifPresent(r -> assertEquals(0L, r.getCount()));
    }

    @Test
    public void testPartitionBy_Pass() {
        List<String> strings = Arrays.asList("3","4","5","7","8");

        strings.stream()
                .collect(Reducers.partitioningBy(s -> Integer.parseInt(s) > 4))
                .ifPresent(r -> {
                    assertEquals(2,r.get(false).size());
                    assertEquals("[3, 4]",r.get(false).toString());
                    assertEquals(3,r.get(true).size());
                    assertEquals("[5, 7, 8]",r.get(true).toString());
                });

        strings.parallelStream()
                .collect(Reducers.partitioningBy(s -> Integer.parseInt(s) > 4))
                .ifPresent(r -> {
                    assertEquals(2,r.get(false).size());
                    assertEquals("[3, 4]",r.get(false).toString());
                    assertEquals(3,r.get(true).size());
                    assertEquals("[5, 7, 8]",r.get(true).toString());
                });

        strings.parallelStream()
                .collect(Reducers.partitioningBy(s -> Integer.parseInt(s) > 0))
                .ifPresent(r -> {
                    assertEquals(0,r.get(false).size());
                    assertEquals("[]",r.get(false).toString());
                    assertEquals(5,r.get(true).size());
                    assertEquals("[3, 4, 5, 7, 8]",r.get(true).toString());
                });

        List<String> zero = Collections.emptyList();
        zero.stream()
                .collect(Reducers.partitioningBy(s -> Integer.parseInt(s) > 4))
                .ifPresent(r -> assertEquals(2, r.size()));
    }

    @Test
    public void testGroupingBy_Pass() {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9);

        numbers.stream()
                .collect(Reducers.groupingBy(n -> n % 2 == 0 ? "Even" : "Odd"))
                .ifPresent(g -> {
                    assertEquals("[2, 4, 6, 8]",g.get("Even").toString());
                    assertEquals("[1, 3, 5, 7, 9]",g.get("Odd").toString());
                });

        numbers.parallelStream()
                .collect(Reducers.groupingBy(n -> n % 2 == 0 ? "Even" : "Odd"))
                .ifPresent(g -> {
                    assertEquals("[2, 4, 6, 8]",g.get("Even").toString());
                    assertEquals("[1, 3, 5, 7, 9]",g.get("Odd").toString());
                });

        List<Integer> zero = Collections.emptyList();
        zero.stream()
                .collect(Reducers.groupingBy(n -> n % 2 == 0 ? "Even" : "Odd"))
                .ifPresent(g -> assertEquals(0, g.size()));
    }

    @Test
    public void testMaxBy_Pass() {
        List<Integer> numbers = Arrays.asList(9,7,5,76,3,40,101);

        numbers.stream()
                .collect(Reducers.maxBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 101,n));

        numbers.parallelStream()
                .collect(Reducers.maxBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 101,n));

        numbers = Arrays.asList(0);
        numbers.stream()
                .collect(Reducers.maxBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 0,n));

        numbers = Arrays.asList();
        numbers.stream()
                .collect(Reducers.maxBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 0,n));

    }

    @Test
    public void testMinBy_Pass() {
        List<Integer> numbers = Arrays.asList(9,7,5,76,2,40,101);

        numbers.stream()
                .collect(Reducers.minBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 2,n));

        numbers.parallelStream()
                .collect(Reducers.minBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 2,n));

        numbers = Arrays.asList(0);
        numbers.stream()
                .collect(Reducers.minBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 0,n));

        numbers = Arrays.asList();
        numbers.stream()
                .collect(Reducers.minBy(Integer::compareTo))
                .ifPresent( n -> assertEquals((Integer) 0,n));
    }
}
