package com.excelsior.core.statistics;

public class DoubleStatisticalCalculators extends ComprehensiveStatisticalCalculators<Double> {

    public DoubleStatisticalCalculators() {
        super(new DoubleSummaryStatisticsAdapter());
    }
}
