package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
    public void setup() {
        tuple = Tuple.of("John","Doe");
        tuple_2 = Tuple.of(1,2);
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
        Iterator iter = tuple.iterator();

        boolean found = false;
        while ( iter.hasNext() ) {
            Object element = iter.next();
            if ( "Doe".equals(element) ) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testIterator_Fail() {
        Iterator iter = tuple.iterator();

        while ( iter.hasNext() )
            iter.next();

        assertThrows(NoSuchElementException.class, iter::next);
    }

    @Test
    public void testCompareTo_Pass() {
        Tuple2 aTuple = Tuple.of(1,3);
        Tuple2 aTuple_2 = Tuple.of(1,2);
        Tuple2 aTuple_3 = Tuple.of(0,0);
        Tuple2 aTuple_4 = Tuple.of(null,0);
        Tuple2 aTuple_5 = Tuple.of(2,1);
        Tuple2 aTuple_6 = Tuple.of(1,1);

        List<Tuple2> list = Arrays.asList(aTuple, aTuple_2, aTuple_3, aTuple_4, aTuple_5, aTuple_6);

        List<Tuple2> sorted = list.stream()
                .sorted()
                .collect(Collectors.toList());

        assertEquals(aTuple_3, sorted.get(0)); // Top
        assertEquals(aTuple_4, sorted.get(5)); // Bottom

        // Sort equal tuples
        Tuple2 aTuple_7 = Tuple.of("John","Doe");
        Tuple2 aTuple_8 = Tuple.of("James","Brown");
        list = Arrays.asList(aTuple_7,this.tuple,aTuple_8);

        Collections.sort(list);
        assertEquals(aTuple_8,list.get(0));
        assertEquals(aTuple_7,list.get(2));
    }

    @Test
    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public void testCompareTo_Fail() {
        assertThrows(NullPointerException.class, () -> tuple.compareTo(null));

        Tuple2 aTable_2 = Tuple.of("John","Doe");
        aTable_2.add(43);
        List<Tuple2> list = Arrays.asList(tuple,aTable_2);

        assertThrows(ClassCastException.class,() -> Collections.sort(list));
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
    public void testFromIterable_Pass() {
       List<String> list = Arrays.asList("John","Doe");

       Nullable<Tuple2<String,String>> maybeTuple = Tuple2.fromIterable(list);

       maybeTuple.ifPresentOrElse(tuple -> {
           assertEquals("John",tuple.value1());
           assertEquals("Doe",tuple.value2());
       }, Assertions::fail);


       list = Arrays.asList("John");
       maybeTuple = Tuple2.fromIterable(list);

       assertTrue(maybeTuple.isEmpty());
    }

    @Test
    public void testJoin_Pass() {
        Tuple3 aTuple3 = tuple_2.join(Tuple.of(3));
        assertEquals(aTuple3,Tuple.of(1,2,3));

        Tuple4 aTuple4 = tuple_2.join(Tuple.of(3,4));
        assertEquals(aTuple4,Tuple.of(1,2,3,4));

        Tuple5 aTuple5 = tuple_2.join(Tuple.of(3,4,5));
        assertEquals(aTuple5,Tuple.of(1,2,3,4,5));

        Tuple6 aTuple6 = tuple_2.join(Tuple.of(3,4,5,6));
        assertEquals(aTuple6,Tuple.of(1,2,3,4,5,6));

        Tuple7 aTuple7 = tuple_2.join(Tuple.of(3,4,5,6,7));
        assertEquals(aTuple7,Tuple.of(1,2,3,4,5,6,7));

        Tuple8 aTuple8 = tuple_2.join(Tuple.of(3,4,5,6,7,8));
        assertEquals(aTuple8,Tuple.of(1,2,3,4,5,6,7,8));

        Tuple9 aTuple9 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9));
        assertEquals(aTuple9,Tuple.of(1,2,3,4,5,6,7,8,9));

        Tuple10 aTuple10 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10));
        assertEquals(aTuple10,Tuple.of(1,2,3,4,5,6,7,8,9,10));

        Tuple11 aTuple11 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11));
        assertEquals(aTuple11,Tuple.of(1,2,3,4,5,6,7,8,9,10,11));

        Tuple12 aTuple12 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12));
        assertEquals(aTuple12,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12));

        Tuple13 aTuple13 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13));
        assertEquals(aTuple13,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13));

        Tuple14 aTuple14 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14));
        assertEquals(aTuple14,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14));

        Tuple15 aTuple15 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14,15));
        assertEquals(aTuple15,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));

        Tuple16 aTuple16 = tuple_2.join(Tuple.of(3,4,5,6,7,8,9,10,11,12,13,14,15,16));
        assertEquals(aTuple16,Tuple.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
    }

    @Test
    public void testTruncate_Pass() {
        Tuple1 aTuple1 = tuple_2.truncate1();
        assertEquals(Tuple.of(1),aTuple1);
    }

    @Test
    public void testTestTransform_Pass() {
        Tuple2 aTuple2;

        List<String> list = tuple_2.transform((a,b) -> Arrays.asList("Item :"+a,"Item :"+b));

        assertEquals("Item :1",list.get(0));
        assertEquals("Item :2",list.get(1));

        aTuple2 = tuple_2.transform1(a -> 0);
        assertEquals(Tuple.of(0,2),aTuple2);

        aTuple2 = tuple_2.transform2(a -> 0);
        assertEquals(Tuple.of(1,0),aTuple2);
    }
}
