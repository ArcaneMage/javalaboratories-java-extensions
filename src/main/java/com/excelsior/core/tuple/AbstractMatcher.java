package com.excelsior.core.tuple;

import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class AbstractMatcher extends AbstractTupleContainer implements Matcher {

    private Pattern[] patterns;

    AbstractMatcher(Object... elements) {
        super(elements);
        patterns = new java.util.regex.Pattern[depth()];
        int i = 0;
        for ( Object o : this )
            patterns[i++] = o instanceof String ? java.util.regex.Pattern.compile(o.toString()) : null;
    }

    @Override
    public <T extends Tuple> boolean match(T tuple) {
        Objects.requireNonNull(tuple);
        boolean result = this.depth() <= tuple.depth(); // Pattern tuple scope adequate? If not, return false.
        if ( result ) {
            int i = 0;
            Iterator<Object> it = tuple.iterator();
            while (result && it.hasNext() && i < this.depth()) {
                Object o = it.next();
                if (!(o instanceof String)) {
                    // Comparison of elements in tuple pattern and tuple should be of the same type,
                    // if not false is returned
                    Object matcherElement = this.get(i);
                    if ( matcherElement == null && o == null ) {
                        result = true;
                    } else {
                        result = matcherElement != null && matcherElement.equals(o);
                    }
                } else {
                    String element = (String) o;
                    Pattern matcherPattern = patterns[i];
                    result = matcherPattern != null && matcherPattern.matcher(element).matches();
                }
                i++;
            }
        }
        return result;
    }
}
