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
package org.javalaboratories.core.cryptography;

import org.apache.commons.codec.binary.Hex;

import java.util.Base64;

public interface HashCryptographyResult {

    byte[] getHash();

    default String getHashAsBase64() {
        return Base64.getEncoder().encodeToString(getHash());
    }

    default String getHashAsHex() {
        return Hex.encodeHexString(getHash());
    }
}
