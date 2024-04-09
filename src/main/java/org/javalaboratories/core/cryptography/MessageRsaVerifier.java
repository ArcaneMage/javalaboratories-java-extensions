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

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.javalaboratories.core.cryptography.transport.Message;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
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

    public byte[] decrypt(final PrivateKey key, final String ciphertext) {
        Message message = new Message(Base64.getDecoder().decode(Objects.requireNonNull(ciphertext,
                "Expected ciphertext string")));
        return decrypt(key, message);
    }

    public byte[] decrypt(final PrivateKey key, final byte[] ciphertext) {
        return decrypt(key,new Message(Objects.requireNonNull(ciphertext,"Expected ciphertext bytes")));
    }

    public String decryptAsString(final PrivateKey key, final Message message) {
        return new String(decrypt(key,Objects.requireNonNull(message,"Expected ciphertext message")));
    }

    public byte[] decrypt(final PrivateKey key, final Message message) {
        PrivateKey pk = Objects.requireNonNull(key,"Expected public key for verification");
        Message m = Objects.requireNonNull(message,"Expected message");
        ByteCryptographyResult<PrivateKey> result = signable().decrypt(pk,message.getData());
        return result.getMessageHash()
            .map(h -> verify(h,m))
            .filter(s -> s)
            .map(s -> result.getBytes())
            .orElseThrow(() -> new CryptographyException("Failed to verify message"));
    }

    private boolean verify(final byte[] hash, final Message message) {
        try {
            PublicKey publicKey = message.getPublicKey();
            Signature signature = Signature.getInstance(DEFAULT_SIGNING_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(hash);
            return signature.verify(message.getSignature());
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to verify signature",e);
        }
    }
}
