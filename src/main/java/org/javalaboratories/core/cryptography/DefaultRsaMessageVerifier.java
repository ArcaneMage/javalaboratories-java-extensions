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
import org.javalaboratories.core.cryptography.keys.RsaKeys;
import org.javalaboratories.core.cryptography.transport.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Objects;

/**
 * Verifies and decrypts ciphertext.
 * <p>
 * The ciphertext has to be encrypted with the {@code RsaMessageSigner} which
 * compliments this object. This encapsulates the hybrid decryption functionality
 * implemented in the {@link RsaHybridCryptography} objects but additionally
 * supplies verification capability.
 * <p>
 * The {@link DefaultRsaMessageSigner} generates header blocks in the ciphertext that
 * include the verification public key and ciphertext signature. The structure
 * ciphertext looks something like the following:
 * <pre>
 *     {@code
 *          [public-key][rsa-signature][rsa-hybrid-message]
 *     }
 * </pre>
 * This block structure is understood by this object. As the {@code signer}
 * bundles the public key in the header, there is no need for recipient to supply
 * the public key, this is automatically managed. However, the {@code private
 * key} is required to decrypt the message.
 *
 * @see DefaultRsaMessageSigner
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class DefaultRsaMessageVerifier extends MessageRsaAuthentication implements RsaMessageVerifier {

    private static final String MESSAGE_SIGNATURE_INVALID = "Invalid message signature";
    private static final int STREAM_HEADERS = 2;

    /**
     * Default constructor
     * <p>
     * Creates an instance of this class with SHA-256 as the default verification
     * algorithm.
     */
    DefaultRsaMessageVerifier() {
        this(MessageDigestAlgorithms.SHA256);
    }

    /**
     * Creates an instance of this object with a given verification algorithm.
     *
     * @param algorithm the verification algorithm.
     * @see MessageDigestAlgorithms
     */
    DefaultRsaMessageVerifier(final MessageDigestAlgorithms algorithm) {
        super(algorithm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decrypt(final PrivateKey key, final Message ciphertext) {
        PrivateKey pk = Objects.requireNonNull(key,"Expected public key for verification");
        Message m = Objects.requireNonNull(ciphertext,"Expected message");
        ByteCryptographyResult<PrivateKey> result = signable().decrypt(pk,ciphertext.getData());
        boolean validSignature = result.getMessageHash()
                .map(h -> verify(m.getPublicKey(),m.getSignature(),h))
                .filter(s -> s)
                .orElse(false);
        if (validSignature) return result.getBytes();
        else throw new MessageSignatureException(MESSAGE_SIGNATURE_INVALID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean decrypt(final PrivateKey key, final File ciphertext, File output) {
        PrivateKey pk = Objects.requireNonNull(key,"Expected private key");
        File ct = Objects.requireNonNull(ciphertext,"Expected ciphertext file");
        File o = Objects.requireNonNull(output,"Expected destination file");

        // Calculate file hash first and validate signature
        try (FileInputStream is = new FileInputStream(ct);
             OutputStream os = OutputStream.nullOutputStream()) {
            PublicKey publicKey = readPublicKeyFrom(is);
            byte[] signature = new StreamHeaderBlock(is).read();
            StreamCryptographyResult<PrivateKey,OutputStream> result = signable().decrypt(pk,is,os);
            boolean validSignature = result.getMessageHash()
                .map(h -> verify(publicKey,signature,h))
                .filter(s -> s)
                .orElse(false);
            // Now that the signature is valid, decrypt the file content.
            if (validSignature) {
                try (FileInputStream fis = new FileInputStream(ct);
                    FileOutputStream fos = new FileOutputStream(o)) {
                    new StreamHeaderBlock(fis).skip(STREAM_HEADERS);
                    signable().decrypt(pk,fis,fos);
                    return true;
                }
            } else throw new MessageSignatureException(MESSAGE_SIGNATURE_INVALID);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read ciphertext",e);
        }
    }

    @Override
    public String toString() {
        return STR."[RsaMessageVerifier,\{getAlgorithm()}]";
    }

    private PublicKey readPublicKeyFrom(final InputStream fis) throws IOException {
        StreamHeaderBlock block = new StreamHeaderBlock(fis);
        return RsaKeys.getPublicKeyFrom(block.read());
    }

    private boolean verify(final PublicKey publicKey, final byte[] signature, final byte[] hash) {
        try {
            Signature s = Signature.getInstance(DEFAULT_SIGNING_ALGORITHM);
            s.initVerify(publicKey);
            s.update(hash);
            return s.verify(signature);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to verify signature",e);
        }
    }
}
