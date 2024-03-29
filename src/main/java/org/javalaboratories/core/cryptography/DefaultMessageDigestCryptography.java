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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class DefaultMessageDigestCryptography implements HashCryptography {

    private static final int BUFFER_SIZE = 512;

    @Override
    public HashCryptographyResult hash(final String string, final MessageDigestAlgorithms algorithms) {
        String s = Objects.requireNonNull(string);
        MessageDigestAlgorithms a = Objects.requireNonNull(algorithms);
        try {
            MessageDigest md = MessageDigest.getInstance(a.getAlgorithm());
            md.update(s.getBytes());
            return md::digest;
        } catch(NoSuchAlgorithmException e) {
            throw new CryptographyException("Failed to generate hash for string",e);
        }
    }

    @Override
    public HashCryptographyResult hash(final InputStream inputStream, final MessageDigestAlgorithms algorithms) {
        try (InputStream is = Objects.requireNonNull(inputStream)) {
            MessageDigest md = MessageDigest.getInstance(Objects.requireNonNull(algorithms).getAlgorithm());
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while((read = is.read(buffer)) != -1)
                md.update(buffer,0,read);
            return md::digest;
        } catch (IOException e) {
            throw new CryptographyException("Failed to generate hash for input stream",e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographyException("Failed to generate hash",e);
        }
    }

    DefaultMessageDigestCryptography() {}
}
