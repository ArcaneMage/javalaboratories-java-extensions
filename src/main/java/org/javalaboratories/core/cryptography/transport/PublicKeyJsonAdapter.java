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
import org.javalaboratories.core.cryptography.keys.RsaKeys;

import java.lang.reflect.Type;
import java.security.PublicKey;
import java.util.Base64;

public final class PublicKeyJsonAdapter implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
    @Override
    public JsonElement serialize(PublicKey key, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    @Override
    public PublicKey deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        String s = jsonElement.getAsString();
        return RsaKeys.getPublicKeyFrom(Base64.getDecoder().decode(s));
    }
}
