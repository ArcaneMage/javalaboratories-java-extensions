package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.holders.Holder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Note: Tuple2 tests will test all methods of the {@link AbstractTuple} class, which
 * will NOT be retested in other tuple unit tests, for example serialization,
 * {@link AbstractTuple#add} and others.
 */
@SuppressWarnings("WeakerAccess")
public class Tuple2Test {

    private Tuple2<String,String> tuple;
    private Tuple2<Integer,Integer> tuple_2;
    private Tuple2<String,String> tuple_3;

    Logger logger = LoggerFactory.getLogger(Tuple2Test.class);

    @BeforeEach
    public void setup() {
        tuple = Tuple.of("John","Doe");
        tuple_2 = Tuple.of(1,2);
        tuple_3 = Tuple.of(null,"Wall");
    }

    @Test
    public void testOf_Pass() {
        assertEquals("John", tuple.value1());
        assertEquals("Doe", tuple.value2());
    }

    @Test
    public void testAddFirst_Pass() {
        Tuple2 tuple = Tuple.of("John","Doe");
        tuple.addFirst("Adrian")
                .addFirst("Wall")
                .addFirst(50);
        assertEquals(5, tuple.depth());
        assertEquals("Tuple2=[50,Wall,Adrian,John,Doe]", tuple.toString());
    }

    @Test
    public void testIndexOf_Pass() {
        assertEquals(0, tuple.indexOf("John"));
        assertEquals(1, tuple.indexOf("Doe"));
        assertEquals(-1, tuple.indexOf(null));
        assertEquals(-1, tuple.indexOf(999));
    }

    @Test
    public void testEquals_Pass() {
        Tuple2 aTuple = Tuple.of("John","Doe");
        assertEquals(this.tuple,aTuple);

        Tuple2 aTuple_3 = Tuple.of("Adrian","Wall");
        assertNotEquals(this.tuple,aTuple_3);
    }

    @Test
    public void testMatch_Pass() {
        Holder<Integer> found = Holder.of(0);
        tuple
            .match(Matcher.allOf("Adrian","Wall"), (a, b) -> {
                logger.info("Matched on \"Adrian,Wall\" tuple -- should not match");
                found.set(found.get()+1);
            })
            .match(Matcher.allOf(1,2,3), (a, b) -> {
                logger.info("Matched on \"1,2,3\" tuple -- should not match");
                found.set(found.get()+1);
            })
            .match(Matcher.allOf("John"), (a, b) -> {
                logger.info("Matched \"John\" tuple on: {} {}",a,b);
                found.set(found.get()+1);
            })
            .match(Matcher.allOf("John","Doe"), (a, b) -> {
                logger.info("Matched on \"John,Doe\" tuple: {} {}",a,b);
                found.set(found.get()+1);
            })
            .match(Matcher.anyOf(null,"^Doe$"),(a, b) -> {
                logger.info("Matched (any) \"null,^Doe$\" on tuple: {} {}",a,b);
                found.set(found.get()+1);
            })
            .match(Matcher.setOf("^Doe$","^John$"),(a,b) -> {
                logger.info("Matched (set) \"^Doe$,^John$\" on tuple: {} {}",a,b);
                found.set(found.get()+1);
            });

        tuple_2
            .match(Matcher.allOf(1,2), (a, b) -> {
                logger.info("Matched on \"1,2\" tuple");
                found.set(found.get()+1);
            })
            .match(Matcher.allOf(1), (a, b) -> {
                logger.info("Matched on \"1\" tuple: {} {}",a,b);
                found.set(found.get()+1);
            })
            .match(Matcher.allOf(3,1),(a, b) -> {
                logger.info("Matched on \"3,1\" tuple -- should not match");
                found.set(found.get()+1);
            })
            .match(Matcher.anyOf(0,2),(a, b) -> {
                logger.info("Matched (any) \"0,2\" on tuple: {} {}",a,b);
                found.set(found.get()+1);
            });
        tuple_3
           .match(Matcher.allOf(null,"Wall"), (a, b) -> {
               logger.info("Matched on \"null,Wall\" tuple: {} {}",a,b);
               found.set(found.get()+1);
           });

        assertEquals(8, found.get());
    }

    @Test
    public void testHashCode_Pass() {
        Tuple2 aTuple = Tuple.of("John","Doe");
        assertEquals(this.tuple.hashCode(),aTuple.hashCode());

        Tuple2 aTuple_2 = Tuple.of("Adrian","Wall");
        assertNotEquals(this.tuple.hashCode(),aTuple_2.hashCode());
    }

    @Test
    public void testGet_Pass() {
        assertEquals("John", tuple.get(0));
        assertEquals("Doe", tuple.get(1));
    }

    @Test
    public void testGet_Fail() {
        assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(2));
    }

    @Test
    public void testIterator_Pass() {
        Iterator<TupleElement> it = tuple.iterator();

        boolean found = false;
        while (it.hasNext()) {
            TupleElement element = it.next();
            assertSame(tuple,element.owner());
            if ("Doe".equals(element.value())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testIterator_Fail() {
        Iterator<TupleElement> it = tuple.iterator();

        while (it.hasNext())
            it.next();

        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    public void testCompareTo_Pass() {
        Tuple2 aTuple = Tuple.of(1,3);
        Tuple2 aTuple_2 = Tuple.of(1,2);
        Tuple2 aTuple_3 = Tuple.of(0,0);
        Tuple2 aTuple_4 = Tuple.of(null,0);
        Tuple2 aTuple_5 = Tuple.of(2,1);
        Tuple2 aTuple_6 = Tuple.of(1,1);
        Tuple2 aTuple_7 = Tuple.of(1,1);
        Tuple3 aTuple_8 = Tuple.of(2,1,1);
        Tuple3 aTuple_9 = Tuple.of(0,3,1);
        Tuple3 aTuple_10 = Tuple.of(-1,99,1);
        Tuple2 aTuple_11 = Tuple.of(9,9);
        Tuple0 aTuple_12 = Tuple.of();

        List<Tuple> list = Arrays.asList(aTuple, aTuple_2, aTuple_3, aTuple_4, aTuple_5, aTuple_6, aTuple_7,aTuple_8,
                aTuple_9,aTuple_10,aTuple_11,aTuple_12);

        List<Tuple> sorted = list.stream()
                .sorted()
                .collect(Collectors.toList());

        assertEquals(aTuple_12, sorted.get(0)); // Top
        assertEquals(aTuple_8, sorted.get(11)); // Bottom

        // Sort equal tuples
        Tuple2 aTuple_13 = Tuple.of("John","Doe");
        Tuple2 aTuple_14 = Tuple.of("James","Brown");
        list = Arrays.asList(aTuple_13,this.tuple,aTuple_14);

        Collections.sort(list);
        assertEquals(aTuple_14,list.get(0));
        assertEquals(aTuple_13,list.get(2));
    }

    @Test
    public void testAsPair_Pass() {
        Pair<Integer,Integer> pair = tuple_2.asPair();

        assertEquals(1,pair._1());
        assertEquals(2,pair._2());
        assertEquals(tuple_2,pair.toTuple());
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testCompareTo_Fail() {
        assertThrows(NullPointerException.class, () -> tuple.compareTo(null));

        Tuple2<String,Integer> aTuple_2 = Tuple.of("John",29);
        Tuple2<String,Integer> aTuple_3 = Tuple.of("James",22);
        Tuple3<String,String,Integer> aTuple_4 = Tuple.of("James","Charles",22);
        Tuple3<String,Integer,String> aTuple_5 = Tuple.of("James",22,"Charles");
        List<Tuple> list = Arrays.asList(aTuple_4,aTuple_2,aTuple_3,aTuple_5);

        assertThrows(TupleComparableException.class,() -> Collections.sort(list));
    }

    @Test
    public void testToArray_Pass() {
        Object[] array = tuple.toArray();

        assertEquals("John",array[0]);
        assertEquals("Doe",array[1]);
    }

    @Test
    public void testToList_Pass() {
        List<?> list = tuple.toList();

        assertEquals(2,list.size());
        assertEquals("John",list.get(0));
        assertEquals("Doe",list.get(1));
    }

    @Test
    public void testToMap_Pass() {
        Map<String,?> map = tuple.toMap(k -> "index"+k);

        assertEquals("John",map.get("index0"));
        assertEquals("Doe",map.get("index1"));
    }

    @Test
    public void testAddAt_Pass() {
        Tuple3 tuple3 = tuple_2.addAt1("a");
        Assertions.assertEquals(Tuple.of("a",1,2),tuple3);

        tuple3 = tuple_2.addAt2("a");
        Assertions.assertEquals(Tuple.of(1,"a",2),tuple3);
    }

    @Test
    public void testSerialization_Pass() throws IOException, ClassNotFoundException  {
        // Serialize
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(ostream);

        out.writeObject(tuple);
        out.close();
        ostream.close();

        // Deserialization
        byte[] bytes = ostream.toByteArray();

        ByteArrayInputStream istream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(istream);
        Tuple2 aTuple_2 = (Tuple2) in.readObject();

        assertEquals(2,aTuple_2.depth());
        assertEquals("John",aTuple_2.value1());
        assertEquals("Doe",aTuple_2.value2());
        in.close();
        istream.close();
    }

    @Test
    public void testPositionOf_Pass() {
        assertEquals(2,tuple_2.positionOf(2));
    }

    @Test
    public void testContains_Pass() {
        assertTrue(tuple.contains("John"));
    }

    @Test
    public void testFromIterable_Pass() {
       List<String> list = Arrays.asList("John","Doe");

       Maybe<Tuple2<String,String>> maybeTuple = Tuple2.fromIterable(list);

       maybeTuple.ifPresentOrElse(tuple -> {
           assertEquals("John",tuple.value1());
           assertEquals("Doe",tuple.value2());
       }, Assertions::fail);

       list = Arrays.asList("John");
       maybeTuple = Tuple2.fromIterable(list);

       assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testHopTo_Pass() {
        Tuple2 tuple2 = tuple_2.hopTo1();
        Assertions.assertEquals(Tuple.of(1,2), tuple2);

        Tuple1 tuple1 = tuple_2.hopTo2();
        Assertions.assertEquals(Tuple.of(2), tuple1);
    }

    @Test
    public void testSpliceAt_Pass() {
        Tuple2<Tuple0,Tuple2<Integer,Integer>>
                spliced1 = tuple_2.spliceAt1();
        assertEquals(2, spliced1.depth());
        assertEquals(0, spliced1.value1().depth());
        assertEquals(2, spliced1.value2().value2());

        Tuple2<Tuple1<Integer>,Tuple1<Integer>>
                spliced2 = tuple_2.spliceAt2();
        assertEquals(2, spliced2.depth());
        assertEquals(1, spliced2.value1().value1());
        assertEquals(2, spliced2.value2().value1());
    }

    @Test
    public void testSplice_Fail() {
        assertThrows(IllegalArgumentException.class, () -> tuple_2.splice(0));
        assertThrows(IllegalArgumentException.class, () -> tuple_2.splice(3));
    }

    @Test
    public void testJoin_Pass() {
        Tuple3 aTuple3 = tuple_2.join("end-of-tuple");
        assertEquals("end-of-tuple",aTuple3.value3());

        Tuple2 aTuple2 = tuple_2.join(Tuple.of());
        Assertions.assertEquals(Tuple.of(1,2),aTuple2);

        Tuple3 aTuple3_2 = tuple_2.join(Tuple.of(3));
        Assertions.assertEquals(Tuple.of(1,2,3),aTuple3_2);

        Tuple4 aTuple4 = tuple_2.join(Tuple.of(3,4));
        Assertions.assertEquals(Tuple.of(1,2,3,4),aTuple4);

        Tuple5 aTuple5 = tuple_2.join(Tuple.of(3,4,5));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5),aTuple5);

        Tuple6 aTuple6 = tuple_2.join(Tuple.of(3,4,5,6));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6),aTuple6);

        Tuple7 aTuple7 = tuple_2.join(Tuple.of(3,4,5,6,7));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7),aTuple7);

        Tuple8 aTuple8 = tuple_2.join(Tuple.of(3,4,5,6,7,8));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8),aTuple8);

        Tuple9 aTuple9 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9),aTuple9);

        Tuple10 aTuple10 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10),aTuple10);

        Tuple11 aTuple11 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11),aTuple11);

        Tuple12 aTuple12 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12),aTuple12);

        Tuple13 aTuple13 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13),aTuple13);

        Tuple14 aTuple14 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14),aTuple14);

        Tuple15 aTuple15 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14,15));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),aTuple15);

        Tuple16 aTuple16 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14,15,16));
        Assertions.assertEquals(Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),aTuple16);
    }

    @Test
    public void testJoin_Fail() {
        assertThrows(TupleOverflowException.class, () -> tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)));
    }

    @Test
    public void testRemove_Pass() {
        Tuple1 tuple1 = tuple_2.removeAt1();
        assertEquals(Tuple.of(2),tuple1);

        tuple1 = tuple_2.removeAt2();
        assertEquals(Tuple.of(1),tuple1);
    }

    @Test
    public void testRemove_Fail() {
        assertThrows(IllegalArgumentException.class, () -> tuple_2.remove("does-not-exist"));
    }

    @Test
    public void testRotateRight_Pass() {
        Tuple2 tuple2;

        tuple2 = tuple_2.rotateRight1();
        assertEquals(Tuple.of(2,1),tuple2);
    }

    @Test
    public void testRotateLeft_Pass() {
        Tuple2 tuple2 = tuple_2.rotateLeft1();
        assertEquals(Tuple.of(2,1),tuple2);
    }
    
    @Test
    public void testTruncateAt_Pass() {
        Tuple0 aTuple0 = tuple_2.truncateAt1();
        Assertions.assertEquals(Tuple.of(),aTuple0);

        Tuple1 aTuple1 = tuple_2.truncateAt2();
        Assertions.assertEquals(Tuple.of(1),aTuple1);
    }

    @Test
    public void testMap_Pass() {
        List<String> list = tuple_2.map((a,b) -> Arrays.asList("Item :"+a,"Item :"+b));

        assertEquals("Item :1",list.get(0));
        assertEquals("Item :2",list.get(1));
    }

    @Test
    public void testMapAt_Pass() {
        Tuple2 aTuple2;
        aTuple2 = tuple_2.mapAt1(a -> 0);
        Assertions.assertEquals(Tuple.of(0,2),aTuple2);

        aTuple2 = tuple_2.mapAt2(a -> 0);
        Assertions.assertEquals(Tuple.of(1,0),aTuple2);
    }
}
