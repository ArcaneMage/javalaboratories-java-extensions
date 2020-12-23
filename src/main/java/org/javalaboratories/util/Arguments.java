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

import java.util.function.Supplier;

public final class Arguments {

    public static void requireNonNull(final Object... arguments) {
        requireNonNull(NullPointerException::new,arguments);
    }

    public static void requireNonNull(final String message, final Object... arguments) {
        requireNonNull(() -> new NullPointerException(message), arguments);
    }

    public static void requireNonNull(final Supplier<RuntimeException> supplier, final Object... arguments) {
        if (arguments != null) {
            for (Object o : arguments) {
                if (o == null)
                    throw supplier.get();
            }
        }
    }
}
