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
package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.cryptography.keys.SymmetricSecretKey;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class DefaultRsaCryptography implements RsaCryptography {

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    @Override
    public <K extends PublicKey> StringCryptographyResult<K> encrypt(K key, String string) {

        try {
            SymmetricSecretKey secretKey = SymmetricSecretKey.newInstance();
            AesCryptography aes = CryptographyFactory.getSymmetricCryptography();
            StringCryptographyResult<SymmetricSecretKey> aesResult = aes.encrypt(secretKey,string);

           Cipher cipher = Cipher.getInstance(ALGORITHM);
           cipher.init(Cipher.ENCRYPT_MODE,key);
           byte[] encryptedSecretKey = cipher.doFinal(secretKey.getEncoded());
           return null;
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to encrypt string",e);
        }
    }

    @Override
    public <K extends PublicKey, T extends OutputStream> StreamCryptographyResult<K, T> encrypt(K key, InputStream is, T cipherStream) {
        return null;
    }

    @Override
    public <K extends PrivateKey> StringCryptographyResult<K> decrypt(K key, String ciphertext) {
        return null;
    }

    @Override
    public <K extends PrivateKey, T extends OutputStream> StreamCryptographyResult<K, T> decrypt(K key, InputStream cipherStream, T os) {
        return null;
    }
}
