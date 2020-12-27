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
package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Maybe;

import java.util.*;
import java.util.regex.Pattern;

import static org.javalaboratories.core.tuple.Matcher.Strategy.*;

public abstract class AbstractMatcher extends AbstractTupleContainer implements Matcher {

    private final Pattern[] matchPatterns;
    private final Strategy strategy;
    private final Map<Strategy,MatcherStrategy<Tuple>> strategies = new HashMap<>();

    /**
     * Constructs this implementation of {@link Matcher} object.
     * <p>
     * Attempts to match elements with a tuple. {@code matchAny} parameter
     * tells the matcher to abort when it encounters the first element that
     * matches.
     *
     * @param elements matcher elements
     */
    AbstractMatcher(Strategy strategy, Object... elements) {
        super(elements);
        Objects.requireNonNull(strategy);
        matchPatterns = new Pattern[depth()];
        int i = 0;
        for (TupleElement element : this)
            matchPatterns[i++] = element.isString() ? Pattern.compile(element.value().toString()) : null;
        this.strategy = strategy;
        strategies.put(MATCH_ALL,MatcherStrategy.allOf());
        strategies.put(MATCH_ANY,MatcherStrategy.anyOf());
        strategies.put(MATCH_SET,MatcherStrategy.setOf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> boolean match(T tuple) {
        Objects.requireNonNull(tuple);
        MatcherStrategy<Tuple> strategy = strategies.get(this.strategy);

        return strategy.match(() -> new DefaultTupleElementMatcher(this),tuple);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Maybe<Pattern> getPattern(int position) {
        verify(position);
        return Maybe.ofNullable(matchPatterns[position -1]);
    }

    /**
     * {@inheritDoc}
     */
    public Strategy getStrategy() {
        return strategy;
    }

}
