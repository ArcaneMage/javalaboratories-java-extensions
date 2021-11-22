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
import java.util.*;
import java.util.function.Function;

public class SmartLinkedList<T> implements  Iterable<T>, Cloneable, Serializable  {

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

    public SmartLinkedList() {
        depth = 0;
        head = null;
        tail = null;
    }

    public SmartLinkedList(final T... elements) {
        this();
        for(T element: elements) 
            add(element);
    }

    @Override
    public Object clone() {
        SmartLinkedList<T> clone = parentClone();

        clone.depth = 0;
        clone.head = clone.tail = null;

        for (T element: this)
            clone.add(element);
        return clone;
    }

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

    @Override
    public int hashCode() {
        int result = 1;
        for (T element: this)
            result = 31 * result + element.hashCode();
        result += Objects.hash(depth);
        return result;
    }

    public final SmartLinkedList<T> add(final T element) {
        linkToLastNode(element);
        return this;
    }

    public final SmartLinkedList<T> addFirst(final T element) {
        linkToFirstNode(element);
        return this;
    }

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

    public ReverseIterator<T> reverseIterator() {
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

    public int depth() {
        return depth;
    }

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

    public boolean isEmpty() {
        return head == null && tail == null;
    }

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
            ReverseIterator<T> iter = this.reverseIterator();
            while (i++ <= index && iter.hasPrevious())
                result = iter.previous();
        }
        return result;
    }

    public T[] toArray() {
        Class<?> clazz = depth == 0 ? Object.class : get(0).getClass();
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz,depth) ;
        int i = 0;
        for (Node<T> node = head; node != null; node = node.next)
            result[i++] = node.element;
        return result;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();
        for (Node<T> node = head; node != null; node = node.next)
            result.add(node.element);
        return result;
    }

    public <K> Map<K,T> toMap(Function<? super Integer, ? extends K> keyMapper) {
        Map<K,T> result = new LinkedHashMap<>();
        int i = 0;
        for (Node<T> node = head; node != null; node = node.next)
            result.put(keyMapper.apply(i++),node.element);

        return result;
    }

    @Override
    public String toString() {
         StringJoiner joiner = new StringJoiner(",");
        for (T element : this)
            if (element == null) joiner.add("null");
            else joiner.add(element.toString());

        return String.format("[%s]",joiner.toString());
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
