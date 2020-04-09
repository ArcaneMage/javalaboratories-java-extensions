package com.excelsior.util.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
public class IntStatisticalCalculatorsTest {
    private IntStatisticalCalculators calculators;

    private List<Integer> terms = Arrays.asList(17,19,21,13,16,18,24,22,21, 21);

    @BeforeEach
    public void setup() {
        calculators = new IntStatisticalCalculators();
        terms.forEach(calculators);
    }

    @Test
    public void testGetCount_Pass() {
        assertEquals(10L,calculators.getCount());
    }

    @Test
    public void testGetMin_Pass() {
        assertEquals(13,calculators.getMin());
    }

    @Test
    public void testGetMax_Pass() {
        assertEquals(24,calculators.getMax());
    }

    @Test
    public void testGetSum_Pass() {
        assertEquals(192,calculators.getSum());
    }

    @Test
    public void testGetAverage_Pass() {
        assertEquals(19.2, calculators.getAverage());
    }

    @Test
    public void testGetMode_Pass() {
        calculators.getMode().ifPresent(m -> assertEquals(21, m));
    }

    @Test
    public void testGetMedian_Pass() {
        assertEquals(20, calculators.getMedian());
    }

    @Test
    public void testGetStandardDeviation_Pass() {
        assertEquals(3.0919249667480617, calculators.getStandardDeviation());
    }

    @Test
    public void testGetVariance_Pass() {
        assertEquals(9.560000000000002, calculators.getVariance());
    }

}
