package org.javalaboratories.core.cryptography;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES Cryptography utility class to enable encryption and decryption of
 * string and streams.
 * <p>
 * At the heart of the encryption/decryption logic is the String type, but
 * unfortunately, there is a limit on the size of this object, which
 * is ~2GB. Therefore, if the <code>InputStream</code> happens to be greater
 * than this, then it may run into problems. However, this class will be
 * useful for most use cases except for large files, ie > 2GB.
 * <p>
 * Will be looking at including additional methods for large file
 * encryption.
 */
public final class AesCryptography {

    private static final String KEY = "0246810121416180";

    // The algorithm name / encryption mode / data filling method
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int MAX_BUFFER_SZ = 32;

    private AesCryptography() {}

    /**
     * Encrypts input stream data
     *
     * @param stream stream to be encrypted
     * @param key encryption secret key
     * @return encrypted resultant string from input stream.
     */
    public static String encrypt(final InputStream stream, String key) {
        return encrypt(streamToString(stream), key);
    }

    /**
     * Decrypts input stream data
     *
     * @param stream stream to be decrypted
     * @param key encryption secret key
     * @return descrypted resultant string from input stream.
     */
    public static String decrypt(final InputStream stream, String key) {
        return decrypt(streamToString(stream),key);
    }

    /**
     * Encryption
     *
     * @param s encrypted string
     * @param key key value
     * @return encrypted data.
     * @throws IllegalCryptographyStateException encryption failure.
     */
    public static String encrypt(final String s, final String key) {
        byte[] b;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(UTF_8), "AES"));
            b = cipher.doFinal(s.getBytes(UTF_8));
        } catch (Exception e) {
            throw new IllegalCryptographyStateException("Failed to encrypt content",e);
        }
        return Base64.encode(b);
    }

    /**
     * Decryption
     *
     * @param s decrypted string
     * @param key decrypted key value
     * @return decrypted data.
     * @throws IllegalCryptographyStateException decryption failure.
     */
    public static String decrypt(final String s, final String key)  {
        byte[] decryptBytes;
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(UTF_8), "AES"));
            byte[] encryptBytes = Base64.decode(s.getBytes(UTF_8));
            decryptBytes = cipher.doFinal(encryptBytes);
        } catch (Exception e) {
            throw new IllegalCryptographyStateException("Failed to decrypt content",e);
        }
        return new String(decryptBytes);
    }

    /**
     * Encrypt input stream data.
     *
     * @param stream input stream to encrypt.
     * @return encrypted stream.
     */
    public static String encrypt(final InputStream stream) {
        return encrypt(stream, KEY);
    }

    /**
     * Decrypt decrypt stream data.
     *
     * @param stream input stream to encrypt.
     * @return decrypted string.
     */
    public static String decrypt(final InputStream stream) {
        return decrypt(stream, KEY);
    }

    /**
     * Encrypt encrypt string
     *
     * @param s input string/content to encrypt.
     * @return encrypted stream.
     */
    public static String encrypt(final String s) {
        return encrypt(s, KEY);
    }

    /**
     * Decrypt encrypt string
     *
     * @param s input string/content to decrypt.
     * @return encrypted stream.
     */
    public static String decrypt(final String s) {
        return decrypt(s, KEY);
    }

    private static String streamToString(final InputStream stream) {
        String result;
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            char[] buffer = new char[MAX_BUFFER_SZ];
            int read;
            StringBuilder b = new StringBuilder();
            while ((read = reader.read(buffer, 0, MAX_BUFFER_SZ)) > -1) {
                b.append(buffer, 0, read);
            }
            result = b.toString();
        } catch (IOException e) {
            throw new IllegalCryptographyStateException("Failed to process input stream",e);
        }
        return result;
    }
}
