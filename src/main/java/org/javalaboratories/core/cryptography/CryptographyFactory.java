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

public final class CryptographyFactory {

    private CryptographyFactory() {}

    public static Cryptography getAesCryptography() {
        return getAesCryptography(AesKeyBitLengths.BITS_128);
    }

    public static Cryptography getAesCryptography(final AesKeyBitLengths keyLength) {
        return new AesCryptography(keyLength);
    }

    public static SymmetricCryptography getAesSymmetricCryptography() {
        return getAesSymmetricCryptography(AesKeyBitLengths.BITS_128);
    }

    public static SymmetricCryptography getAesSymmetricCryptography(final AesKeyBitLengths keyLength) {
        return new AesCryptography(keyLength);
    }

    public static Cryptography getMdCryptography() {
        return getMdCryptography(MdAlgorithms.MD5);
    }

    public static Cryptography getMdCryptography(MdAlgorithms algorithm) {
        return new MdCryptography(algorithm);
    }
}
