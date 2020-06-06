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

@SuppressWarnings("WeakerAccess")
public final class DefaultTupleElementMatcher implements TupleElementMatcher {

    private static final int DERIVE_POSITION = -1;

    private final MatchableTuple matchable;

    public DefaultTupleElementMatcher(final MatchableTuple matchable) {
        this.matchable = matchable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(TupleElement element) {
        return matchObjectOrPattern(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> boolean match(T tuple) {
        boolean result = true;
        for ( int j = 0; j < matchable.depth() && result; j++ ) {
            boolean exists = false;
            Iterator<TupleElement> it = tuple.iterator();
            while ( it.hasNext() && !exists) {
                TupleElement element = it.next();
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
    public MatchableTuple getMatchable() {
        return matchable;
    }

    private boolean matchObjectOrPattern (TupleElement element) {
        return matchObjectOrPattern(element, DERIVE_POSITION);
    }

    private boolean matchObjectOrPattern (TupleElement element, int position) {
        int p = position == DERIVE_POSITION ? element.position() : position;
        Object matcherValue = matchable.value(p);
        Pattern matcherPattern;
        if (matchable instanceof MatchablePatternAware)
            matcherPattern = ((MatchablePatternAware) matchable).getPattern(p).orElse(null);
        else
            throw new IllegalArgumentException("Expected MatchablePatternAware object");

        boolean result;
        if ( !(element.isString()) ) {
            // Comparison of elements in matcher pattern and tuple should be of the same type,
            // if not false is returned
            result = matchObject(matcherValue, element.value());
        } else {
            result = matchPattern(matcherPattern, element.value());
        }

        return result;
    }

    private boolean matchObject(Object matcherValue, Object elementValue) {
        return matcherValue == null && elementValue == null ||
                matcherValue != null && matcherValue.equals(elementValue);
    }

    private boolean matchPattern(Pattern pattern, String elementValue) {
        return pattern != null && pattern.matcher(elementValue).matches();
    }
}
