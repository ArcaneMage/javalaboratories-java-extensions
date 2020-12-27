package org.javalaboratories.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
public class LongStatisticalCalculatorsTest {

    private LongStatisticalCalculators calculators;

    private List<Long> terms = Arrays.asList(17L,19L,21L,13L,16L,18L,24L,22L,21L, 21L);

    @BeforeEach
    public void setup() {
        calculators = new LongStatisticalCalculators();
        terms.forEach(calculators);
    }

    @Test
    public void testGetCount_Pass() {
        assertEquals(10,calculators.getCount());
    }

    @Test
    public void testGetMin_Pass() {
        assertEquals(13L,calculators.getMin());
    }

    @Test
    public void testGetMax_Pass() {
        assertEquals(24L,calculators.getMax());
    }

    @Test
    public void testGetSum_Pass() {
        assertEquals(192L,calculators.getSum());
    }

    @Test
    public void testGetAverage_Pass() {
        assertEquals(19.2, calculators.getAverage());
    }

    @Test
    public void testGetMode_Pass() {
        calculators.getMode().ifPresent(m -> assertEquals(21L, m));
    }

    @Test
    public void testGetMedian_Pass() {
        assertEquals(20L, calculators.getMedian());
    }

    @Test
    public void testGetStandardDeviation_Pass() {
        assertEquals(3.0919249667480617, calculators.getStandardDeviation());
    }

    @Test
    public void testGetVariance_Pass() {
        assertEquals(9.560000000000002, calculators.getVariance());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("LongStatisticalCalculators{count=10, sum=192, min=13, average=19.2, max=24, mode=Maybe[21.0], " +
                "median=20.000000, variance=9.560000, standard-deviation=3.091925}", calculators.toString());
    }

}
