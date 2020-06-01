package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Nullable;

import java.util.regex.Pattern;

/**
 * Tuples that implement this interface are considered as matchable with
 * other tuples.
 * <p>
 * They have patterns which are associated with each element of the tuple,
 * and are referenced at a logical position. The patterns, if applicable,
 * are applied to determine an element match. However, depending on
 * {@link MatcherStrategy} implementation, this alone will not necessarily
 * mean that the {@link Matchable} tuple matches.
 *
 * For more information,
 * @see MatcherStrategy interface.
 */
public interface Matchable extends TupleBase {

    /**
     * Returns {@link Pattern} object associated with this {@link Matchable}
     * elements.
     * <p>
     * Not all elements have an associated pattern and for these {@code null}
     * is implied.
     * @param position logical position of element, non-zero positive value.
     * @return pattern associated with element at {@code position}
     * @throws IllegalArgumentException if {@code position} value is invalid.
     */
    Nullable<Pattern> getPattern(int position);

}
