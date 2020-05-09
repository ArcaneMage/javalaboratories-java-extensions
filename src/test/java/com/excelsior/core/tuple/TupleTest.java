package com.excelsior.core.tuple;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.excelsior.core.tuple.Matcher.when;
import static com.excelsior.core.tuple.Tuple.of;

public class TupleTest {

    private Logger logger = LoggerFactory.getLogger(TupleTest.class);

    @Test
    public void testExamples_Pass() {

        // earth: ("Earth",7926,92955807), diameter in miles, distance from Sun in miles
        Tuple3<String,Integer,Integer> earth = of("Earth",7926,92955807);

        // earth.value2(): 7926
        earth.value2();

        // kmEarth: ("Earth",12756,92955807), diameter in km
        Tuple3<String,Integer,Integer> kmEarth = earth.mapAt2(t -> Math.round((t / (float) 0.621371)));

        // earthMoon: ("Earth",7926,92955807,"Moon",9128), joined moon, diameter of 2159
        Tuple5<String,Integer,Integer,String,Integer> earthMoon = earth.join(of("Moon",2159));

        // planetaryBodies: (("Earth",7926,92955807),("Moon",9128))
        Tuple2<Tuple3<String,Integer,Integer>,Tuple2<String,Integer>> planetaryBodies = earthMoon.spliceAt4();

        // earth: ("Earth",7926,92955807)
        earth = planetaryBodies.value1();

        // moon: ("Moon",9128,92900000), added moon distance from Sun
        Tuple3<String,Integer,Integer> moon = planetaryBodies.value2().join(92900000);

        // home: ("Earth")
        Tuple1<String> home = earth.truncateAt2();

        // Outputs: "Earth's distance from Sun 92955807"
        earth.match(when("^Earth$"),(a,b,c) -> logger.info("Earth's distance from Sun {}",c));
    }
}
