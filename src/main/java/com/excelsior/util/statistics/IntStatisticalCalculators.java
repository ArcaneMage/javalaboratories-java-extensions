package com.excelsior.util.statistics;

public class IntStatisticalCalculators extends ComprehensiveStatisticalCalculators<Integer> {
    public IntStatisticalCalculators() {
        super(new IntSummaryStatisticsAdapter());
    }
}
