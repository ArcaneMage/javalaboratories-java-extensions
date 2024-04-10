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

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = true)
public class RsaMessageVerifier extends MessageRsaAuthentication {

    public static final String MESSAGE_SIGNATURE_INVALID = "Invalid message signature";
    private static final int STREAM_HEADERS = 2;

    public RsaMessageVerifier() {
        this(MessageDigestAlgorithms.SHA256);
    }

    public RsaMessageVerifier(final MessageDigestAlgorithms algorithm) {
        super(algorithm);
    }

    public String decryptAsString(final PrivateKey key, final String ciphertext) {
        return new String(decrypt(key,Objects.requireNonNull(ciphertext,"Expected ciphertext message string")));
    }

    public String decryptAsString(final PrivateKey key, final Message ciphertext) {
        return new String(decrypt(key,Objects.requireNonNull(ciphertext,"Expected ciphertext message")));
    }

    public byte[] decrypt(final PrivateKey key, final String ciphertext) {
        Message message = new Message(Base64.getDecoder().decode(Objects.requireNonNull(ciphertext,
                "Expected ciphertext string in base64 format")));
        return decrypt(key, message);
    }

    public byte[] decrypt(final PrivateKey key, final byte[] ciphertext) {
        return decrypt(key,new Message(Objects.requireNonNull(ciphertext,"Expected ciphertext bytes")));
    }

    public boolean decrypt(final PrivateKey key, final File ciphertext, File output) {
        PrivateKey pk = Objects.requireNonNull(key,"Expected private key");
        File ct = Objects.requireNonNull(ciphertext,"Expected ciphertext file");
        File o = Objects.requireNonNull(output,"Expected destination file");

        // Calculate file hash first and validate signature
        try (FileInputStream is = new FileInputStream(ct);
             OutputStream os = OutputStream.nullOutputStream()) {
            PublicKey publicKey = readSignaturePublicKeyFrom(is);
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

    private PublicKey readSignaturePublicKeyFrom(final InputStream fis) throws IOException {
        StreamHeaderBlock block = new StreamHeaderBlock(fis);
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(block.read());
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_FACTORY_ALGORITHM);
            return kf.generatePublic(spec);
        } catch (GeneralSecurityException e) {
            throw new CryptographyException("Failed to decode public key",e);
        }
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
