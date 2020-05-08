package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Tuples {

    @SuppressWarnings("unchecked")
    static <T> T emptyTuple() { return (T) Tuple.of(); }

    public static <T extends Tuple,E> Nullable<T> fromIterable(Iterable<E> iterable, int depth) {
        return fromIterable(iterable, 0, depth);
    }

    @SuppressWarnings("unchecked")
    static <T extends Tuple,E> Nullable<T> fromIterable(Iterable<E> iterable, int start, int depth) {
        Iterator<E> it = iterable.iterator();
        T result = null;
        try {
            int i = 1;
            while ( it.hasNext() && i++ < start )
                it.next();

            switch (depth) {
                case 0 : result = emptyTuple(); break;
                case 1 : result = (T) Tuple.of(it.next()); break;
                case 2 : result = (T) Tuple.of(it.next(),it.next()); break;
                case 3 : result = (T) Tuple.of(it.next(),it.next(),it.next()); break;
                case 4 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next()); break;
                case 5 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 6 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 7 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 8 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 9 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 10 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 11 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 12 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 13 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 14 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 15 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
                case 16 : result = (T) Tuple.of(it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next(),it.next()); break;
            }
        } catch (NoSuchElementException e) {
            // Handled
        }
        return Nullable.ofNullable(result);
    }

    private Tuples() {}
}
