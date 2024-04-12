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
import org.javalaboratories.core.cryptography.MessageSignatureException;
import org.javalaboratories.core.cryptography.StreamHeaderBlock;
import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.core.util.Bytes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * Message value object represents encapsulated encrypted data, ready for
 * serialisation.
 * <p>
 * Class has the ability to decode header blocks as well as encoding them. The
 * behind is that verification public key, signature and data are all packaged
 * together ready for serialisation; it has ability to decode signed blocks.
 * The structure of the messages is as follows:
 * <pre>
 *     {@code
 *          [public-key][rsa-signature][rsa-hybrid-message]
 *     }
 * </pre>
 */
@Value
@EqualsAndHashCode
public class Message {

    transient PublicKey publicKey;
    transient byte[] signature;
    transient byte[] data;

    byte[] signed;

    private static final String DEFAULT_SIGNING_ALGORITHM = "RSA";
    private static final int STREAM_BUFFER_SIZE = 4096;

    /**
     * Creates an instance of this value object with the given public key,
     * signature and RSA/AES encrypted data.
     *
     * @param key the public key
     * @param signature the message signature
     * @param data encrypted data, RSA/AES hybrid encrypted data.
     */
    public Message(PublicKey key, byte[] signature, byte[] data) {
        Arguments.requireNonNull("Message arguments cannot be null",data,signature,key);
        this.publicKey = key;
        this.signature = signature;
        this.data = data;
        signed = encodeSign();
    }

    /**
     * Creates an instance of this value object with the given signed, encrypted
     * data.
     * <p>
     * The signed data must confirm the proprietary format describe here: {@link
     * Message}. It would've originated from the {@link Message#getSigned()}}
     * method of the message sender. Primarily, this method is used on the
     * recipient's side, i.e. within the {@code RsaMessageVerifier}.
     *
     * @param signed the signed data
     *
     * @see Message#getSigned()
     * @see Message#getSignedAsBase64()
     */
    public Message(byte[] signed) {
        Objects.requireNonNull(signed);
        try (ByteArrayInputStream is = new ByteArrayInputStream(signed)) {
            StreamHeaderBlock block = new StreamHeaderBlock(is);
            byte[] publicKeyBytes = block.read();
            signature = block.read();
            final byte[] b = new byte[STREAM_BUFFER_SIZE];
            byte[] tempbuf = new byte[0];
            int read;
            while ((read = is.read(b)) != -1)
                tempbuf = Bytes.concat(tempbuf, b, read);
            data = tempbuf;

            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(DEFAULT_SIGNING_ALGORITHM);
            this.publicKey = kf.generatePublic(spec);
            this.signed = signed;
        } catch (GeneralSecurityException e) {
            throw new MessageSignatureException("Failed to decode public key", e);
        } catch (IOException e) {
            throw new MessageSignatureException("Signed message does do not conform to signature format");
        }
    }

    /**
     * Returns entire message structure encoding public key, signature and
     * message block in byte form.
     * <p>
     * The message structure is illustrated below:
     * <pre>
     *     {@code
     *          [public-key][rsa-signature][rsa-hybrid-message]
     *     }
     * </pre>
     * A copy is returned to maintain immutability of this object.
     *
     * @return a copy of the message structure in bytes array.
     */
    public byte[] getSigned() {
        return Bytes.copy(signed);
    }

    /**
     * Returns entire message structure encoding public key, signature and
     * message block as string in Base64 format.
     * <p>
     * The message structure is illustrated below:
     * <pre>
     *     {@code
     *          [public-key][rsa-signature][rsa-hybrid-message]
     *     }
     * </pre>
     *
     * @return a copy of the message structure in Base64 format.
     */
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
