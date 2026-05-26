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

import java.util.*;

/**
 * Bytes class containing useful byte array operations.
 * <p>
 * Static operations include the ability to copy, concatenate, move and much
 * more. Moreover, it is possible to create a a container of bytes and perform
 * a variety of operations to manipulate the contained bytes.
 * <p>
 * The bytes class is immutable and threadsafe.
 */
public final class Bytes implements Iterable<Byte> {

    private static final int DEFAULT_EXTENSION = 32;
    private static final int UNSIGNED_MASK = 0xFF;

    private byte[] bytes;
    private int marker;

    /**
     * Default constructor of this Bytes container.
     */
    public Bytes() {
        this(new byte[0]);
    }

    /**
     * Constructors this {@link Bytes} container encapsulating the bytes array.
     *
     * @param bytes the bytes array to be encapsulated within this {@link Bytes}
     *              container.
     * @throws NullPointerException when bytes array is null
     */
    public Bytes(final byte... bytes) {
        this.bytes = Objects.requireNonNull(bytes);
        this.marker = this.bytes.length;
    }

    /**
     * Copy constructor
     *
     * @param other the other {@link Bytes} container to be copied.
     */
    public Bytes(final Bytes other) {
        Bytes o = Objects.requireNonNull(other,"Bytes object expected");
        this.bytes = Bytes.copy(o.bytes);
        this.marker = o.marker;
    }

    /**
     * Adds the byte {@code value} to the end of the internal byte array.
     * <p>
     * If the internal {@code marker} is at the end of the array, additional
     * capacity is created to accommodate the value. In other words, the internal
     * array is "resized" when it's at full capacity.
     *
     * @param value the value to be added to the array.
     */
    public void add(final byte value) {
       bytes = createCapacity();
       bytes[this.marker++] = value;
    }

    /**
     * Returns a byte (signed) at {@code index} location.
     *
     * @param index index location of byte to be returned.
     * @return signed byte from index location
     * @throws IndexOutOfBoundsException when index exceeds length of internal
     * byte array; if index is less than 0.
     */
    public byte at(final int index) {
        return (byte) this.at(index,true);
    }

