package com.excelsior.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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


}
