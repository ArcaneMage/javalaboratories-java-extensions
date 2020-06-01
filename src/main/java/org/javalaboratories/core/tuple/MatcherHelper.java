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
     * Its purpose to determine a match of a specific element.
     * The match or equality is determined from {@code objectMatching} or
     * {@code patternMatching} predicate functions. In the case of pattern matching,
     * the {@link Pattern} object is passed to the {@code patternMatching} function.
     * Note that {@link Pattern} object is already in a compiled state,
     * performed by the {@link Matcher} object.
     * <p>
     * For {@link String} elements, the {@code patternMatching} is executed; and for
     * anything else, the {@code objectMatching} function is executed.
     * @param element current element of {@code position}, from the tuple, to match
     * @param matcherElement current matcher element
     * @param matcherPattern current matcher pattern {@link Tuple}
     * @param objectMatching match function for non-string elements
     * @param patternMatching match function for string elements.
     * @return {@code True} if element matches current {@link Matcher} element.
     */
    static boolean matchElement(Object element, Object matcherElement, Pattern matcherPattern,
                                                    BiPredicate<Object,Object> objectMatching,
                                                    BiPredicate<Pattern,String> patternMatching) {
        Objects.requireNonNull(patternMatching);

        boolean result;
        if ( !(element instanceof String) ) {
            // Comparison of elements in matcher pattern and tuple should be of the same type,
            // if not false is returned
            result = objectMatching.test(matcherElement,element);
        } else {
            String s = (String) element;
            result = patternMatching.test(matcherPattern,s);
        }
        return result;
    }

    private MatcherHelper() {}
}
