package com.excelsior.util.statistics;

import java.util.DoubleSummaryStatistics;

public class DoubleSummaryStatisticsAdapter implements SummaryStatisticsAdapter<Double> {

    private DoubleSummaryStatistics adaptee = new DoubleSummaryStatistics();

    @Override
    public void combine(SummaryStatisticsAdapter<Double> o) {
        DoubleSummaryStatistics other = ((DoubleSummaryStatisticsAdapter) o).adaptee;
        this.adaptee.combine(other);
    }

    @Override
    public long getCount() {
        return adaptee.getCount();
    }

    @Override
    public double getAverage() {
        return adaptee.getAverage();
    }

    @Override
    public Double getMax() {
        return adaptee.getMax();
    }

    @Override
    public Double getMin() {
        return adaptee.getMin();
    }

    @Override
    public Double getSum() {
        return adaptee.getSum();
    }

    @Override
    public void accept(Double value) {
        adaptee.accept(value);
    }
}
