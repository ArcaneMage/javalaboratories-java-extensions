/*
 * Copyright 2024 Kevin Henry
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
package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.util.Bytes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class reads a block data from the stream.
 * <p>
 * A block of data is defined with a block size followed by block data. MAny of
 * the cryptographic classes use this object to read signature and
 * initialization vector "headers".
 * <p>
 * It will affect the stream pointer, thus mutating the stream, often the
 * header block will always proceed the data.
 */
public class StreamHeaderBlock {

    private final InputStream stream;

    /**
     * Creates an instance of the object with the given stream.
     *
     * @param stream the input stream.
     * @throws NullPointerException if stream is null.
     */
    public StreamHeaderBlock(final InputStream stream) {
        this.stream = Objects.requireNonNull(stream,"Expected a stream");
    }

    /**
     * Reads the current header block, moving the stream pointer to the end of
     * the current block.
     *
     * @return a block of data in a byte array.
     * @throws IOException if attempted to read pass end-of-file.
     */
    public byte[] read() throws IOException {
        return read(null);
    }

    /**
     * Reads the current header block, moving the stream pointer to the end of
     * the current block.
     * <p>
     * Supply the predicate function to validate the block size. If false is
     * returned the current block will throw an {@link IOException} exception.
     * <p>
     * @param validate a function to validate block size.
     * @return a block of data in a byte array.
     * @throws IOException if attempted to read pass end-of-file.
     */
    public byte[] read(final Predicate<Integer> validate) throws IOException {
        byte[] b = new byte[4]; // 32bit number: encoded size of block
        if (stream.read(b) == -1)
            throw new IOException("Failed to read block from stream: cannot determine size");
        int blockSize = Bytes.fromBytes(b);
        if (validate != null && validate.test(blockSize))
            throw new IOException("Invalid block size encountered");
        byte[] block = new byte[blockSize];
        if (stream.read(block) == -1)
            throw new IOException("Failed to read block from stream: not enough content");
        return block;
    }

    /**
     * Calling this method will skip the current block(s).
     * <p>
     * To skip multiple blocks, it is assumed that the blocks are consecutive.
     * If this is not the case, the reading the stream will produce unpredictable
     * results.
     *
     * @param blocks number of consecutive blocks to skip.
     * @throws IOException if attempted to read pass end-of-file.
     */
    public void skip(int blocks) throws IOException {
        if (blocks < 0)
            throw new IllegalArgumentException();
        for (int i = 0; i < blocks; i++)
            read();
    }
}
