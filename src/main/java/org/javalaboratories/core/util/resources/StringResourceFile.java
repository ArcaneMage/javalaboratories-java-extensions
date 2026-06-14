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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Simple utility to read resource files using the default ClassLoader object.
 * <p>
 * A stream is used to read and process the file as opposed to alternate
 * means to enable access to resource files in the JAR libraries.
 * <p>
 * For methods that require a {@code root} parameter, provide a leading "/"
 * in the filename to tell the class loader to search from the root in the
 * package hierarchy; also ensure the parameter is set to {@code true}.
 * <p>
 * The class is design to read strings in resource files.
 */
public class StringResourceFile extends ResourceFile<String> {

    /**
     * Creates an instance of this {@link StringResourceFile} with relative path search
     *
     * @param filename filename of the resource file.
     */
    public StringResourceFile(final String filename) {
        super(filename);
    }

    /**
     * Creates an instance of this {@link StringResourceFile}.
     *
     * @param clazz class package from which file is searched
     * @param filename filename of the resource file
     * @param root set to false for relative path search, otherwise search from
     *            root.
     */
    public StringResourceFile(Class<?> clazz, String filename, boolean root) {
        super(clazz, filename, root);
    }

    /**
     * Creates an instance of this {@link StringResourceFile} with relative path search
     * originating from class package.
     *
     * @param clazz class package from which file is searched.
     * @param filename filename of the resource file.
     */
    public  StringResourceFile(final Class<?> clazz, final String filename) {
        super(clazz, filename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String transform(final byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Convenience static method to read a string from the {@link
     * StringResourceFile} with relative path search.
     *
     * @param filename filename of the resource file.
     * @return contents as a string
     * @throws IOException I/O exception thrown in the event of read failure.
     */
    public static String read(final String filename) throws IOException {
        return new StringResourceFile(filename).read();
    }
}
