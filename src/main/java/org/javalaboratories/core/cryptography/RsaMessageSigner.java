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

@Value
@EqualsAndHashCode(callSuper = true)
public class RsaMessageSigner extends MessageRsaAuthentication {

    PrivateKey privateKey;

    public RsaMessageSigner(final PrivateKey key) {
        this(key,MessageDigestAlgorithms.SHA256);
    }

    public RsaMessageSigner(final PrivateKey key, MessageDigestAlgorithms algorithm) {
        super(algorithm);
        this.privateKey = Objects.requireNonNull(key);
    }

    public Message encrypt(final PublicKey publicKey, final String string) {
        PublicKey pk = Objects.requireNonNull(publicKey,"Expected public key");
        String s = Objects.requireNonNull(string,"Expected string to encrypt and sign");

        return encrypt(pk,s.getBytes());
    }

    public Message encrypt(final PublicKey key, final  byte[] bytes) {
        ByteCryptographyResult<PublicKey> result = signable().encrypt(key,bytes);
        PublicKey publicKey = getSignaturePublicKey();
        return result.getMessageHash()
            .map(this::sign)
            .map(signature -> new Message(publicKey,
                    signature,
                    result.getBytes()))
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));
    }

    public boolean encrypt(final PublicKey key, final File source, final File ciphertext) {
        PublicKey pk = Objects.requireNonNull(key,"Expected public key");
        File file = Objects.requireNonNull(source,"Expected source file to encrypt");
        File ct = Objects.requireNonNull(ciphertext,"Expected output file object");

        FileCryptographyResult<PublicKey> result = signable().encrypt(pk,file,ct);
        byte[] signature = result.getMessageHash()
            .map(this::sign)
            .orElseThrow(() -> new CryptographyException(MESSAGE_NOT_SIGNABLE));

        PublicKey publicKey = getSignaturePublicKey();
        File tempfile = new File(STR."\{ciphertext.getAbsolutePath()}.tmp");
        try (FileInputStream fis = new FileInputStream(ciphertext);
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
