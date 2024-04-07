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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.javalaboratories.core.cryptography.transport.SignedTransitMessage;
import org.javalaboratories.core.util.Bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

@Value
public class MessageAuthenticator {

    private static final int STREAM_BUFFER_SIZE = 4096;

    private static final String MESSAGE_NOT_SIGNABLE = "Encrypted data is not signable";
    private static final String DEFAULT_SIGNING_ALGORITHM = "SHA256withRSA";
    private static final String DEFAULT_KEY_FACTORY_ALGORITHM = "RSA";

    MessageDigestAlgorithms algorithm;
    PrivateKey privateKey;

    @Getter(AccessLevel.PRIVATE)
    @EqualsAndHashCode.Exclude
    RsaHybridCryptography signable;

    public MessageAuthenticator(final PrivateKey key) {
        this(key,MessageDigestAlgorithms.SHA256);
    }

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

    public boolean encrypt(final PublicKey key, final File source, final File ciphertext) {
        PublicKey pk = Objects.requireNonNull(key,"Expected public key");
        File file = Objects.requireNonNull(source,"Expected source file to encrypt");
        File ct = Objects.requireNonNull(ciphertext,"Expected output file object");

        FileCryptographyResult<PublicKey> result = signable.encrypt(pk,file,ct);
        byte[] signed = result.getMessageHash()
            .map(this::sign)
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));

        PublicKey signatureKey = getSignaturePublicKey();
        File tempfile = new File(STR."\{ciphertext.getAbsolutePath()}.tmp");
        try (FileInputStream isstream = new FileInputStream(ciphertext);
            FileOutputStream osstream = new FileOutputStream(tempfile)) {

            // Write signature header to file
            osstream.write(Bytes.toByteArray(signed.length));
            osstream.write(signed);
            osstream.write(Bytes.toByteArray(signatureKey.getEncoded().length));
            osstream.write(signatureKey.getEncoded());

            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int read;
            while((read = isstream.read(buffer)) != -1)
                osstream.write(buffer,0,read);

            return ciphertext.delete() && tempfile.renameTo(ciphertext);
        } catch (IOException e) {
            throw new CryptographyException("Failed to sign encrypted file",e);
        }
    }

    private PublicKey getSignaturePublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_KEY_FACTORY_ALGORITHM);
            RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
            return kf.generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(),rsaPrivateKey.getPublicExponent()));
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
