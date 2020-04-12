package com.excelsior.core.statistics;

public class IntStatisticalCalculators extends ComprehensiveStatisticalCalculators<Integer> {
    public IntStatisticalCalculators() {
        super(new IntSummaryStatisticsAdapter());
    }
}
