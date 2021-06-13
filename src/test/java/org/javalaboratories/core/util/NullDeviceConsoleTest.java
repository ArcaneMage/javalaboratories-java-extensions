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
package org.javalaboratories.core.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class NullDeviceConsoleTest {

    private static final Logger logger = LoggerFactory.getLogger(NullDeviceConsole.class);

    @Test
    public void testDisableStdErr_Pass() {
        // Given
        NullDeviceConsole console = NullDeviceConsole.getInstance();

        // When
        console.disableStdErr();
        System.err.println("There should be no output here");
        console.enableStdErr();
        System.err.println("Woohoo! output is back");

        // Then
        assertFalse(console.isStdErrRedirected());
    }

    @Test
    public void testDisableStdOut_Pass() {
        // Given
        NullDeviceConsole console = NullDeviceConsole.getInstance();

        // When
        console.disableStdOut();
        System.out.println("There should be no output here");
        console.enableStdOut();
        System.out.println("Woohoo! output is back");

        // Then
        assertFalse(console.isStdOutRedirected());
    }

}
