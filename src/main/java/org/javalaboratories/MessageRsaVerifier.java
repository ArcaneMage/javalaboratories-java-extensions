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
package org.javalaboratories;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.javalaboratories.core.cryptography.ByteCryptographyResult;
import org.javalaboratories.core.cryptography.CryptographyException;
import org.javalaboratories.core.cryptography.MessageDigestAlgorithms;
import org.javalaboratories.core.cryptography.MessageRsaAuthentication;
import org.javalaboratories.core.cryptography.transport.SignedTransitMessage;
import org.javalaboratories.core.cryptography.transport.TransitMessage;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = true)
public class MessageRsaVerifier extends MessageRsaAuthentication {

    PublicKey publicKey;

    public MessageRsaVerifier(final PublicKey key) {
        this(key,MessageDigestAlgorithms.SHA256);
    }

    public MessageRsaVerifier(final PublicKey key, final MessageDigestAlgorithms algorithm) {
        super(algorithm);
        this.publicKey = Objects.requireNonNull(key);
    }

    public TransitMessage<String> decrypt(final PrivateKey key, final SignedTransitMessage<String> message) {
        // TODO: Fix this pesky type erasure issue.
    }

    public TransitMessage<byte[]> decrypt(final PrivateKey key, final SignedTransitMessage<byte[]> message) {
        SignedTransitMessage<byte[]> m = Objects.requireNonNull(message);

        ByteCryptographyResult<PrivateKey> result = signable().decrypt(key,m.data());
        return result.getMessageHash()
            .map(h -> verify(h,m))
            .filter(s -> s)
            .map(b -> new TransitMessage<>(result.getBytes()))
            .orElseThrow(() -> new CryptographyException("Failed to verify message"));
    }

    public boolean verify(byte[] hash,SignedTransitMessage<byte[]> message) {
        PublicKey signatureKey = getSignaturePublicKey(message.publicKey());
        try {
            Signature signature = Signature.getInstance(DEFAULT_SIGNING_ALGORITHM);
            signature.initVerify(signatureKey);
            signature.update(message.signature());
            return signature.verify(hash);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to verify signature",e);
        }
    }

    private PublicKey getSignaturePublicKey(byte[] encoded) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_SIGNING_ALGORITHM);
            return kf.generatePublic(spec);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decode public key",e);
        }
    }
}
