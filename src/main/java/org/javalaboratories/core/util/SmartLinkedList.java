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
package org.javalaboratories.core.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This {@code linked list} implements the doubly linked-list approach. With
 * this approach, it is possible to traverse the {@link Node} forwards or
 * backwards as desired, it provides an {@link ReverseIterator} for this
 * purpose.
 * <p>
 * It is considered "smart" in that the {@link SmartLinkedList#get(int)} method
 * will automatically decide whether to sequentially search from the {@code head}
 * or the {@code tail} depending on which "end" is the closest to the
 * requested {@code index}.
 * <p>
 * Currently considering introducing a small LRU cache to speed up retrieval to
 * minimise sequential searches.
 *
 * @param <T> type of elements in {@link SmartLinkedList}
 */
public class SmartLinkedList<T> implements Iterable<T>, Cloneable, Serializable  {

    private static final long serialVersionUID = 379872715184844475L;

    private static class Node<T> {
        public T element;
        public Node<T> prev;
        public Node<T> next;

        public Node(T element) {
            this(null,element,null);
        }

        public Node(Node<T> prev, T element, Node<T> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }

    private transient int depth;
    private transient Node<T> head;
    private transient Node<T> tail;

    /**
     * Default constructor
     */
    public SmartLinkedList() {
        depth = 0;
        head = null;
        tail = null;
    }

    /**
     * Constructor
     * <p>
     * Initialises linked list with provided {@code elements}
     *
     * @param elements to populate linked-list.
     */
    @SafeVarargs
    public SmartLinkedList(final T... elements) {
        this();
        Objects.requireNonNull(elements,"No elements in parameter");
        for(T element: elements) 
            add(element);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        SmartLinkedList<T> clone = parentClone();

        clone.depth = 0;
        clone.head = clone.tail = null;

        for (T element: this)
            clone.add(element);
        return clone;
    }

    /**
     * {@inheritDoc}
     *
     * Performs a shallow comparison with the {@code equals} method of each
     * element in the list.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartLinkedList<?> that = (SmartLinkedList<?>) o;
        if (depth != that.depth)
            return false;
        Iterator<T> iter1 = this.iterator();
        Iterator<?> iter2 = that.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            T e1 = iter1.next();
            Object e2 = iter2.next();
            if (!e1.equals(e2))
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
        for (T element: this)
            result = 31 * result + element.hashCode();
        result += Objects.hash(depth);
        return result;
    }

    /**
     * Adds an element to the linked-list.
     * <p>
     * The element is added to the end of the list.
     *
     * @param element to add to the list.
     * @return this linked-list
     */
    public final SmartLinkedList<T> add(final T element) {
        linkToLastNode(element);
        return this;
    }

    /**
     * Adds an element to the linked-list.
     * <p>
     * The element is added to the beginning o the list.
     *
     * @param element to add to the list.
     * @return this linked-list
     */
    public final SmartLinkedList<T> addFirst(final T element) {
        linkToFirstNode(element);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> node = head;

            @Override
            public boolean hasNext() {
                return !isEmpty() && node != null;
            }

            @Override
            public T next() {
                if (node == null)
                    throw new NoSuchElementException();
                T result = node.element;
                node = node.next;
                return result;
            }
        };
    }

    /**
     * Returns a {@link ReverseIterator} to allow traversal from the {@code }tail
     * to the {@code head}.
     *
     * @return {@link ReverseIterator} instance.
     */
    public ReverseIterator<T> reverse() {
        return new ReverseIterator<T>() {
            Node<T> node = tail;

            @Override
            public boolean hasPrevious() {
                return !isEmpty() && node != null;
            }

            @Override
            public T previous() {
                if (node == null)
                    throw new NoSuchElementException();
                T result = node.element;
                node = node.prev;
                return result;
            }
        };
    }

    /**
     * @return the number of elements in the list.
     */
    public int depth() {
        return depth;
    }

    /**
     * Returns the first index of the element that equals {@code element}.
     *
     * @param element with which to search.
     * @return index of element.
     */
    public final int indexOf(final T element) {
        int result = 0;
        for (Node<T> node = head; node != null; node = node.next) {
            if ((element == null && node.element == null) ||
                    (element != null && element.equals(node.element)))
                return result;
            result++;
        }
        return -1;
    }

    /**
     * @return {@code true} if list is empty.
     */
    public boolean isEmpty() {
        return head == null && tail == null;
    }

    /**
     * Finds and returns element at {@code findex} location in the linked list.
     * <p>
     * A sequential search is performed but not always necessarily from the
     * top of the list. This primarily depends on the {@code findex} value: if
     * the value is nearest the top, the the sequential search shall start
     * from there, otherwise the search is performed from the other end.
     *
     * @param findex index at which element resides in the list.
     * @return element from the list.
     */
    public final T get(int findex) {
        validateNodeIndex(findex);
        T result = null;
        int i = 0;
        if ( findex < depth / 2 ) {
            Iterator<T> iter = this.iterator();
            while (i++ <= findex && iter.hasNext())
                result = iter.next();
        } else {
            int index = depth - findex -1;
            ReverseIterator<T> iter = this.reverse();
            while (i++ <= index && iter.hasPrevious())
                result = iter.previous();
        }
        return result;
    }

    /**
     * Returns an array of elements that are in this linked-list.
     *
     * @return an array of elements.
     */
    public T[] toArray() {
        Class<?> clazz = depth == 0 ? Object.class : get(0).getClass();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz,depth);
        AtomicInteger index = new AtomicInteger(0);
        forEach(e -> result[index.getAndIncrement()] = e);
        return result;
    }

    /**
     * Returns a {@link List} of elements that compliments this linked-list.
     *
     * @return a {@link List} object.
     */
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        forEach(result::add);
        return result;
    }

    /**
     * Returns a {@link Map} of elements from this linked-list.
     * <p>
     * The {@code keys} are determined from the resultant calls to the {@code
     * keyMapper} function.
     *
     * @param keyMapper function to calculate key values.
     * @param <K> type of key
     * @return a {@link Map} of elements, each with an associated key.
     */
    public <K> Map<K,T> toMap(Function<? super Integer, ? extends K> keyMapper) {
        Map<K,T> result = new LinkedHashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        forEach(e -> result.put(keyMapper.apply(index.getAndIncrement()),e));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
         StringJoiner sj = new StringJoiner(",");
        for (T element : this)
            if (element == null) sj.add("null");
            else sj.add(element.toString());

        return String.format("[%s]", sj);
    }

    @SuppressWarnings("unchecked")
    private SmartLinkedList<T> parentClone() {
        SmartLinkedList<T> result;
        try {
            result = (SmartLinkedList<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        return result;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) in.readObject();
            linkToLastNode(element);
        }
    }

    private Node<T> linkToFirstNode(final T element) {
        Node<T> link;
        if (isEmpty()) {
            link = new Node<>(element);
            tail = link;
        } else {
            link = new Node<>(null,element, head);
            head.prev = link;
        }
        head = link;
        depth++;
        return head;
    }

    private Node<T> linkToLastNode(final T element) {
        Node<T> link;
        if (isEmpty()) {
            link = new Node<>(element);
            head = link;
        } else {
            link = new Node<>(tail,element,null);
            tail.next = link;
        }
        tail = link;
        depth++;
        return tail;
    }

    private void validateNodeIndex(final int index) {
        if (index < 0 || index > depth -1)
            throw new IndexOutOfBoundsException();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.depth);
        for (Node<T> node = head; node != null; node = node.next)
            out.writeObject(node.element);
    }
}
