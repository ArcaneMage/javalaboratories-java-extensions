package org.javalaboratories.core;

import org.javalaboratories.core.util.Holders.Holder;

import java.util.Comparator;

public final class Comparators {
    public static <T extends Comparable<T>> Comparator<Eval<T>> evalComparator() {
        return Comparator.comparing(Eval::get);
    }

    public static <T extends Comparable<T>> Comparator<Holder<T>> holderComparator() {
        return Comparator.comparing(Holder::get);
    }

    public static <T extends Comparable<T>> Comparator<Maybe<T>> maybeComparator() {
        return Comparator.comparing(Maybe::get);
    }

    private Comparators() {}
}
