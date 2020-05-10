package com.excelsior.core.tuple;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.excelsior.core.tuple.Matcher.when;
import static com.excelsior.core.tuple.Tuple.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TupleTest {

    private Logger logger = LoggerFactory.getLogger(TupleTest.class);

    @Test
    public void testExamples_Pass() {

        // tupleEarth: ("Earth",7926,92955807), diameter in miles, distance from Sun in miles
        Tuple3<String,Integer,Integer> tupleEarth = of("Earth",7926,92955807);

        // tupleEarth.value2(): 7926
        tupleEarth.value2();

        // tupleEarth: ("Earth",12756,92955807), diameter in km
        Tuple3<String,Integer,Integer> kmEarth = tupleEarth.mapAt2(t -> Math.round((t / (float) 0.621371)));

        // earthMoon: ("Earth",7926,92955807,"Moon",2159), joined moon, diameter of 2159
        Tuple5<String,Integer,Integer,String,Integer> tupleEarthMoon = tupleEarth.join(of("Moon",2159));

        // planetaryBodies: (("Earth",7926,92955807),("Moon",2159))
        Tuple2<Tuple3<String,Integer,Integer>,Tuple2<String,Integer>> tuplePlanetaryBodies = tupleEarthMoon.spliceAt4();

        // tupleEarth: ("Earth",7926,92955807)
        tupleEarth = tuplePlanetaryBodies.value1();

        // tupleMoon: ("Moon",2159,92900000), added moon distance from Sun
        Tuple3<String,Integer,Integer> tupleMoon = tuplePlanetaryBodies.value2().join(92900000);

        // tupleCoordinates: ("Milky Way","Earth","Europe","England","Blackfriars","London","EC2 1QW")
        Tuple7<String,String,String,String,String,String,String> tupleCoordinates = tupleEarth
                .truncateAt2()
                .addAt1("Milky Way")
                .join(of("Europe","England","Blackfriars","London","EC2 1QW"));

        // list: ["Milky Way","Earth","Europe","England","Blackfriars","London","EC2 1QW"]
        List<?> list = tupleCoordinates.toList();

        // Outputs: "Earth's distance from Sun 92955807"
        tupleEarth.match(when("^Earth$"),(a,b,c) -> logger.info("Earth's distance from Sun {}",c));

        assertEquals(7,list.size());
        String galaxy = (String) list.get(0);
        assertEquals("Milky Way",galaxy);

    }
}
