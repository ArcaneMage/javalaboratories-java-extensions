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
package org.javalaboratories.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ArgumentsTest {
    @Test
    public void testRequireNonNull_NullPointerException_Fail() {
        Object argument = null;
        assertThrows(NullPointerException.class,() -> Arguments.requireNonNull(argument));

        Object argument2 = null;
        assertThrows(NullPointerException.class,() -> Arguments.requireNonNull(argument,argument2));
    }

    @Test
    public void testRequireNonNull_NullPointerExceptionForAnyArgument_Fail() {
        String argument1 = "Hello World";
        String argument2 = null;
        assertThrows(NullPointerException.class,() -> Arguments.requireNonNull(argument1,argument2));
    }

    @Test
    public void testRequireNonNull_NullPointerExceptionWithMessage_Fail() {
        Object argument = null;
        Exception e = assertThrows(NullPointerException.class,() -> Arguments.requireNonNull("No argument",argument));

        assertEquals("No argument",e.getMessage());
    }

    @Test
    public void testRequireNonNull_RaiseAlternativeException_Fail() {
        Object argument = null;
        assertThrows(IllegalArgumentException.class,() -> Arguments.requireNonNull(IllegalArgumentException::new,argument));
    }

    @Test
    public void testRequireNonNull_ValidateArguments_Pass() {
        String argument = "Hello World";
        Arguments.requireNonNull(argument);

        String argument2 = "Hello World2";
        Arguments.requireNonNull(argument,argument2);
    }
}
