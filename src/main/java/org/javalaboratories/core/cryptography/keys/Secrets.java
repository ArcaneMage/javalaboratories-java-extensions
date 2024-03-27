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
package org.javalaboratories.core.cryptography.keys;

import org.javalaboratories.core.cryptography.CryptographyException;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Objects;

@Deprecated
public record Secrets(SecretKey key, IvParameterSpec ivParameterSpec) {

    private static final int FILE_BUFFER_SIZE = 1024;

    public static Secrets from(final String secrets) {
        String[] s = Objects.requireNonNull(secrets, "Expected encoded secrets to import").split(":");
        if (s.length != 2)
            throw new CryptographyException("Secrets string does not conform to exported format");
        try {
            byte[] key = Base64.getDecoder().decode(s[0]);
            byte[] iv = Base64.getDecoder().decode(s[1]);
            return new Secrets(new SecretKeySpec(key,0,key.length,"AES"),new IvParameterSpec(iv));
        } catch (IllegalArgumentException e) {
            throw new CryptographyException("Failed to decode secrets string: does not conform to exported format",e);
        }
    }

    public static Secrets fromStream(final InputStream inputStream) {
        try (InputStreamReader is = new InputStreamReader(Objects.requireNonNull(inputStream,"Expected input stream"))) {
            char[] buffer = new char[FILE_BUFFER_SIZE];
            StringBuilder strbuf = new StringBuilder();
            int length;
            while ((length = is.read(buffer)) != -1)
                strbuf.append(buffer, 0, length);
            return from(strbuf.toString());
        } catch (IOException e) {
            throw new CryptographyException("Failed to import secrets from stream",e);
        }
    }

    public static Secrets fromFile(final File file) {
        try (FileInputStream is = new FileInputStream(Objects.requireNonNull(file,"Expected file"))) {
            return fromStream(is);
        } catch (IOException e) {
            throw new CryptographyException("Failed to import secrets from stream",e);
        }
    }

    public Secrets(final SecretKey key, final IvParameterSpec ivParameterSpec) {
        this.key = Objects.requireNonNull(key);
        this.ivParameterSpec = Objects.requireNonNull(ivParameterSpec);
    }

    public String export() {
        String keyAsBase64 = Base64.getEncoder().encodeToString(this.key.getEncoded());
        String ivAsBase64 = Base64.getEncoder().encodeToString(this.ivParameterSpec.getIV());
        return STR."\{keyAsBase64}:\{ivAsBase64}";
    }

    public void exportToFile(final File file) {
        try (FileWriter writer = new FileWriter(Objects.requireNonNull(file,"Expected file object"))) {
            writer.write(export());
        } catch (IOException e) {
            throw new CryptographyException("Failed to export secrets to file",e);
        }
    }
}
