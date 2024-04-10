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

class StreamHeaderBlock {

    private final InputStream stream;

    public StreamHeaderBlock(final InputStream stream) {
        this.stream = Objects.requireNonNull(stream,"Expected a stream");
    }

    public byte[] read() throws IOException {
        return read(null);
    }

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

    public byte[] skip(int blocks) throws IOException {
        if (blocks < 0)
            throw new IllegalArgumentException();
        byte[] result = null;
        for (int i = 0; i < blocks; i++)
            result = read();
        return result;
    }
}
