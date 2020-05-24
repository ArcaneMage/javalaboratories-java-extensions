package org.javalaboratories.core.statistics;

public class IntStatisticalCalculators extends ComprehensiveStatisticalCalculators<Integer> {
    public IntStatisticalCalculators() {
        super(new IntSummaryStatisticsAdapter());
    }
}
