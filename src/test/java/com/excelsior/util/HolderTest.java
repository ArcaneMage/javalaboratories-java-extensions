package com.excelsior.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("WeakerAccess")
public class HolderTest {
    private Holder<String> mutableHolder;
    private Holder<Person> immutableHolder;
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

        mutableHolder = Holders.mutableHolder("Hello World");
        immutableHolder = Holders.immutableHolder(() -> new Person(person));
        synchronizedHolder = Holders.synchronizedHolder(mutableHolder);
    }

    @Test
    public void testSet_Pass() {
        mutableHolder.set("Hello Galaxy");
        synchronizedHolder.set("Hello Galaxy");
        assertThrows(UnsupportedOperationException.class, () -> immutableHolder.set(new Person("John Smith",26)));
        assertEquals("Hello Galaxy", mutableHolder.get());
        assertEquals("Hello Galaxy", synchronizedHolder.get());

    }

    @Test
    public void testGet_Pass() {
        assertEquals("Hello World", mutableHolder.get());
        assertEquals(new Person("John Doe",26), immutableHolder.get());
        assertEquals("Hello World", synchronizedHolder.get());
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(Holders.mutableHolder("Hello World"), mutableHolder);
        assertEquals(Holders.immutableHolder(() -> new Person("John Doe",26)), immutableHolder);
        assertEquals(Holders.synchronizedHolder(mutableHolder), synchronizedHolder);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(Holders.mutableHolder("Hello World").hashCode(), mutableHolder.hashCode());
        assertEquals(Holders.immutableHolder(() -> new Person("John Doe",26)).hashCode(), immutableHolder.hashCode());
        assertEquals(Holders.synchronizedHolder(mutableHolder).hashCode(), synchronizedHolder.hashCode());
    }

    @Test
    public void testToString_Pass() {
        assertEquals("Holder[value=Hello World]", mutableHolder.toString());
        assertEquals("Holder[value=Person[name='John Doe']]", immutableHolder.toString());
        assertEquals("Holder[value=Hello World]", synchronizedHolder.toString());
    }
}
