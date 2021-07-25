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

/**
 * Generally used with RSA asymmetric cryptography.
 * <p>
 * Encrypting the data with asymmetric keys will generate an additional key
 * known as the secret-key, represented by the {@code getEncryptedKey} property
 * -- this key is itself encrypted with the public key. This means it is not
 * possible to decrypt data without the original private key and the secret-key.
 * What happens in this scenario is that the secret-key is decrypted with the
 * private key and then the decrypted secret-key is used to decrypt the actual
 * data.
 *
 * @see AsymmetricCryptography
 * @see SunRsaAsymmetricCryptography
 */
public interface CryptographyResult {

    /**
     * @return The data that has undergone encryption or decryption.
     */
    byte[] getData();

    /**
     * Encryption with a the public key generates a new encrypted secret-key;
     * decryption returns the secret-key currently in use.
     *
     * @return encrypted secret-key of the data. Can only be decrypted with the
     * private key.
     */
    EncryptedAesKey getEncryptedKey();
}
