package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Tuples {

    public static <T extends Tuple,E> Nullable<T> fromIterable(Iterable<E> iterable, int depth) {
        return fromIterable(iterable, 0, depth);
    }

    @SuppressWarnings("unchecked")
    static <T extends Tuple,E> Nullable<T> fromIterable(Iterable<E> iterable, int start, int depth) {
        Iterator<E> iter = iterable.iterator();
        T result = null;
        try {
            int i = 0;
            while ( iter.hasNext() && i++ < start )
                iter.next();

            switch (depth) {
                case 1 : result = (T) Tuple.of(iter.next()); break;
                case 2 : result = (T) Tuple.of(iter.next(),iter.next()); break;
                case 3 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next()); break;
                case 4 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 5 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 6 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 7 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 8 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 9 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 10 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 11 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 12 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 13 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 14 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                case 15 : result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next()); break;
                default:
                    result = (T) Tuple.of(iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next(),iter.next());
            }
        } catch (NoSuchElementException e) {
            // Handled
        }
        return Nullable.ofNullable(result);
    }

    private Tuples() {}
}
