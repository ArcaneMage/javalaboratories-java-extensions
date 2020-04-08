package com.excelsior.util.statistics;

public class LongStatisticalCalculators extends ComprehensiveStatisticalCalculators<Long> {

    public LongStatisticalCalculators() {
        super(new LongSummaryStatisticsAdapter());
    }
}
