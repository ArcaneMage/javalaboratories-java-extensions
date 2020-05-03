package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TupleTest {

    private Logger logger = LoggerFactory.getLogger(TupleTest.class);

    @Test
    public void testExamples_Pass() {

        // Creating tuple
        Tuple2<String,Integer> person = Tuple.of("James",12);


        // Retrieve Values
        logger.info("Name: {}",person.value1());
        logger.info("Grade: {}",person.value2());


        // Setting Values
        Tuple2<String,Integer> modified = person.mapAt2(s -> 16);
        logger.info("Name: {}",modified.value1());
        logger.info("Grade: {}",modified.value2());

        // From collection
        List<String> people = Arrays.asList("James","Carl","Andrea","Sharon");

        Nullable<Tuple4<String,String,String,String>> maybeTuple = Tuple4.fromIterable(people);

        maybeTuple.ifPresent(tuple -> tuple.forEach(name -> logger.info("Name: {}",name)));

        // To collection
        Tuple4<String,Integer,String,Integer> data = Tuple.of("A1",1,"A2",2);
        List<?> list = data.toList();
        logger.info("Tuple to list -> {}",list);

        // Splicing
        Tuple4<String,String,String,String> party = maybeTuple.get();
        Tuple2<Tuple2<String,String>,Tuple2<String,String>> teams = party.splice(3);

        teams.value1().forEach(member -> logger.info("Boy: {}",member));
        teams.value2().forEach(member -> logger.info("Girl: {}",member));
    }
}
