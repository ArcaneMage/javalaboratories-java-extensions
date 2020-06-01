package org.javalaboratories.core.tuple;

/**
 * Matches an element of a tuple or elements within a tuple against a
 * {@link Matchable} object.
 * <p>
 * This object will attempt to match each element in the {@link Matchable},
 * applying {@link java.util.regex.Pattern}, if applicable -- only {@link String}
 * object types invoke pattern matching, and only if a pattern is available.
 * All other types invoke the equal method.
 * <p>
 * {@link AbstractMatcher} creates a default implementation of this interface.
 * @see AbstractMatcher
 * @see Matchable
 */
public interface TupleElementMatcher {
    /**
     * Match {@code element} at logical {@code position} with {@link Matchable}
     * element.
     * <p><
     * @param element element to match, ultimately came from {@link Tuple}
     * @param position logical position of element undergoing matching. This is a
     *                 none-zero, positive value.
     * @return {@code True} for a match; {@code False} for a non-match.
     */
    boolean match(Object element, int position);

    /**
     * Each element in the {@link Matchable} is tested against all the
     * {@link Tuple} elements.
     * <p>
     * @param tuple the {@link Tuple} to match.
     * @param <T> type of {@link Tuple}
     * @return {@code True} for a match; {@code False} for a non-match.
     */
    <T extends Tuple> boolean match(T tuple);

    /**
     * Returns underlying {@link Matchable} object whose elements and patterns
     * are used in the matching process.
     * @return an implementation of {@link Matchable}
     */
    Matchable getMatchable();
}
