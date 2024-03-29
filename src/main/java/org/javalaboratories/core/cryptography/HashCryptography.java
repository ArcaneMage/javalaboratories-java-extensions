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
package org.javalaboratories.core.cryptography;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public interface HashCryptography {

    HashCryptographyResult hash(final String s,final MessageDigestAlgorithms algorithms);

    HashCryptographyResult hash(final InputStream is, MessageDigestAlgorithms algorithms);

    default HashCryptographyResult hash(final String s) {
        return hash(s,MessageDigestAlgorithms.MD5);
    }

    default HashCryptographyResult hash(final File file) {
        return hash(file,MessageDigestAlgorithms.MD5);
    }

    default HashCryptographyResult hash(final File file, MessageDigestAlgorithms algorithms) {
        File f = Objects.requireNonNull(file, "Expected file object");
        try (FileInputStream fis = new FileInputStream(f)) {
            return hash(fis,algorithms);
        } catch (IOException e) {
            throw new CryptographyException("Failed to generate hash for file",e);
        }
    }
}
