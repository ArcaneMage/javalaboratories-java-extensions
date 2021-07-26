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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Rfc4716KeyFormatterTest {

    private static final String BASE64_ASYMMETRIC_ENCRYPTED_KEY  = "D3HvZI4OTiwUqymzBa6HPlTJCAYaezV7aJ3tvD1b1xnc05tf6h1Z0kskUqhjDCkN" +
            "bo9lrD37yp6yBSgSdgu5l10T9bC9v4/3CPNlCIzEd/R4/5Lihtm831T4eDLcV0GiGoJ0HwryVCPVIb8+GesVKkhB6bcaFdCgjE8UJCKG2aa+Xf1ulwzHNOg" +
            "2R2Q+i3uLyYr/29RCfvQtiD3VQ3hHzh4q3JwHF1L7uON0VaSTjRSp219CBoJG2qFcqFV5bsL025MeYapy3Gu28p7XJmwtd6KQlwXEpapEHk4ecDPq6pq2Q3" +
            "a56IKpyVWoFSvGpvatpcnaZrH0S5MDEkJlC7kh2A==";

    @Test
    public void testWrite_Pass() {
        // Given
        Rfc4716KeyFormatter format = new Rfc4716KeyFormatter(BASE64_ASYMMETRIC_ENCRYPTED_KEY.getBytes(),true,
                "Begin RSA Encrypted Symmetric Key","End RSA Encrypted Symmetric Key");

        assertTrue(format.isBase64());
        assertEquals("Begin RSA Encrypted Symmetric Key",format.getHeader());
        assertEquals("End RSA Encrypted Symmetric Key",format.getFooter());
        format.write(System.out);
    }

}
