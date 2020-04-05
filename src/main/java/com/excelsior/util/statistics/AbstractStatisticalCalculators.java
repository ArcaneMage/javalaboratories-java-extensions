package com.excelsior.util.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public class AbstractStatisticalCalculators<T extends Number> implements Consumer<T> {

    private List<StatisticalCalculator> calculators;

    public AbstractStatisticalCalculators() {
        calculators = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void accept(T t) {
        calculators.forEach(c -> c.add(t));
    }

    @SuppressWarnings("unchecked")
    public void combine(AbstractStatisticalCalculators<T> other) {
        // Source data from the first calculator for now
        // TODO: Need to refactor calculator container
        if ( other.calculators.size() > 0 )
            other.calculators.get(0).getData()
                    .forEach(v -> calculators
                            .forEach (c -> c.add(v)));
    }

    protected final void add(StatisticalCalculator<T,?> calculator) {
        Objects.requireNonNull(calculator);
        calculators.add(calculator);
    }

    public long getCount() {
        return 0L;
    }

    public T getSum() {
       return null;
    }

    public T getMin() {
        return null;
    }

    public T getMax() {
        return null;
    }

    public double getAverage() {
        return 0.0;
    }
}
