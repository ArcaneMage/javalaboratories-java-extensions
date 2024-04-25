package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.cryptography.json.Message;

import java.io.File;
import java.security.PublicKey;

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
public interface RsaMessageSigner {
    /**
     * Encrypts the string with the given recipient's {@link PublicKey}.
     *
     * @param publicKey recipient's public key.
     * @param string    the string data to encrypt.
     * @return a {@link Message} object encapsulating encrypted data.
     */
    Message encrypt(PublicKey publicKey, String string);

    /**
     * Encrypts the string with the given recipient's {@link PublicKey}.
     *
     * @param key   the recipient's public key.
     * @param bytes the data to be encrypted.
     * @return a {@link Message} object encapsulating encrypted data.
     */
    Message encrypt(PublicKey key, byte[] bytes);

    /**
     * Encrypts {@code source} datafile and generates {@code ciphertext} file with
     * given recipient's {@link PublicKey}.
     *
     * @param key        recipient's private key.
     * @param source     source file to be encrypted.
     * @param ciphertext ciphertext datafile containing encrypted data.
     * @return true to indicate encryption was successful.
     */
    boolean encrypt(PublicKey key, File source, File ciphertext);
}
