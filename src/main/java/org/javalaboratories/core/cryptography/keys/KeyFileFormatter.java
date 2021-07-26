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

import lombok.Value;
import org.apache.commons.codec.binary.Base64;
import org.javalaboratories.core.cryptography.CryptographyException;

import java.io.*;
import java.util.Objects;

@Value
public class KeyFileFormatter {
    private static final String BEGIN_MARKER="BEGIN";
    private static final String END_MARKER="END";

    byte[] key;
    String header, footer;
    boolean base64;

    private static final int DEFAULT_BUFFER_SZ = 64;

    public static KeyFileFormatter from(final InputStream stream) {
        Objects.requireNonNull(stream);
        String header=null, footer=null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            StringBuilder keyBuffer = new StringBuilder();
            int l = 0;
            while ((line = reader.readLine()) != null) {
                if (line.toUpperCase().contains(BEGIN_MARKER) && l++ == 0) {
                    header = line.replace("-","").trim();
                } else {
                    if (line.toUpperCase().contains(END_MARKER)) {
                        footer = line.replace("-","").trim();
                    } else {
                        keyBuffer.append(line.replace("\n",""));
                    }
                }
            }
            byte[] key = Base64.decodeBase64(keyBuffer.toString());
            return new KeyFileFormatter(key,false,header,footer);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read input stream",e);
        }
    }

    public KeyFileFormatter(final byte[] key) {
        this(key,false);
    }

    public KeyFileFormatter(final byte[] key, boolean base64) {
        this(key,base64,null,null);
    }

    public KeyFileFormatter(final byte[] key, boolean base64, String header, String footer) {
        Objects.requireNonNull(key);
        this.key = key;
        this.base64 = base64;
        this.header = header;
        this.footer = footer;
    }

    public void write(final OutputStream stream) {
        Objects.requireNonNull(stream);
        try (InputStream istream = createInputStream();
            OutputStream ostream = stream) {
            if (header != null)
                ostream.write(String.format("---- %s ----\n", header.toUpperCase()).getBytes());
            byte[] buffer = new byte[DEFAULT_BUFFER_SZ];
            int read;
            while ((read = istream.read(buffer,0,DEFAULT_BUFFER_SZ)) != -1) {
                ostream.write(buffer,0,read);
                ostream.write('\n');
            }
            if (footer != null)
                ostream.write(String.format("---- %s ----\n", footer.toUpperCase()).getBytes());
        } catch(IOException e) {
            throw new CryptographyException("Failed to write key to output stream",e);
        }
    }

    private InputStream createInputStream() {
        return base64 ? new ByteArrayInputStream(key) : new ByteArrayInputStream(Base64.encodeBase64(key));
    }
}
