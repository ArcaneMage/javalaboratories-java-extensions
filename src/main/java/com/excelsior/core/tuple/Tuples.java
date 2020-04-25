package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Tuples {

    public static <T,U extends Tuple> Nullable<U> fromIterable(Iterable<T> iterable, int depth) {
        return fromIterable(iterable, 0, depth);
    }

    @SuppressWarnings("unchecked")
    static <T,U extends Tuple> Nullable<U> fromIterable(Iterable<T> iterable, int start, int depth) {
        Iterator<T> iter = iterable.iterator();
        int position = 0;
        U result = null;
        try {
            while ( iter.hasNext() && position++ < start )
                iter.next();

            switch (depth) {
                case 1 : result = (U) Tuple.of(iter.next()); break;
                case 2 : result = (U) Tuple.of(iter.next(),iter.next()); break;
                case 3 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next()); break;
                case 4 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 5 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 6 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 7 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 8 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 9 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 10 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 11 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 12 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 13 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 14 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 15 : result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                default:
                    result = (U) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next());
            }
        } catch (NoSuchElementException e) {
            // Handled
        }
        return Nullable.ofNullable(result);
    }

    private Tuples() {}
}
