package com.excelsior.util.statistics;

import com.excelsior.util.Holder;
import com.excelsior.util.Holders;
import com.excelsior.util.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ModeCalculator<T extends Number> implements StatisticalCalculator<T,Nullable<T>>{
    private Map<T,Long> modeMap = new HashMap<>();

    public void add(T value) {
        Long count = modeMap.get(value);
        if ( count == null )
            count = 0L;
        modeMap.put(value,++count);
    }

    public Nullable<T> getResult() {
        List<T> result = modeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return isModeCalculable(result) ? Nullable.of(result.get(0)) : Nullable.empty();
    }

    public List<T> getData() {
        Holder<List<T>>  result = Holders.writableHolder();
        result.set(new ArrayList<>());

        modeMap.forEach((k,v) -> regenerateData(result.get(),k,v));
        return Collections.unmodifiableList(result.get());
    }

    private void regenerateData(final List<T> data, final T key, final long count) {
        for ( long i = 0; i < count; i++ )
            data.add(key);
    }

    private boolean isModeCalculable(List<T> sortedKeys) {
        if ( modeMap.size() > 1 )
            return !modeMap.get(sortedKeys.get(0)).equals(modeMap.get(sortedKeys.get(sortedKeys.size() - 1)));
        else
            return modeMap.size() == 1;
    }
}
