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
import lombok.Singular;
import lombok.Value;
import org.javalaboratories.core.cryptography.transport.SignedTransitMessage;

import java.io.File;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Value
public class MessageAuthenticator {

    private static final String MESSAGE_NOT_SIGNABLE = "Encrypted data is not signable";
    private static final String DEFAULT_SIGNING_ALGORITHM = "SHA256withRSA";
    private static final String DEFAULT_KEY_FACTORY_ALGORITHM = "RSA";

    MessageDigestAlgorithms algorithm;
    PrivateKey privateKey;

    @Getter(AccessLevel.PRIVATE)
    RsaHybridCryptography signable;

    public MessageAuthenticator(final PrivateKey key, MessageDigestAlgorithms algorithm) {
        this.privateKey = Objects.requireNonNull(key);
        this.algorithm = Objects.requireNonNull(algorithm);
        this.signable = CryptographyFactory.getSignableAsymmetricHybridCryptography(algorithm);
    }

    public SignedTransitMessage<String> encrypt(final PublicKey publicKey, final String string) {
        PublicKey pk = Objects.requireNonNull(publicKey,"Expected public key");
        String s = Objects.requireNonNull(string,"Expected string to encrypt and sign");

        SignedTransitMessage<byte[]> result = encrypt(pk,s.getBytes());
        return new SignedTransitMessage<> (
                Base64.getEncoder().encodeToString(result.data()),
                Base64.getEncoder().encodeToString(result.signature()),
                Base64.getEncoder().encodeToString(result.publicKey())
        );
    }

    public SignedTransitMessage<byte[]> encrypt(final PublicKey key, final  byte[] bytes) {
        ByteCryptographyResult<PublicKey> result = signable.encrypt(key,bytes);
        PublicKey signatureKey = getSignaturePublicKey();
        return result.getMessageHash()
            .map(this::sign)
            .map(signed -> new SignedTransitMessage<>(result.getBytes(),
                    signed,
                    signatureKey.getEncoded()))
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));
    }

    public void encrypt(final PublicKey key, File source, File ciphertext) {

    }

    private PublicKey getSignaturePublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_FACTORY_ALGORITHM);
            return kf.generatePublic(new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to generate public key from private key",e);
        }
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
}
