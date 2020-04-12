package com.excelsior.core.statistics;

public class LongStatisticalCalculators extends ComprehensiveStatisticalCalculators<Long> {

    public LongStatisticalCalculators() {
        super(new LongSummaryStatisticsAdapter());
    }
}
