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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Objects;

/**
 * Bytes class containing useful byte array operations.
 */
public final class Bytes {

    /**
     * Concatenates first and second byte arrays and returns a new combined
     * byte array.
     *
     * @param first array of bytes
     * @param second array of bytes
     * @return combined byte array.
     */
    public static byte[] concat(final byte[] first, final byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first,0,result,0,first.length);
        System.arraycopy(second,0,result,first.length,second.length);
        return result;
    }

    /**
     * Copies byte array and returns a new byte array copy.
     *
     * @param source of byte array to copy.
     * @return a copy of the source byte array.
     */
    public static byte[] copy(final byte[] source) {
        byte[] result = new byte[source.length];
        System.arraycopy(source,0,result,0,source.length);
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
    public static byte[] trimLeft(final byte[] source, int bytes) {
        byte[] result = new byte[source.length - bytes];
        System.arraycopy(source,bytes,result,0,source.length - bytes);
        String s = "Hello";
        return result;
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
        if ( beginIndex < 0 || endIndex > source.length || beginIndex > endIndex)
            throw new IndexOutOfBoundsException();
        byte[] result = new byte[endIndex - beginIndex];
        System.arraycopy(source,beginIndex,result,0,endIndex - beginIndex);
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
    public static byte[] trimRight(final byte[] source, int bytes) {
        byte[] result = new byte[source.length - bytes];
        System.arraycopy(source,0,result,0,source.length - bytes);
        return result;
    }

    /**
     * Converts an integer value into a byte array.
     *
     * @param value integer value to be transformed.
     * @return byte array.
     */
    public static byte[] toByteArray(int value) {
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
     * The byte array would've been created by the {@link Bytes#toByteArray(int)}
     * function.
     *
     * @param bytes the byte array with encoded integer.
     * @return the integer
     */
    public static int fromBytes(byte[] bytes) {
        byte[] b = Objects.requireNonNull(bytes);
        if (b.length != 4)
            throw new IllegalArgumentException("Expected 32 bit array");
        return (((bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF));
    }

    private Bytes() {}
}
