package com.excelsior.core.tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public abstract class TupleContainer implements  Comparable<TupleContainer>, Iterable<Object>, Serializable  {

    public static final long serialVersionUID = 6627930356619406060L;

    private static class Node {
        public Object element;
        public Node prev;
        public Node next;

        public Node(Object element) {
            this(null,element,null);
        }

        public Node(Node prev, Object element, Node next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }

    private transient int depth;
    private transient Node head;
    private transient Node tail;

    TupleContainer() {
        depth = 0;
        head = null;
        tail = null;
    }

    TupleContainer(Object... elements) {
        this();
        for (Object element : elements)
            add(element);
    }

    @Override
    public int compareTo(TupleContainer o) {
        if ( o == null )
            throw new NullPointerException();

        // Tuple must be of the same depth for now -- may reconsider
        if ( this.depth != o.depth )
            throw new ClassCastException();

        if ( this.equals(o) )
            return 0;

        int result = 0;
        Iterator<Object> iter1 = this.iterator();
        Iterator<Object> iter2 = o.iterator();

        while ( iter1.hasNext() && iter2.hasNext() && result == 0 ) {
            Object e1 = iter1.next();
            Object e2 = iter2.next();
            result = Comparators.compare(e1,e2);
        }

        return result;
    }

    public boolean isEmpty() {
        return head == null && tail == null;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            Node node = head;
            @Override
            public boolean hasNext() {
                return !isEmpty() && node != null;
            }

            @Override
            public Object next() {
                if ( node == null )
                    throw new NoSuchElementException();
                Object result = node.element;
                node = node.next;
                return result;
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleContainer objects = (TupleContainer) o;

        if ( depth != objects.depth)
            return false;

        Iterator iter1 = this.iterator();
        Iterator iter2 = objects.iterator();

        while ( iter1.hasNext() && iter2.hasNext() ) {
            Object e1 = iter1.next();
            Object e2 = iter2.next();
            if ( !Objects.equals(e1,e2) )
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (Object object : this)
            result = 31 * result + (object == null ? 0 : object.hashCode());
        return result;
    }

    public int indexOf(Object element) {
        int result = 0;
        for (Node node = head; node != null; node = node.next) {
            if ( (element == null && node.element == null) ||
                    (element != null && element.equals(node.element)) )
                return result;
            result++;
        }
        return -1;
    }

    public Object get(int index) {
        validateNodeIndex(index);
        Object result = null;
        int i = 0;
        for ( Node node = head; node != null && result == null; node = node.next ) {
            if ( index == i ) result = node.element;
            else i++;
        }
        return result;
    }

    public int depth() { return depth; }

    public <K> Map<K,?> toMap(Function<? super Integer,? extends K> keyMapper) {
        Map<K,Object> result = new LinkedHashMap<>();
        int i = 0;
        for ( Node node = head; node != null; node = node.next )
            result.put(keyMapper.apply(i++),node.element);

        return result;
    }

    public Object[] toArray() {
        Object[] result = new Object[depth];
        int i = 0;
        for ( Node node = head; node != null; node = node.next )
            result[i++] = node.element;
        return result;
    }

    public List<?> toList() {
        List<Object> result = new ArrayList<>();
        for ( Node node = head; node != null; node = node.next )
            result.add(node.element);
        return result;
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",");
        for (Object o : this)
            if (o == null) joiner.add("null");
            else joiner.add(o.toString());

        return String.format("%s=[%s]",this.getClass().getSimpleName(),joiner.toString());
    }

    final TupleContainer add(Object element) {
        linkToLastNode(element);
        return this;
    }

    final TupleContainer addFirst(Object element) {
        linkToFirstNode(element);
        return this;
    }

    private Node linkToFirstNode(Object element) {
        Node link;
        if ( isEmpty() ) {
            link = new Node(element);
            tail = link;
        } else {
            link = new Node(null,element, head);
            head.prev = link;
        }
        head = link;
        depth++;
        return head;
    }

    private Node linkToLastNode(Object element) {
        Node link;
        if ( isEmpty() ) {
            link = new Node(element);
            head = link;
        } else {
            link = new Node(tail,element,null);
            tail.next = link;
        }
        tail = link;
        depth++;
        return tail;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        for ( int i = 0; i < size; i++ ) {
            Object element = in.readObject();
            linkToLastNode(element);
        }
    }

    private void validateNodeIndex(int index) {
        if ( index < 0 || index > depth -1)
            throw new IndexOutOfBoundsException();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.depth);
        for (Node node = head; node != null; node = node.next)
            out.writeObject(node.element);
    }
}

final class Comparators {
    @SuppressWarnings("unchecked")
    static <T> int compare(T v1, T v2) {
        return Objects.compare(v1,v2, Comparator.nullsLast((a, b) -> ((Comparable<T>) a).compareTo(b)));
    }
}