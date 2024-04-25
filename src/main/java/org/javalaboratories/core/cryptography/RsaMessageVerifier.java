package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.cryptography.json.JsonHelper;
import org.javalaboratories.core.cryptography.json.Message;

import java.io.File;
import java.security.PrivateKey;
import java.util.Base64;
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
public interface RsaMessageVerifier {

    /**
     * Decrypts and verifies ciphertext {@link Message}
     *
     * @param key        the private key with witch to decrypt the message.
     * @param ciphertext the message object to be decrypted.
     * @return decrypted bytes.
     */
    byte[] decrypt(PrivateKey key, Message ciphertext);

    /**
     * Decrypts and verifies ciphertext with given {@link PrivateKey}.
     * <p>
     * The file structure has header blocks containing public key and signature
     * created by the {@link DefaultRsaMessageSigner}. These values are used to verify
     * the message integrity. If the verification fails, the file cannot be
     * decrypted.
     *
     * @param key        the private key with witch to decrypt the message.
     * @param ciphertext the ciphertext in a file to be decrypted.
     * @param output     the decrypted data.
     * @return true to indicate file has been decrypted successfully.
     */
    boolean decrypt(PrivateKey key, File ciphertext, File output);

    /**
     * Decrypts and verifies ciphertext with given {@link PrivateKey}
     * <p>
     * There are two conditions to follow for successful decryption:
     * <ol>
     *     <li>The ciphertext must be in Base64 format and signed.</li>
     *     <li>The original ciphertext must be derived from the Message.getSignedAsBase64()</li>
     * </ol>
     *
     * @param key        the private key with which to decrypt the message.
     * @param ciphertext the ciphertext in Base64 format.
     * @return decrypted string.
     */
    default String decryptAsString(PrivateKey key, String ciphertext) {
        return new String(decrypt(key, Objects.requireNonNull(ciphertext, "Expected ciphertext message string")));
    }

    /**
     * Decrypts and verifies {@link Message} ciphertext with given {@link
     * PrivateKey}
     *
     * @param key        the private key with which to decrypt the message.
     * @param ciphertext the ciphertext in Base64 format.
     * @return decrypted string.
     */
    default String decryptAsString(PrivateKey key, Message ciphertext) {
        return new String(decrypt(key, Objects.requireNonNull(ciphertext, "Expected ciphertext message")));
    }

    /**
     * Decrypts and verifies ciphertext with given {@link PrivateKey}
     * <p>
     * There are two conditions to follow for successful decryption:
     * <ol>
     *     <li>The ciphertext must be in Base64 format and signed.</li>
     *     <li>The original ciphertext must be derived from the Message.getSignedAsBase64()</li>
     * </ol>
     *
     * @param key        the private key with which to decrypt the message.
     * @param ciphertext the ciphertext in Base64 format.
     * @return decrypted bytes
     */
    default byte[] decrypt(PrivateKey key, String ciphertext) {
        Message message = new Message(Base64.getDecoder().decode(Objects.requireNonNull(ciphertext,
                "Expected ciphertext string in base64 format")));
        return decrypt(key, message);
    }

    /**
     * Decrypts and verifies ciphertext with given {@link PrivateKey}
     * <p>
     * The ciphertext bytes must follow a specific structure outlined
     * here: {@link RsaMessageVerifier}. The structure can be generated
     * by the {@link Message#getSigned()} method.
     *
     * @param key        the private key with which to decrypt the message.
     * @param ciphertext the ciphertext in bytes that adheres to the
     *                   message structure described above.
     * @return decrypted bytes
     * @see Message
     */
    default byte[] decrypt(PrivateKey key, byte[] ciphertext) {
        return decrypt(key, new Message(Objects.requireNonNull(ciphertext, "Expected signed ciphertext bytes")));
    }

    /**
     * Decrypts and verifies JSON ciphertext with given {@link PrivateKey}
     * <p>
     * The ciphertext string is a serialised JSON string, originally generated
     * by {@link JsonHelper#messageToJson(Message)}.
     *
     * @param key        the private key with which to decrypt the message.
     * @param ciphertext the ciphertext encapsulated in a JSON string that
     *                   adheres to the message structure described above.
     * @return decrypted bytes
     *
     * @see Message
     * @see JsonHelper
     */
    default byte[] decryptJson(PrivateKey key, String ciphertext) {
        return decrypt(key, JsonHelper.jsonToMessage(Objects.requireNonNull(ciphertext, "Expected signed ciphertext bytes")));
    }
}
