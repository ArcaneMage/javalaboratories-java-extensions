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
package org.javalaboratories.core.cryptography.transport;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.javalaboratories.core.cryptography.CryptographyException;
import org.javalaboratories.core.cryptography.MessageSignatureException;
import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.core.util.Bytes;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Value
@EqualsAndHashCode
public class Message {

    transient PublicKey publicKey;
    transient byte[] signature;
    transient byte[] data;

    byte[] signed;

    private static final String DEFAULT_SIGNING_ALGORITHM = "RSA";

    public Message(PublicKey key, byte[] signature, byte[] data) {
        Arguments.requireNonNull("Message arguments cannot be null",data,signature,key);
        this.publicKey = key;
        this.signature = signature;
        this.data = data;
        signed = encodeSign();
    }

    public Message(byte[] signed) {
        Objects.requireNonNull(signed);
        try {
            int publicKeySz = Bytes.fromBytes(Bytes.subBytes(signed, 0, 4));
            byte[] publicKeyBytes = Bytes.subBytes(signed, 4, publicKeySz + 4);

            byte[] remainder = Bytes.trimLeft(signed, publicKeySz + 4);

            int signatureKeySz = Bytes.fromBytes(Bytes.subBytes(remainder, 0, 4));
            this.signature = Bytes.subBytes(remainder, 4, signatureKeySz + 4);

            remainder = Bytes.trimLeft(remainder, signatureKeySz + 4);
            this.data = Bytes.subBytes(remainder, 0, remainder.length);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_SIGNING_ALGORITHM);
            this.publicKey = kf.generatePublic(spec);
            this.signed = signed;
        } catch (GeneralSecurityException e) {
            throw new MessageSignatureException("Failed to decode public key", e);
        } catch(IndexOutOfBoundsException e) {
            throw new MessageSignatureException("Signed message does do not conform to signature format");
        }
    }

    public byte[] getSigned() {
        return Bytes.copy(signed);
    }

    public String getSignedAsBase64() {
        return Base64.getEncoder().encodeToString(signed);
    }

    private byte[] encodeSign() {
        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] signatureSz = Bytes.toByteArray(signature.length);
        byte[] publicKeySz = Bytes.toByteArray(publicKeyBytes.length);
        byte[] publicKeyBlock = Bytes.concat(publicKeySz,publicKeyBytes);
        byte[] signatureBlock = Bytes.concat(signatureSz,signature);
        byte[] header = Bytes.concat(publicKeyBlock,signatureBlock);
        return Bytes.concat(header,data);
    }
}
