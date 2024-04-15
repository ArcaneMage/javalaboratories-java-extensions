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

import java.security.PublicKey;
import java.util.Objects;

/**
 * A utility class that provides methods to transform JSON objects to and from
 * {@link Message}.
 */
public final class JsonHelper {

    /**
     * Serializes Message objects into JSON string form.
     *
     * @param message the message object.
     * @return A JSON string encapsulating the message.
     * @throws NullPointerException if message reference is null.
     */
    public static String messageToJson(final Message message) {
        return getCustomGson().toJson(Objects.requireNonNull(message));
    }

    /**
     * Deserializes JSON message into a Message object.
     *
     * @param jsonMessage the JSON string message object.
     * @return Message object encapsulating the message.
     * @throws NullPointerException if message reference is null.
     */
    public static Message jsonToMessage(final String jsonMessage) {
        return getCustomGson().fromJson(Objects.requireNonNull(jsonMessage), Message.class);
    }

    private static Gson getCustomGson() {
        return new GsonBuilder()
                .registerTypeAdapter(byte[].class, new ByteArrayJsonAdapter())
                .registerTypeHierarchyAdapter(PublicKey.class,new PublicKeyJsonAdapter())
                .create();
    }
}