    /**
     * Returns the byte (unsigned/signed) at {@code index} location as
     * an integer.
     * <p>
     * Set the {@code unsigned} parameter to false for signed values; true for
     * unsigned values. The returned value is an integer to ensure that the
     * returned unsigned value does not lose the signed bit due to overflow.
     *
     * @param index index location of byte to be returned.
     * @param unsigned flag to indicate whether byte value is returned signed
     *                or unsigned.
     * @return signed byte from index location
     * @throws IndexOutOfBoundsException when index exceeds length of internal
     * byte array; if index is less than 0.
     */
    public int at(final int index, final boolean unsigned) {
        int i = Objects.checkIndex(index,this.marker);
        return unsigned ? bytes[i] & UNSIGNED_MASK : bytes[i];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bytes bytes1)) return false;
        return marker == bytes1.marker && Objects.deepEquals(bytes, bytes1.bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(bytes), marker);
    }

    /**
     * Returns the "real" length of the byte array.
     * <p>
     * When the byte array is full, additional capacity maybe created. This does
     * not affect the marker.
     *
     * @return length of the byte array.
     */
    public int length() {
        return marker;
    }

    /**
     * Concatenates this {@link Bytes} object to the {@link Bytes} object offered in
     * the parameter.
     *
     * @param bytes bytes object to be concatenated to this object.
     */
    public Bytes concat(final Bytes bytes) {
        Objects.requireNonNull(bytes,"Requires bytes object");
        return new Bytes(Bytes.concat(this.bytes,bytes.bytes,bytes.length()));
    }

    /**
     * Creates a copy of this {@link Bytes} container.
     *
     * @return an independent copy of this container.
     */
    public Bytes copy() {
        return new Bytes(Bytes.copy(this.bytes,this.length()));
    }

    /**
     * Trims/truncates the left-most bytes from this {@link Bytes} container.
     *
     * @param bytes number of bytes to truncate
     */
     public Bytes trimLeft(int bytes) {
        return new Bytes(Bytes.trimLeft(this.bytes,bytes,this.length()));
    }

    /**
     * Trims/truncates the right-most bytes from this {@link Bytes} container.
     *
     * @param bytes number of bytes to truncate
     */
    public Bytes trimRight(int bytes) {
        return new Bytes(Bytes.trimRight(this.bytes,bytes,this.length()));
    }

    /**
     * Returns a copy of sub-bytes from this {@link Bytes} object, specified by
     * supplied {@code beginIndex} and {@code endIndex -1}.
     *
     * @param beginIndex starting index
     * @param endIndex ending index -1.
     * @return a copy of sub-bytes.
     * @throws IndexOutOfBoundsException if beginIndex is negative;
     * endIndex > source length; beginIndex > endIndex.
     */
    public Bytes subBytes(final int beginIndex, final int endIndex) {
        Objects.requireNonNull(bytes,"Requires bytes object");
        return new Bytes(Bytes.subBytes(this.bytes,beginIndex,endIndex));
    }

    /**
     * Copies a block of bytes in {@code source} to a destination specified by
     * {@code destIndex}.
     * <p>
     * A new {@link Bytes} object is returned with copied block of bytes, the
     * original source bytes remain unchanged.
     *
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @param destIndex destination of block
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array; {@code destIndex} cannot accommodate block.
     */
    public Bytes copyBlock(final int beginIndex, final int endIndex, final int destIndex) {
        byte[] scope = Bytes.copy(this.bytes,this.length());
        return new Bytes(Bytes.copyBlock(scope,beginIndex,endIndex,destIndex));
    }

    /**
     * Moves a block of bytes in {@code source} to a destination specified by
     * {@code destIndex}.
     * <p>
     * A new {@link Bytes} object is returned with moved bytes, the original
     * source bytes remain unchanged.
     *
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @param destIndex destination of block
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array; {@code destIndex} cannot accommodate block.
     */
    public Bytes moveBlock(final int beginIndex, final int endIndex, final int destIndex) {
        byte[] scope = Bytes.copy(this.bytes,this.length());
        return new Bytes(Bytes.moveBlock(scope,beginIndex,endIndex,destIndex));
    }

    /**
     * Removes a block of bytes in {@code source}.
     * <p>
     * A new {@link Bytes} object is returned with removed block of bytes, the
     * original source bytes remain unchanged.
     *
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array.
     */
    public Bytes removeBlock(final int beginIndex, final int endIndex) {
        byte[] scope = Bytes.copy(this.bytes,this.length());
        return new Bytes(Bytes.removeBlock(scope,beginIndex,endIndex));
    }

    /**
     * Decodes 32-bit integer from the current {@code index} location.
     * <p>
     * Calculates the integer at the current {@code index} location. Four bytes
     * from the current {@code index} are used to calculate the 32-bit number.
     *
     * @param index index must be greater than 0 and less than
     * {@link this#length() -4}
     * @return a 32 bit integer number
     * @throws IndexOutOfBoundsException exception if insufficient bytes are
     * supplied from current {@code index} location.
     */
    public int valueOf(final int index) {
        int i = Objects.checkFromToIndex(index, index + 4, this.length());
        Bytes sub = this.subBytes(i, i + 4);
        return Bytes.valueOf(sub.bytes);
    }

    /**
     * Returns a copy of the internal array of bytes.
     *
     * @return bytes array.
     */
    public byte[] toArray() {
        return Bytes.copy(this.bytes,this.length());
    }

    /**
     * Returns a string representation of the {@link Bytes} container.
     * <p>
     * All values are signed byte values. For unsigned values, consider the
     * use of {@link this#toString(boolean)}.
     *
     * @return a string representation of the container.
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * Returns a string representation of the {@link Bytes} container.
     * <p>
     * Set the {@code unsigned} parameter to true for unsigned bytes
     * representation.
     *
     * @param unsigned flag to indicate signed or unsigned values.
     * @return a string representation of the container.
     */
    public String toString(boolean unsigned) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        forEach(b -> {
            buffer.append(unsigned ? b & UNSIGNED_MASK : b);
            buffer.append(",");
        });
        return buffer.substring(0, buffer.length() -1)+"]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<>() {
            private int i = 0;
            @Override
            public boolean hasNext() {
                return i < Bytes.this.length();
            }
            @Override
            public Byte next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return Bytes.this.at(i++);
            }
        };
    }

    /**
     * Concatenates first and second byte arrays and returns a new combined
     * byte array.
     *
     * @param first array of bytes
     * @param second array of bytes
     * @return combined byte array.
     */
    public static byte[] concat(final byte[] first, final byte[] second) {
        return Bytes.concat(first,second,second.length);
    }

    /**
     * Concatenates first and second byte arrays and returns a new combined
     * byte array.
     *
     * @param first array of bytes
     * @param second array of bytes
     * @param length number of bytes to concatenate. This must be greater than 0
     *              and less than or equal to length of second parameter.
     * @return combined byte array.
     * @throws NullPointerException if first or second byte array is null
     */
    public static byte[] concat(final byte[] first, final byte[] second, final int length) {
        Objects.requireNonNull(first);
        int l = Objects.checkIndex(length,Objects.requireNonNull(second).length + 1);
        byte[] result = new byte[first.length + l];
        System.arraycopy(Objects.requireNonNull(first,"First byte array is null"),0,result,0,first.length);
        System.arraycopy(Objects.requireNonNull(second,"Second byte array is null"),0,result,first.length,l);
        return result;
    }

    /**
     * Copies byte array and returns a new byte array copy.
     *
     * @param source of byte array to copy.
     * @return a copy of the source byte array.
     */
    public static byte[] copy(final byte[] source) {
        return Bytes.copy(source,source.length);
    }

    /**
     * Copies byte array and returns a new byte array copy.
     * <p>
     * If {@code malloc} is less than {@code source length} then {@link
     * IndexOutOfBoundsException} is thrown.
     *
     * @param source of byte array to copy.
     * @param malloc size of destination array
     * @return a copy of the source byte array.
     * @throws IndexOutOfBoundsException when {@code malloc} is invalid.
     * @throws NullPointerException when {@code source} is null.
     */
    public static byte[] copy(final byte[] source, final int malloc) {
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        if (malloc < s.length)
            throw new IndexOutOfBoundsException();
        byte[] result = new byte[malloc];
        System.arraycopy(s,0,result,0,s.length);
        return result;
    }

    /**
     * Copies a block of bytes in {@code source} to a destination specified by
     * {@code destIndex}.
     * <p>
     * A new byte array is returned with copied block of bytes, the original
     * source bytes remain unchanged.
     *
     * @param source source of bytes to be processed.
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @param destIndex destination of block
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array; {@code destIndex} cannot accommodate block.
     */
    public static byte[] copyBlock(final byte[] source, final int beginIndex, final int endIndex, final int destIndex) {
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        int fromIndex = checkBlockIndexes(beginIndex, endIndex, destIndex, s.length);
        byte[] block = Bytes.subBytes(s, fromIndex, endIndex);
        byte[] result = Bytes.copy(source);
        System.arraycopy(block, 0, result, destIndex, block.length);
        return result;
    }

    /**
     * Moves a block of bytes in {@code source} to a destination specified by
     * {@code destIndex}.
     * <p>
     * A new byte array is returned with moved bytes, the original source bytes
     * remain unchanged.
     *
     * @param source source of bytes to be processed.
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @param destIndex destination of block
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array; {@code destIndex} cannot accommodate block.
     */
    public static byte[] moveBlock(final byte[] source, final int beginIndex, final int endIndex, final int destIndex) {
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        int fromIndex = checkBlockIndexes(beginIndex, endIndex, destIndex, s.length);
        // Extract block
        byte[] block = Bytes.subBytes(s, fromIndex, endIndex);
        // Remove source block, leaving just left and right portions of either side of block
        byte[] remainder = removeBlock(s, beginIndex, endIndex);
        byte[] result = new byte[s.length];
        // Write block to destination
        System.arraycopy(block, 0, result, destIndex, block.length);
        // Write left-most of remainder
        System.arraycopy(remainder, 0, result, 0, destIndex);
        // Write right-most of remainder
        System.arraycopy(remainder, destIndex, result, destIndex + block.length, s.length - (destIndex + block.length));
        return result;
    }

    /**
     * Removes a block of bytes in {@code source}.
     * <p>
     * A new byte array is returned with removed block of bytes, the original
     * source bytes remain unchanged.
     *
     * @param source source of bytes to be processed.
     * @param beginIndex beginning of block (inclusive)
     * @param endIndex end of block (exclusive)
     * @return a new byte array is returned with moved bytes.
     * @throws IndexOutOfBoundsException when block is greater than size of
     * {@code source} array.
     */
    public static byte[] removeBlock(final byte[] source, final int beginIndex, final int endIndex) {
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        int fromIndex = checkBlockIndexes(beginIndex, endIndex, 0, s.length);
        int block = endIndex - fromIndex;
        byte[] result = new byte[source.length - block];
        System.arraycopy(s, 0, result, 0, fromIndex);
        System.arraycopy(s, endIndex, result, endIndex - block, s.length - endIndex);
        return result;
    }

    /**
     * Trims/truncates the left-most bytes from the byte array source and
     * returns a copy of the source byte array.
     *
     * @param source source of byte array to truncate.
     * @param bytes number of bytes with which to truncate on the left.
     * @return truncated byte array of source.
     */
    public static byte[] trimLeft(final byte[] source, final int bytes) {
        return trimLeft(source,bytes,source.length);
    }

    /**
     * Returns a copy of sub-bytes from the source bytes, specified by supplied
     * {@code beginIndex} and {@code endIndex -1}.
     *
     * @param source the source byte array.
     * @param beginIndex starting index
     * @param endIndex ending index -1.
     * @return a copy of sub-bytes.
     * @throws IndexOutOfBoundsException if beginIndex is negative;
     * endIndex > source length; beginIndex > endIndex.
     */
    public static byte[] subBytes(final byte[] source, final int beginIndex, final int endIndex) {
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        int fromIndex = Objects.checkFromToIndex(beginIndex,endIndex,s.length);
        byte[] result = new byte[endIndex - fromIndex];
        System.arraycopy(s,fromIndex,result,0,endIndex - fromIndex);
        return result;
    }

    /**
     * Trims/truncates the right-most bytes from the byte array source and
     * returns a copy of the source byte array.
     *
     * @param source source of byte array to truncate.
     * @param bytes number of bytes with which to truncate on the right.
     * @return truncated byte array of source.
     */
    public static byte[] trimRight(final byte[] source, final int bytes) {
        return trimRight(source,bytes,source.length);
    }

    /**
     * Converts an integer value into a byte array.
     *
     * @param value integer value to be transformed.
     * @return byte array.
     */
    public static byte[] toBytes(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value
        };
    }

    /**
     * Converts four bytes (32 bits) to an integer.
     * <p>
     * The byte array would've been created by the {@link Bytes#toBytes(int)}
     * function.
     *
     * @param bytes the byte array with encoded integer.
     * @return the integer
     */
    public static int valueOf(byte[] bytes) {
        byte[] b = Objects.requireNonNull(bytes);
        if (b.length != 4)
            throw new IllegalArgumentException("Expected 32 bit array");
        return (((bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF));
    }

    private static byte[] trimLeft(final byte[] source, final int bytes, final int length) {
        Objects.checkIndex(bytes,length);
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        byte[] result = new byte[length - bytes];
        System.arraycopy(s,bytes,result,0,length - bytes);
        return result;
    }

    private static byte[] trimRight(final byte[] source,  final int bytes, final int length) {
        Objects.checkIndex(bytes,length);
        byte[] s = Objects.requireNonNull(source,"Source byte array is null");
        byte[] result = new byte[length - bytes];
        System.arraycopy(s,0,result,0,length - bytes);
        return result;
    }

    private static int checkBlockIndexes(final int beginIndex, final int endIndex, final int destIndex, final int length) {
        int fromIndex = Objects.checkFromToIndex(beginIndex,endIndex,length);
        if (destIndex < 0 || destIndex >= length - (endIndex - beginIndex) + 1)
            throw new IndexOutOfBoundsException("Insufficient space in which to copy/move block size %d, destination %d"
                    .formatted(endIndex - beginIndex,destIndex));
        return fromIndex;
    }

    private byte[] createCapacity() {
        byte[] bytes = this.bytes;
        if (marker >= bytes.length) {
            bytes = Bytes.copy(this.bytes, this.bytes.length + DEFAULT_EXTENSION);
            return bytes;
        }
        return bytes;
    }
}
