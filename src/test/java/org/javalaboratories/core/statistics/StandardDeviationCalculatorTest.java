package org.javalaboratories.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDeviationCalculatorTest {
    private StandardDeviationCalculator<Long> standardDeviationCalculator1;
    private StandardDeviationCalculator<Long> standardDeviationCalculator2;

    @BeforeEach
    public void setup() {
        List<Long> terms1 = Arrays.asList(17L,19L,21L,13L,16L,18L,24L,20L,20L);

        standardDeviationCalculator1 = new StandardDeviationCalculator<>();
        terms1.forEach(t -> standardDeviationCalculator1.accept(t));

        standardDeviationCalculator2 = new StandardDeviationCalculator<>();
    }

    @Test
    public void testAdd_Pass() {
        assertEquals(9, standardDeviationCalculator1.getData().size());
    }

    @Test
    public void testGetResult_Pass() {

        assertEquals(2.981423969999705,standardDeviationCalculator1.getResult());

    }

    @Test
    public void testGetResult_Fail() {
       assertThrows (InsufficientPopulationException.class, () -> standardDeviationCalculator2.getResult());
    }
}
