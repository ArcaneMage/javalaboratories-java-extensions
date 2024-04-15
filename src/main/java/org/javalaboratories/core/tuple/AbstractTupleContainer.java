/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.tuple;

import org.javalaboratories.core.collection.SmartLinkedList;

import java.io.Serial;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Tuples inherit from this class.
 * <p>
 * The tuple is considered to be a container. It currently uses a simple doubly
 * linked list data structure. A decision was made to use this approach rather
 * than reusing {@link LinkedList} because it is important to reduce the overhead
 * of the structure of the tuple as well as minimise typing where possible in
 * concrete classes. This class implements {@link Comparable},
 * {@link java.io.Serializable} and {@link Iterable} interfaces.
 * <p>
 * Although the internal data structure of the container is a linked list, it is
 * a linked list of {@link Object} element types, however, when the iterator
 * traverses the {@code Node} objects, it returns {@link TupleElement}
 * implementation with information pertaining to current the element. This is
 * useful for some client classes that require additional information such as the
 * {@link DefaultTupleElementMatcher} class. This approach is also conservative
 * of memory.
 * <p>
 * This class and derived classes are immutable.
 *
 * @author Kevin Henry
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractTupleContainer implements TupleContainer {
    @Serial
    public static final long serialVersionUID = -8849025043951429993L;

    private final SmartLinkedList<Object> adaptee;

    AbstractTupleContainer() {
        adaptee = new SmartLinkedList<>();
    }

    AbstractTupleContainer(Object... elements) {
        this();
        for (Object element : elements)
            adaptee.add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(TupleContainer o) {
        if (o == null)
            throw new NullPointerException();

        if (this.equals(o))
            return 0;

        // Compare depth (sort by depth first)
        int result = this.depth() - o.depth();
        if (result != 0)
            return result;

        // Then by tuple elements
        Iterator<TupleElement> iter1 = this.iterator();
        Iterator<TupleElement> iter2 = o.iterator();
        try {
            while (iter1.hasNext() && iter2.hasNext() && result == 0)
                result = Comparators.compare(iter1.next().value(), iter2.next().value());
        } catch (ClassCastException e) {
            throw new TupleComparableException("Element types of tuples, with equal depth, must be in the same order");
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object element) {
        return indexOf(element) > -1;
    }

    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<TupleElement> iterator() {

        return new Iterator<TupleElement>() {
            final Iterator<Object> iter = adaptee.iterator();
            int index = 0;
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public TupleElement next() {
                Object element = iter.next();
                TupleElement result = new TupleElement() {
                    private final int position = index + 1;
                    @Override
                    public <T> T value() {
                        @SuppressWarnings("unchecked")
                        T result = (T) element;
                        return result;
                    }

                    @Override
                    public boolean isString() {
                        return element instanceof String;
                    }

                    @Override
                    public <T extends TupleContainer> T owner() {
                        @SuppressWarnings("unchecked")
                        T result = (T) AbstractTupleContainer.this;
                        return result;
                    }

                    @Override
                    public int position() {
                        return position;
                    }
                };
                index++;
                return result;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleContainer objects = (TupleContainer) o;

        if (this.depth() != objects.depth())
            return false;

        Iterator<TupleElement> iter1 = this.iterator();
        Iterator<TupleElement> iter2 = objects.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            TupleElement e1 = iter1.next();
            TupleElement e2 = iter2.next();
            if (!Objects.equals(e1.value(),e2.value()))
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;
        for (TupleElement element : this)
            result = 31 * result + (element.value() == null ? 0 : element.value().hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depth() { return adaptee.depth(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public int positionOf(Object object) {
        return indexOf(object) + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> Map<K,?> toMap(Function<? super Integer, ? extends K> keyMapper) {
        return adaptee.toMap(keyMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return adaptee.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> toList() {
        return adaptee.toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String s = adaptee.toString();
        return String.format("%s=%s",this.getClass().getSimpleName(),s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T value(int position) {
        verify(position);
        @SuppressWarnings("unchecked")
        T result = (T) this.get(position -1);
        return result;
    }

    protected final void verify(int position) {
        if (position < 1 || position > (depth()))
            throw new IllegalArgumentException("Position must be non-zero and less than or equal depth");
    }

    final AbstractTupleContainer add(Object element) {
        adaptee.add(element);
        return this;
    }

    final AbstractTupleContainer addFirst(Object element) {
        adaptee.addFirst(element);
        return this;
    }

    final int indexOf(Object element) {
        return adaptee.indexOf(element);
    }

    final Object get(int index) {
        return adaptee.get(index);
    }
}

final class Comparators {
    @SuppressWarnings("unchecked")
    static <T> int compare(T v1, T v2) {
        return Objects.compare(v1,v2, Comparator.nullsLast((a, b) -> ((Comparable<T>) a).compareTo(b)));
    }
}