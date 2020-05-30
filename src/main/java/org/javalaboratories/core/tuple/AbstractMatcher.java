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
import java.util.regex.Pattern;

import static org.javalaboratories.core.tuple.Matcher.MatcherStrategies.MATCH_ALL;
import static org.javalaboratories.core.tuple.Matcher.MatcherStrategies.MATCH_ANY;

public abstract class AbstractMatcher extends AbstractTupleContainer implements Matcher {

    private final Pattern[] matchPatterns;
    private final MatcherStrategies strategy;
    private final Map<MatcherStrategies,MatcherStrategy<Matcher,Tuple>> strategies = new HashMap<>();

    /**
     * Constructs this implementation of {@link Matcher} object.
     * <p>
     * Attempts to match elements with a tuple. {@code matchAny} parameter
     * tells the matcher to abort when it encounters the first element that
     * matches.
     *
     * @param elements matcher elements
     */
    AbstractMatcher(MatcherStrategies strategy, Object... elements) {
        super(elements);
        Objects.requireNonNull(strategy);
        matchPatterns = new Pattern[depth()];
        int i = 0;
        for ( Object o : this )
            matchPatterns[i++] = o instanceof String ? Pattern.compile(o.toString()) : null;
        this.strategy = strategy;
        strategies.put(MATCH_ALL,MatcherStrategy.all());
        strategies.put(MATCH_ANY,MatcherStrategy.any());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> boolean match(T tuple) {
        Objects.requireNonNull(tuple);
        MatcherStrategy<Matcher,Tuple> strategy = strategies.get(this.strategy);

        return strategy.match(this,tuple);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Nullable<Pattern> getPattern(int position) {
        verify(position);
        return Nullable.ofNullable(matchPatterns[position -1]);
    }

    /**
     * {@inheritDoc}
     */
    public MatcherStrategies getStrategy() {
        return strategy;
    }

}
