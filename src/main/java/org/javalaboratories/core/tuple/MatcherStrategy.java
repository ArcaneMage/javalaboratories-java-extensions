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
