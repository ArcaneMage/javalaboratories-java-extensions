package org.javalaboratories.core.tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
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
 * This class and derived classes are immutable.
 *
 * @author Kevin Henry
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractTupleContainer implements TupleContainer {

    public static final long serialVersionUID = -8849025043951429993L;

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

    AbstractTupleContainer() {
        depth = 0;
        head = null;
        tail = null;
    }

    AbstractTupleContainer(Object... elements) {
        this();
        for (Object element : elements)
            add(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(TupleContainer o) {
        if ( o == null )
            throw new NullPointerException();

        if ( this.equals(o) )
            return 0;

        // Compare depth (sort by depth first)
        int result = this.depth() - o.depth();
        if ( result != 0 )
            return result;

        // Then by tuple elements
        Iterator<Object> iter1 = this.iterator();
        Iterator<Object> iter2 = o.iterator();
        try {
            while (iter1.hasNext() && iter2.hasNext() && result == 0)
                result = Comparators.compare(iter1.next(), iter2.next());
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
        return head == null && tail == null;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleContainer objects = (TupleContainer) o;

        if ( this.depth() != objects.depth())
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;
        for (Object object : this)
            result = 31 * result + (object == null ? 0 : object.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depth() { return depth; }

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
        Map<K,Object> result = new LinkedHashMap<>();
        int i = 0;
        for (Node node = head; node != null; node = node.next )
            result.put(keyMapper.apply(i++),node.element);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        Object[] result = new Object[depth];
        int i = 0;
        for (Node node = head; node != null; node = node.next )
            result[i++] = node.element;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> toList() {
        List<Object> result = new ArrayList<>();
        for (Node node = head; node != null; node = node.next )
            result.add(node.element);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",");
        for (Object o : this)
            if (o == null) joiner.add("null");
            else joiner.add(o.toString());

        return String.format("%s=[%s]",this.getClass().getSimpleName(),joiner.toString());
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
        if ( position < 1 || position > (depth()) )
            throw new IllegalArgumentException("Position must be non-zero and less than or equal depth");
    }

    final AbstractTupleContainer add(Object element) {
        linkToLastNode(element);
        return this;
    }

    final AbstractTupleContainer addFirst(Object element) {
        linkToFirstNode(element);
        return this;
    }

    final int indexOf(Object element) {
        int result = 0;
        for (Node node = head; node != null; node = node.next) {
            if ( (element == null && node.element == null) ||
                    (element != null && element.equals(node.element)) )
                return result;
            result++;
        }
        return -1;
    }

    final Object get(int index) {
        validateNodeIndex(index);
        Object result = null;
        int i = 0;
        for (Node node = head; node != null; node = node.next ) {
            if ( index == i ) {
                result = node.element;
                break;
            }
            else i++;
        }
        return result;
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