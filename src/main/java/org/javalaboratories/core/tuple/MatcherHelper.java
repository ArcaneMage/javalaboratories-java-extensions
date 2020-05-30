package org.javalaboratories.core.tuple;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * Helper class for the {@link MatcherStrategy} interface.
 * <p>
 * This class has package-private accessibility to prevent other client classes
 * using it. Its purpose is to provide a service to the {@link MatcherStrategy}
 * interface.
 */
final class MatcherHelper {

    /**
     * The is a helper method of the {@link MatcherStrategy} interface.
     * <p>
     * Its purpose to determine a match of a specific element in the
     * {@link Matcher} at logical {@code position} with the {@code element}.
     * The match or equality is determined from {@code objectMatch} or
     * {@code patternMatch} predicate functions. In the case of pattern matching,
     * the pattern is obtained from the {@link Matcher} object for the current
     * {@code position} and passed to the {@code patternMatch} function. Note that
     * {@link Pattern} object is already in a compiled state, performed by the
     * {@link Matcher} object.
     * <p>
     * For {@link String} elements, the {@code patternMatch} is executed; and for
     * anything else, the {@code objectMatch} function is executed.
     * @param matcher matcher object
     * @param element current element of {@code position}, from the tuple, to match
     * @param position current logical position of both {@link Matcher} and
     * {@link Tuple}
     * @param objectMatch match function for non-string elements
     * @param patternMatch match function for string elements.
     * @param <T> type of {@link Matcher}
     * @return {@code True} if element matches current {@link Matcher} element.
     */
    static <T extends Matcher> boolean match(T matcher, Object element, int position, BiPredicate<Object,Object> objectMatch,
                                             BiPredicate<Pattern,String> patternMatch) {
        Objects.requireNonNull(matcher);
        Objects.requireNonNull(objectMatch);
        Objects.requireNonNull(patternMatch);

        boolean result;
        if ( !(element instanceof String) ) {
            // Comparison of elements in matcher pattern and tuple should be of the same type,
            // if not false is returned
            Object matcherElement = matcher.value(position);
            result = objectMatch.test(matcherElement,element);
        } else {
            String s = (String) element;
            Pattern matchPattern = matcher.getPattern(position).orElse(null);
            result = patternMatch.test(matchPattern,s);
        }
        return result;
    }

    private MatcherHelper() {}
}
