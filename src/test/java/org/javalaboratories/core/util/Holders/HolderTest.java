package org.javalaboratories.core.util.Holders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class HolderTest {
    private Holder<String> readWriteHolder;
    private Holder<Person> readOnlyHolder;
    private Holder<String> synchronizedHolder;

    private class Person {
        private String name;
        private int age;

        public Person(Person p) {
            this(p.name,p.age);
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age &&
                    name.equals(person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return "Person[" +
                    "name='" + name + '\'' +
                    ']';
        }
    }

    @BeforeEach
    public void setup() {
        Person person = new Person("John Doe",26);

        readWriteHolder = Holder.of("Hello World");

        readOnlyHolder = Holder.of(new Person(person));
        readOnlyHolder = Holder.readOnly(readOnlyHolder);

        synchronizedHolder = Holder.synchronizedHolder(readWriteHolder);
    }

    @Test
    public void testReadOnlyCopyConstructor_Pass() {
        Holder<String> rholder = Holder.readOnly(readWriteHolder);

        assertEquals("Hello World", rholder.get());
        assertThrows(UnsupportedOperationException.class, () -> rholder.set("John Bishop"));
    }

    @Test
    public void testMutableCopyConstructor_Pass() {
        Holder<Person> mholder = Holder.readWrite(readOnlyHolder);

        assertEquals("John Doe", mholder.get().getName());
        mholder.set(new Person("James Smith",30));
        assertEquals("James Smith",mholder.get().getName());
    }

    @Test
    public void testSet_Pass() {
        readWriteHolder.set("Hello Galaxy");
        synchronizedHolder.set("Hello Galaxy");
        assertThrows(UnsupportedOperationException.class, () -> readOnlyHolder.set(new Person("John Smith",26)));
        assertEquals("Hello Galaxy", readWriteHolder.get());
        assertEquals("Hello Galaxy", synchronizedHolder.get());
    }

    @Test
    public void testFunctorReadWrite_Pass() {
        Holder<Integer> holder = readWriteHolder
                .map(String::length);

        int length = holder
                .fold(0,n -> n);

        assertEquals(length, 11);
    }

    @Test
    public void testFlatMap_Pass() {
        Holder<Integer> nested = Holder.of(5);
        int value = nested
                .flatMap(n -> Holder.of(n +2))
                .fold(0,n -> n);

        assertEquals(value,7);
    }

    @Test
    public void testFunctorReadOnly_Pass() {
        Holder<Integer> holder = readOnlyHolder
                .map(p -> p.getName().length());

        int length = holder
                .fold(0,n -> n);

        assertEquals(length, 8);
        assertThrows(UnsupportedOperationException.class, () -> holder.set(10));
    }



    @Test
    public void testGet_Pass() {
        assertEquals("Hello World", readWriteHolder.get());
        assertEquals("John Doe", readOnlyHolder.get().getName());
        assertEquals("Hello World", synchronizedHolder.get());
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Holder.of("Hello World"), readWriteHolder);
        assertNotEquals(Holder.of(new Person("John Doe",26)), readOnlyHolder);
        assertEquals(Holder.synchronizedHolder(readWriteHolder), synchronizedHolder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Holder.of("Hello World").hashCode(), readWriteHolder.hashCode());
        assertEquals(Holder.of(new Person("John Doe",26)).hashCode(), readOnlyHolder.hashCode());
        assertEquals(Holder.synchronizedHolder(readWriteHolder).hashCode(), synchronizedHolder.hashCode());
    }

    @Test
    public void testForEach_Pass() {
        Holder<Boolean> looped = Holder.of(false);
        readWriteHolder.forEach(s -> looped.set(true));

        assertInstanceOf(Iterable.class, readWriteHolder);
        assertTrue(looped.get());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", readWriteHolder.toString());
        assertEquals("Holder[value=Person[name='John Doe']]", readOnlyHolder.toString());
        assertEquals("Holder[value=Hello World]", synchronizedHolder.toString());
    }
}
