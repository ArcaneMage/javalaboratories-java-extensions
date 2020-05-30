package org.javalaboratories.core.tuple;
/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import org.javalaboratories.core.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ALL;
import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ANY;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractMatcher extends AbstractTupleContainer implements Matcher {

    public enum MatcherProperties {MATCH_ALL, MATCH_ANY}

    private final Pattern[] matchPatterns;
    private final Set<MatcherProperties> properties;
    private final Map<MatcherProperties,MatcherStrategy<Matcher,Tuple>> strategies = new HashMap<>();

    /**
     * Constructs this implementation of {@link Matcher} object.
     *
     * @param elements matcher elements
     */
    AbstractMatcher(Object... elements) {
        this(EnumSet.of(MatcherProperties.MATCH_ALL),elements);
    }

    /**
     * Constructs this implementation of {@link Matcher} object.
     * <p>
     * Attempts to match elements with a tuple. {@code matchAny} parameter
     * tells the matcher to abort when it encounters the first element that
     * matches.
     *
     * @param elements matcher elements
     */
    AbstractMatcher(Set<MatcherProperties> properties, Object... elements) {
        super(elements);
        Objects.requireNonNull(properties);
        matchPatterns = new Pattern[depth()];
        int i = 0;
        for ( Object o : this )
            matchPatterns[i++] = o instanceof String ? Pattern.compile(o.toString()) : null;
        this.properties = properties;
        strategies.put(MATCH_ALL,MatcherStrategy.all(getObjectMatch(),getPatternMatch()));
        strategies.put(MATCH_ANY,MatcherStrategy.any(getObjectMatch(),getPatternMatch()));
    }

    @Override
    public <T extends Tuple> boolean match(T tuple) {
        Objects.requireNonNull(tuple);
        MatcherStrategy<Matcher,Tuple> strategy = strategies.get(this.properties.contains(MATCH_ALL) ? MATCH_ALL : MATCH_ANY);

        return strategy.match(this,tuple);
    }

    @Override
    public Nullable<Pattern> getPattern(int position) {
        verify(position);
        return Nullable.ofNullable(matchPatterns[position]);
    }

    public Set<MatcherProperties> getProperties() {
        return properties;
    }

    private BiPredicate<Object,Object> getObjectMatch() {
        return (matcherElement,element) -> {
            boolean result;
            if ( matcherElement == null && element == null ) {
                result = true;
            } else {
                result = matcherElement != null && matcherElement.equals(element);
            }
            return result;
        };
    }

    private BiPredicate<Pattern,String> getPatternMatch() {
        return (pattern,element) -> pattern != null && pattern.matcher(element).matches();
    }

}
