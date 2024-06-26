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
 * Supported message digest algorithms.
 * <p>
 * Note that MD5 is provided for backward compatability, and therefore it is
 * not recommended for use. Instead, consider the use of the SHA algorithms
 * as they are much more secure.
 */
public enum MessageDigestAlgorithms {
    MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"),SHA384("SHA-384"),SHA512("SHA-512");

    private final String algorithm;

    MessageDigestAlgorithms(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }
}
