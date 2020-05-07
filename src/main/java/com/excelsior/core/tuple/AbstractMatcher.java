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
            Iterator<Object> iter = tuple.iterator();
            while ( result && iter.hasNext() && i < this.depth()) {
                Object o = iter.next();
                if (!(o instanceof String)) {
                    // Comparison of elements in tuple pattern and tuple should be of the same type,
                    // if not false is returned
                    result = this.get(i).equals(o);
                } else {
                    String element = (String) o;
                    Pattern p = patterns[i];
                    result = p != null && p.matcher(element).matches();
                }
                i++;
            }
        }
        return result;
    }
}
