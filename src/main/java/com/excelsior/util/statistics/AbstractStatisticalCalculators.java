package com.excelsior.util.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractStatisticalCalculators<T extends Number> implements Consumer<T> {

    private final List<StatisticalCalculator<T,?>> calculators;
    private final SummaryStatisticsAdapter<T> delegate;

    public AbstractStatisticalCalculators(final SummaryStatisticsAdapter<T> delegate) {
        Objects.requireNonNull(delegate,"Summary statistics object required");
        calculators = new ArrayList<>();
        this.delegate = delegate;
    }

    @Override
    public void accept(T t) {
        calculators.forEach(c -> c.add(t));
        delegate.accept(t);
    }

    public void combine(AbstractStatisticalCalculators<T> other) {
        // Source data from the first calculator for now
        if ( other.calculators.size() > 0 )
            other.calculators.get(0).getData()
                    .forEach(v -> calculators
                            .forEach (c -> c.add(v)));
        delegate.combine(other.delegate);
    }

    protected final void add(StatisticalCalculator<T,?> calculator) {
        Objects.requireNonNull(calculator);
        calculators.add(calculator);
    }

    public long getCount() {
        return delegate.getCount();
    }

    public T getSum() {
       return delegate.getSum();
    }

    public T getMin() {
        return delegate.getMin();
    }

    public T getMax() {
        return delegate.getMax();
    }

    public double getAverage() {
        return delegate.getAverage();
    }
}
