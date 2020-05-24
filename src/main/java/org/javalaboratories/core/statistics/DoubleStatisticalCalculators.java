package org.javalaboratories.core.statistics;

public class DoubleStatisticalCalculators extends ComprehensiveStatisticalCalculators<Double> {

    public DoubleStatisticalCalculators() {
        super(new DoubleSummaryStatisticsAdapter());
    }
}
