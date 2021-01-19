package org.javalaboratories.core.statistics;

import org.javalaboratories.core.Maybe;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ModeCalculator<T extends Number> implements StatisticalCalculator<T, Maybe<Double>>{
    private Map<T,Long> modeMap = new HashMap<>();

    public void accept(T value) {
        Long count = modeMap.get(value);
        if (count == null)
            count = 0L;
        modeMap.put(value,++count);
    }

    public Maybe<Double> getResult() {
        if ( modeMap.size() == 0)
            throw new InsufficientPopulationException("Could not calculate mode");
        List<T> result = modeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return isModeCalculable(result) ? Maybe.of(result.get(0).doubleValue()) : Maybe.empty();
    }

    public List<T> getData() {
        List<T> result = new ArrayList<>();
        // Regenerate data to original form
        modeMap.forEach((k,v) ->
                LongStream.range(0,v)
                    .forEach(c -> result.add(k)));

        return Collections.unmodifiableList(result);
    }

    private boolean isModeCalculable(List<T> sortedKeys) {
        if ( modeMap.size() > 1)
            return !modeMap.get(sortedKeys.get(0)).equals(modeMap.get(sortedKeys.get(sortedKeys.size() - 1)));
        else
            return modeMap.size() == 1;
    }
}
