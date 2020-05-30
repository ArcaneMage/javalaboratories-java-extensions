package org.javalaboratories.core.tuple;

import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

@FunctionalInterface
public interface MatcherStrategy<Q extends Matcher, R extends TupleContainer> {

    boolean match(Q matcher, R tuple);

    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> all(BiPredicate<Object,Object> objectMatch,
                                                                                 BiPredicate<Pattern,String> patternMatch) {
        return (matcher, tuple) -> {
            boolean result = true;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( result && it.hasNext() && i < matcher.depth() ) {
                Object o = it.next();
                result = MatcherHelper.match(matcher,o,i + 1,objectMatch,patternMatch);
                i++;
            }
            return result;
        };
    }

    static <Q extends Matcher,R extends TupleContainer> MatcherStrategy<Q,R> any(BiPredicate<Object,Object> objectMatch,
                                                                                 BiPredicate<Pattern,String> patternMatch) {
        return (matcher, tuple) -> {
            boolean result = false;
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( !result && it.hasNext() && i < matcher.depth() ) {
                Object o = it.next();
                result = MatcherHelper.match(matcher,o,i + 1,objectMatch,patternMatch);
                i++;
            }
            return result;
        };
    }
}
