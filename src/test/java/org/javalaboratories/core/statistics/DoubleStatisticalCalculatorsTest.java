package org.javalaboratories.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("WeakerAccess")
public class DoubleStatisticalCalculatorsTest {
    private DoubleStatisticalCalculators calculators;

    private List<Double> terms = Arrays.asList(17.0,19.0,21.0,13.0,16.0,18.0,24.0,22.0,21.0,21.0);

    @BeforeEach
    public void setup() {
        calculators = new DoubleStatisticalCalculators();
        terms.forEach(calculators);
    }

    @Test
    public void testGetCount_Pass() {
        assertEquals(10L,calculators.getCount());
    }

    @Test
    public void testGetMin_Pass() {
        assertEquals(13.0,calculators.getMin());
    }

    @Test
    public void testGetMax_Pass() {
        assertEquals(24.0,calculators.getMax());
    }

    @Test
    public void testGetSum_Pass() {
        assertEquals(192.0,calculators.getSum());
    }

    @Test
    public void testGetAverage_Pass() {
        assertEquals(19.2, calculators.getAverage());
    }

    @Test
    public void testGetMode_Pass() {
        calculators.getMode().ifPresent(m -> assertEquals(21.0, m));
    }

    @Test
    public void testGetMedian_Pass() {
        assertEquals(20.0, calculators.getMedian());
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
        assertEquals("DoubleStatisticalCalculators{count=10, sum=192.0, min=13.0, average=19.2, max=24.0, " +
                "mode=Nullable[21.0], median=20.000000, variance=9.560000, standard-deviation=3.091925}", calculators.toString());
    }

}
