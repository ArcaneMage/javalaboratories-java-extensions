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
package org.javalaboratories.core.util.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Simple utility to read resource files using the default ClassLoader object.
 * <p>
 * A stream is used to read and process the file as opposed to alternate
 * means to enable access to resource files in the JAR libraries.
 * <p>
 * For methods that require a {@code root} parameter, provide a leading "/"
 * in the filename to tell the class loader to search from the root in the
 * package hierarchy; also ensure the parameter is set to {@code true}.
 */
public abstract class ResourceFile<T> {

    private final String filename;
    private final Class<?> clazz;
    private final boolean root;

    /**
     * Creates an instance of this {@link ResourceFile} with relative path search
     *
     * @param filename filename of the resource file.
     */
    public ResourceFile(final String filename) {
        this(ResourceFile.class, filename, false);
    }

    /**
     * Creates an instance of this {@link ResourceFile} with relative path search
     * originating from class package.
     *
     * @param clazz class package from which file is searched.
     * @param filename filename of the resource file.
     */
    public ResourceFile(final Class<?> clazz, final String filename) {
        this(clazz, filename, false);
    }

    /**
     * Creates an instance of this {@link ResourceFile}.
     *
     * @param clazz class package from which file is searched
     * @param filename filename of the resource file
     * @param root set to false for relative path search, otherwise search from
     *            root.
     */
    public ResourceFile(final Class<?> clazz, final String filename, final boolean root) {
        this.clazz = Objects.requireNonNull(clazz);
        this.filename = Objects.requireNonNull(filename);
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResourceFile<?> that)) return false;
        return Objects.equals(filename, that.filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "["+filename+"]";
    }

    /**
     * Reads the file into {@link String} object.
     * <p>
     * The resource file is derived from the current package location of this
     * class.
     *
     * @return contents of the file that has been read into a string.
     * @throws IOException I/O exception thrown in the event of read failure
     * @throws NullPointerException if no file is provided.
     */
    public T read() throws IOException {
        try (InputStream stream = createInputStream()) {
            if (stream == null)
                throw new IOException("Resource \"%s\" not found".formatted(filename));

            return readFromStream(stream);
        }
    }

    /**
     * Derived classes transform bytes that originate from the file stream into
     * the appropriate object type T.
     *
     * @param data file contents as bytes
     * @return contents of type T
     */
    protected abstract T transform(final byte[] data);

    private T readFromStream(final InputStream stream) throws IOException {
        final int BUFFER_SIZE = 2048;
        try (stream; ByteArrayOutputStream ostream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = stream.read(buffer)) != -1)
                ostream.write(buffer, 0, read);
            return transform(ostream.toByteArray());
        }
    }

    private InputStream createInputStream() {
        return root ? clazz.getResourceAsStream(filename) : clazz.getClassLoader().getResourceAsStream(filename);
    }
}
