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

import org.javalaboratories.core.cryptography.keys.SymmetricKey;

import java.io.File;
import java.security.Key;

/**Ï
 * {@code CryptographyResult} object is returned from performing cryptographic
 * operations with the {@link AesCryptography} object.
 * <p>
 * It encapsulates the {@link SymmetricKey} and {@code File} that are
 * associated with the {@code cipher text}.
 * <p>
 * This object is returned from performing cryptographic operations with {@code
 * files}
 */
public interface FileCryptographyResult<K extends Key> extends CryptographyResult<K>, SessionMessage, SignableMessage {

    /**
     * @return file object to the decrypted/encrypted cipher text, depending on
     * {@link AesCryptography} file operation.
     */
    File getFile();
}
