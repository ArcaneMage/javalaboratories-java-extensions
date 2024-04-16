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
import java.util.Objects;

/**
 * Signs and encrypts messages.
 * <p>
 * The created ciphertext has to be verified decrypted by {@link
 * RsaMessageVerifier} which compliments this class. This encapsulates the
 * hybrid encryption functionality provided in the {@link RsaHybridCryptography}
 * objects but additionally supplies signing capability.
 * <p>
 * For bytes, string and files, the header block structure created by this
 * class is the same. It is designed to package the ciphertext with its
 * verification public key, signature and encrypted data altogether. The {@link
 * RsaMessageVerifier} decrypts and verifies the ciphertext. The structure
 * of the ciphertext looks something like the following:
 * <pre>
 *     {@code
 *          [public-key][rsa-signature][rsa-hybrid-message]
 *     }
 * </pre>
 * This block structure is understood by this object. As the {@code signer}
 * bundles the public key in the header, there is no need for recipient to supply
 * the public key, this is automatically managed. However, the {@code private
 * key} is required to decrypt the message.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class DefaultRsaMessageSigner extends MessageRsaAuthentication implements RsaMessageSigner {

    private static final String MESSAGE_NOT_SIGNABLE = "Encrypted data is not signable";

    PrivateKey privateKey;

    /**
     * Default constructor
     * <p>
     * Constructs this object with the provided {@link PublicKey} which the
     * {@link DefaultRsaMessageSigner} uses to sign the ciphertext.
     * <p>
     * SHA-256 is the default signing algorithm.
     * @param key the private key
     */
    DefaultRsaMessageSigner(final PrivateKey key) {
        this(key,MessageDigestAlgorithms.SHA256);
    }

    /**
     * Constructs an instance of this class with the given {@link PrivateKey}
     * and signing {@link MessageDigestAlgorithms} algorithm.
     *
     * @param key the private key with which to sign the ciphertext.
     * @param algorithm the algorithm with which to sign the ciphertext.
     * @throws NullPointerException when parameters are null.
     */
    DefaultRsaMessageSigner(final PrivateKey key, final MessageDigestAlgorithms algorithm) {
        super(algorithm);
        this.privateKey = Objects.requireNonNull(key);
    }

    @Override
    public Message encrypt(final PublicKey publicKey, final String string) {
        PublicKey pk = Objects.requireNonNull(publicKey,"Expected public key");
        String s = Objects.requireNonNull(string,"Expected string to encrypt and sign");

        return encrypt(pk,s.getBytes());
    }

    @Override
    public Message encrypt(final PublicKey key, final byte[] bytes) {
        ByteCryptographyResult<PublicKey> result = signable().encrypt(key,bytes);
        PublicKey publicKey = getPublicKey();
        return result.getMessageHash()
            .map(this::sign)
            .map(signature -> new Message(publicKey,
                    signature,
                    result.getBytes()))
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));
    }

    @Override
    public boolean encrypt(final PublicKey key, final File source, final File ciphertext) {
        PublicKey pk = Objects.requireNonNull(key,"Expected public key");
        File file = Objects.requireNonNull(source,"Expected source file to encrypt");
        File ct = Objects.requireNonNull(ciphertext,"Expected output file object");

        SignableMessage result = signable().encrypt(pk,file,ct);
        byte[] signature = result.getMessageHash()
            .map(this::sign)
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));

        PublicKey publicKey = getPublicKey();
        File tempfile = new File(STR."\{ciphertext.getAbsolutePath()}.tmp");
        try (FileInputStream fis = new FileInputStream(ct);
            FileOutputStream fos = new FileOutputStream(tempfile)) {

            // Write signature header to file
            fos.write(Bytes.toByteArray(publicKey.getEncoded().length));
            fos.write(publicKey.getEncoded());
            fos.write(Bytes.toByteArray(signature.length));
            fos.write(signature);

            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int read;
            while((read = fis.read(buffer)) != -1)
                fos.write(buffer,0,read);

            return ciphertext.delete() && tempfile.renameTo(ciphertext);
        } catch (IOException e) {
            throw new CryptographyException("Failed to sign encrypted file",e);
        }
    }
    @Override
    public String toString() {
        return STR."[RsaMessageSigner,\{getAlgorithm()}]";
    }

    private PublicKey getPublicKey() {
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
