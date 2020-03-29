package com.excelsior.util;

import java.util.stream.Collector;

public interface Reducer<T,A,R> extends Collector<T,A,R> {
}
