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

import java.util.Iterator;
import java.util.regex.Pattern;

public final class DefaultTupleElementMatcher implements TupleElementMatcher {

    private final Matchable matchable;

    public DefaultTupleElementMatcher(final Matchable matchable) {
        this.matchable = matchable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Object element, int position) {
        return matchObjectOrPattern(element,position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> boolean match(T tuple) {
        boolean result = true;
        for ( int j = 0; j < matchable.depth() && result; j++ ) {
            boolean exists = false;
            Iterator<Object> it = tuple.iterator();
            while ( it.hasNext() && !exists) {
                Object element = it.next();
                exists = matchObjectOrPattern(element,j+1);
            }
            result = exists;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Matchable getMatchable() {
        return matchable;
    }

    private boolean matchObjectOrPattern (Object element, int position) {
        Object matcherElement = matchable.value(position);
        Pattern matcherPattern = matchable.getPattern(position).orElse(null);

        boolean result;
        if ( !(element instanceof String) ) {
            // Comparison of elements in matcher pattern and tuple should be of the same type,
            // if not false is returned
            result = matchObject(matcherElement, element);
        } else {
            String s = (String) element;
            result = matchPattern(matcherPattern, s);
        }

        return result;
    }

    private boolean matchObject(Object matcherElement, Object element) {
        return matcherElement == null && element == null ||
                matcherElement != null && matcherElement.equals(element);
    }

    private boolean matchPattern(Pattern pattern, String element) {
        return pattern != null && pattern.matcher(element).matches();
    }
}
