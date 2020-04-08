package com.excelsior.util.statistics;

public class DoubleStatisticalCalculators extends ComprehensiveStatisticalCalculators<Double> {

    public DoubleStatisticalCalculators() {
        super(new DoubleSummaryStatisticsAdapter());
    }
}
