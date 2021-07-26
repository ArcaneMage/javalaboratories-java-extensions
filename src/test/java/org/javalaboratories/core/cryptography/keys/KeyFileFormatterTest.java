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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyFileFormatterTest {

    private static final Logger logger = LoggerFactory.getLogger(KeyFileFormatter.class);

    private static final String BASE64_ASYMMETRIC_ENCRYPTED_KEY  =
            "D3HvZI4OTiwUqymzBa6HPlTJCAYaezV7aJ3tvD1b1xnc05tf6h1Z0kskUqhjDCkNbo9lrD37yp6yBSgSdgu5l10T9bC9v4/3CPNlCIzEd/R4" +
            "/5Lihtm831T4eDLcV0GiGoJ0HwryVCPVIb8+GesVKkhB6bcaFdCgjE8UJCKG2aa+Xf1ulwzHNOg2R2Q+i3uLyYr/29RCfvQtiD3VQ3hHzh4q" +
            "3JwHF1L7uON0VaSTjRSp219CBoJG2qFcqFV5bsL025MeYapy3Gu28p7XJmwtd6KQlwXEpapEHk4ecDPq6pq2Q3a56IKpyVWoFSvGpvatpcnaZ" +
            "rH0S5MDEkJlC7kh2A==";

    private static final String BASE64_PUBLIC_KEY =
            "---- BEGIN SSH2 PUBLIC KEY ----\n" +
            "AAAAB3NzaC1yc2EAAAABJQAAAQBZ9s5nqsH6bwB1ljF3DHBRs05PpeWIZEYnYRF5\n" +
            "Ri4CTpUlZq2Ne/32qUUKgLTXpGrsbmASqdYLqow5U91slzb5Lg6zfkZsWz+CgAFV\n" +
            "YPQ5/ZbAZHKstvvES8L/RYJBCczSCuJiQbi60OpRryxP2lVQXbWeLrF/xYThW07p\n" +
            "VhyxxOeB1KocM7gfA6etI7GkQBppFuE/gW1c+efzx6GQNaacimm9k7gSdd+t2JZx\n" +
            "x6WmMmTHxDzCcGz4DCjpctG2AHFu6RcguvhX4G4Dk+Q53Hu1+9OvocJXrXxvPbdu\n" +
            "qU9YwfGHe6ZRXTpV/5XvSXvkIr3moKyXiCAzSD20yffEAXT7\n" +
            "---- END SSH2 PUBLIC KEY ----";
    @Test
    public void testWrite_Pass() {
        // Given
        KeyFileFormatter format = new KeyFileFormatter(BASE64_ASYMMETRIC_ENCRYPTED_KEY.getBytes(),true,
                "Begin RSA Encrypted Symmetric Key","End RSA Encrypted Symmetric Key");

        // Then
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        format.write(stream);
        logger.info("\n"+stream.toString());

        assertTrue(format.isBase64());
        assertEquals("Begin RSA Encrypted Symmetric Key",format.getHeader());
        assertEquals("End RSA Encrypted Symmetric Key",format.getFooter());
    }

    @Test
    public void testFrom_Pass() {
        // Given
        KeyFileFormatter format = KeyFileFormatter.from(new ByteArrayInputStream(BASE64_PUBLIC_KEY.getBytes()));

        // Then
        assertEquals("BEGIN SSH2 PUBLIC KEY",format.getHeader());
        assertEquals("END SSH2 PUBLIC KEY",format.getFooter());
        assertTrue(format.getKey().length > 0);
    }

}
