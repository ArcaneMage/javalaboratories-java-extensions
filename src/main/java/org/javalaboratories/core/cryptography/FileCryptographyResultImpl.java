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

import org.javalaboratories.core.cryptography.keys.CryptographyResultImpl;

import java.io.File;
import java.security.Key;
import java.util.Objects;

public final class FileCryptographyResultImpl<K extends Key> extends CryptographyResultImpl<K>
        implements FileCryptographyResult<K> {

    private final File file;

    public FileCryptographyResultImpl(final K key, final File file) {
        this(key,null,file);
    }

    public FileCryptographyResultImpl(final K key, byte[] sessionKey, final File file) {
        super(key, sessionKey);
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public File getFile() {
        return file;
    }
}
