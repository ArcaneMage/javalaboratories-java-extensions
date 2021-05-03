package org.javalaboratories.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("WeakerAccess")
public class HolderTest {
    private Holder<String> writableHolder;
    private Holder<Person> readableHolder;
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

        writableHolder = Holders.writableHolder("Hello World");
        readableHolder = Holders.readableHolder(() -> new Person(person));
        synchronizedHolder = Holders.synchronizedHolder(writableHolder);
    }

    @Test
    public void testSet_Pass() {
        writableHolder.set("Hello Galaxy");
        synchronizedHolder.set("Hello Galaxy");
        assertThrows(UnsupportedOperationException.class, () -> readableHolder.set(new Person("John Smith",26)));
        assertEquals("Hello Galaxy", writableHolder.get());
        assertEquals("Hello Galaxy", synchronizedHolder.get());

    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World", writableHolder.get());
        assertEquals("John Doe", readableHolder.get().getName());
        assertEquals("Hello World", synchronizedHolder.get());
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Holders.writableHolder("Hello World"), writableHolder);
        assertEquals(Holders.readableHolder(() -> new Person("John Doe",26)), readableHolder);
        assertEquals(Holders.synchronizedHolder(writableHolder), synchronizedHolder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Holders.writableHolder("Hello World").hashCode(), writableHolder.hashCode());
        assertEquals(Holders.readableHolder(() -> new Person("John Doe",26)).hashCode(), readableHolder.hashCode());
        assertEquals(Holders.synchronizedHolder(writableHolder).hashCode(), synchronizedHolder.hashCode());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", writableHolder.toString());
        assertEquals("Holder[value=Person[name='John Doe']]", readableHolder.toString());
        assertEquals("Holder[value=Hello World]", synchronizedHolder.toString());
    }
}
