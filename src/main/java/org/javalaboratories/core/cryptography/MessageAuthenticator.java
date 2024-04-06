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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.javalaboratories.core.cryptography.transport.Message;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Value
public class MessageAuthenticator {

    private static final String MESSAGE_NOT_SIGNABLE = "Encrypted data is not signable";
    private static final String DEFAULT_SIGNING_ALGORITHM = "SHA256withRSA";

    MessageDigestAlgorithms algorithm;
    PrivateKey privateKey;

    @Getter(AccessLevel.PRIVATE)
    RsaHybridCryptography signable;

    public MessageAuthenticator(final PrivateKey key, MessageDigestAlgorithms algorithm) {
        this.privateKey = Objects.requireNonNull(key);
        this.algorithm = Objects.requireNonNull(algorithm);
        this.signable = CryptographyFactory.getSignableAsymmetricHybridCryptography(algorithm);
    }

    public Message<String> encrypt(final PublicKey key, final  String s) {
        ByteCryptographyResult<PublicKey> result = signable.encrypt(key,s);
        PublicKey signatureKey = getSignaturePublicKey();
        return result.getMessageHash()
            .map(this::sign)
            .map(signature -> new Message<>(result.getBytesAsBase64(), Base64.getEncoder().encodeToString(signature),
                    Base64.getEncoder().encodeToString(signatureKey.getEncoded())))
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));
    }

    private byte[] sign(final byte[] bytes) throws CryptographyException {
        try {
            Signature signature = Signature.getInstance(DEFAULT_SIGNING_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(bytes);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new CryptographyException(MESSAGE_NOT_SIGNABLE,e);
        }
    }

    private PublicKey getSignaturePublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to generate public key from private key",e);
        }
    }
}
