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

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractMatcher extends AbstractTupleContainer implements Matcher {

    public enum MatcherProperties {MATCH_ALL, MATCH_ANY}

    private final Pattern[] matchPatterns;
    private final Set<MatcherProperties> properties;

    /**
     * Constructs this implementation of {@link Matcher} object.
     *
     * @param elements
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
    }

    @Override
    public <T extends Tuple> boolean match(T tuple) {
        Objects.requireNonNull(tuple);
        boolean result = this.depth() <= tuple.depth(); // Pattern tuple scope adequate? If not, return false.
        if ( result ) {
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while ( result && it.hasNext() && i < this.depth() ) {
                Object o = it.next();
                if ( !(o instanceof String) ) {
                    // Comparison of elements in matcher pattern and tuple should be of the same type,
                    // if not false is returned
                    Object matcherElement = this.get(i);
                    if ( matcherElement == null && o == null ) {
                        result = true;
                    } else {
                        result = matcherElement != null && matcherElement.equals(o);
                    }
                } else {
                    String element = (String) o;
                    Pattern matcherPattern = matchPatterns[i];
                    result = matcherPattern != null && matcherPattern.matcher(element).matches();
                }
                if ( result && properties.contains(MatcherProperties.MATCH_ANY) )
                    break;
                i++;
            }
        }
        return result;
    }

    public Set<MatcherProperties> getProperties() {
        return properties;
    }
}
