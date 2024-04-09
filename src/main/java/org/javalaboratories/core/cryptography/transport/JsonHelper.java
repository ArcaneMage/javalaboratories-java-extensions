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
package org.javalaboratories.core.cryptography.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonHelper {
    public static String messageToJson(final Message message) {
        return getCustomGson().toJson(message);
    }

    public static Message jsonToMessage(final String jsonMessage) {
        return getCustomGson().fromJson(jsonMessage, Message.class);
    }

    private static Gson getCustomGson() {
        return new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter()).create();
    }
}
