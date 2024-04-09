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

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Base64;

public final class ByteArrayJsonAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    @Override
    public JsonElement serialize(byte[] bytes, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(Base64.getEncoder().encodeToString(bytes));
    }

    @Override
    public byte[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        String s = jsonElement.getAsString();
        return Base64.getDecoder().decode(s);
    }
}