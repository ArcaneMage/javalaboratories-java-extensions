package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.util.Arguments;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES Cryptography class to enable encryption and decryption of bytes and
 * streams.
 * <p>
 * The advanced encryption standard used as the basis of the encryption and
 * decryption uses the cipher block chaining (CBC) with PKCS5 padding. It is
 * advisable to use the Cryptography Factory {@link CryptographyFactory} to
 * create an instance of this class. It provides a standard interface allowing
 * for alternative implementations.
 * <p>
 * The class supports encryption and decryption of byte arrays; as well as
 * decryption and decryption of Input/Output streams, which is useful files,
 * but it only supports symmetric keys.
 *
 * Example below illustrates usage :-
 * <pre>
 *     {@code
 *          Cryptography cryptography = CryptographyFactory.getAesCryptography();
 *          byte[] result = cryptography.encrypt("Hello World".getBytes());
 *          ...
 *          System.out.println(Base64.encodeBase64String(result))
 *          ...
 *          Outputs -> "d9WYwk6LrIzw8zsNWnijsw=="
 *     }
 * </pre>
 *
 * @see CryptographyFactory
 * @see Cryptography
 * @see SymmetricCryptography
 */
public final class AesCryptography implements Cryptography, SymmetricCryptography {

    private static final String KEY = "0246810121416180";

    // The algorithm name / Cipher Block Chaining (CBC)  / data filling method
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int MAX_BUFFER_SZ = 64;

    private final AesKeyLengths keyLength;

    AesCryptography(final AesKeyLengths keyLength) {
        Objects.requireNonNull(keyLength,"AESKeyLengths?");
        this.keyLength = keyLength;
    }

    /**
     * Reads an {@link InputStream} encrypts and outputs the encrypted data to
     * the {@link OutputStream}.
     *
     * @param istream stream to be encrypted
     * @param ostream stream of encrypted data.               
     * @param key encryption secret key
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    @Override
    public void encrypt(final String key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, istream and ostream",key,istream,ostream);
        try {
            String k = normaliseKey(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8),"AES"),iv);

            OutputStream cstream = new CipherOutputStream(ostream,cipher);
            write(istream,cstream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        }
    }

    /**
     * Reads an {@link InputStream} decrypts and outputs the encrypted data to
     * the {@link OutputStream}.
     *
     * @param istream stream to be encrypted
     * @param ostream stream of encrypted data.
     * @param key encryption secret key
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    @Override
    public void decrypt(final String key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, stream",key, istream,ostream);
        try {
            String k = normaliseKey(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8),"AES"),iv);

            InputStream cstream = new CipherInputStream(istream,cipher);
            write(cstream,ostream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to decrypt stream",e);
        }
    }

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * encrypted bytes in {@code byte[]} array.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @return an array of encrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    public byte[] encrypt(final byte[] data) {
        return encrypt(KEY,data);
    }

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * encrypted bytes in {@code byte[]} array, using the symmetric {@code
     * key}.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @param key encryption secret key
     * @return an array of encrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    @Override
    public byte[] encrypt(final String key, final byte[] data) {
        Arguments.requireNonNull("Expected both bytes data to encrypt and Key objects",data,key);
        byte[] result;
        try {
            String k = normaliseKey(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8), "AES"), iv);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptographyException("Failed to encrypt content",e);
        }
        return result;
    }

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * decrypted bytes in {@code byte[]} array.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @return an array of decrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    @Override
    public byte[] decrypt(final byte[] data)  {
        return decrypt(KEY,data);
    }

    /**
     * This method reads the {@code byte[]} array and returns an array of
     * decrypted bytes in {@code byte[]} array, using the symmetric {@code
     * key}.
     *
     * @param data an array of {@code byte[]} to be encrypted.
     * @param key encryption secret key
     * @return an array of decrypted bytes.
     * @throws NullPointerException if any of the parameters is null.
     * @throws CryptographyException encapsulates cryptographic error.
     */
    @Override
    public byte[] decrypt(final String key, final byte[] data)  {
        Arguments.requireNonNull("Expected both a String to decrypt and a Key",data,key);
        byte[] decryptBytes;
        try {
            String k = normaliseKey(key);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8), "AES"),iv);
            decryptBytes = cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptographyException("Failed to decrypt content",e);
        }
        return decryptBytes;
    }

    private String normaliseKey(final String key) {
        Objects.requireNonNull(key);
        String result;
        int length = keyLength.getLength() / 8;
        if (key.length() >= length) {
            result = key.substring(0,length);
        } else {
            result = key + String.format("%" + (length - key.length()) + "s", "0").replace(" ", "0"); // Right Padding
        }
        return result;
    }

    private OutputStream write(final InputStream source, final OutputStream destination) throws IOException {
        OutputStream result;
        try (InputStream istream = source;
            OutputStream ostream = destination
        ) {
            byte[] buffer = new byte[MAX_BUFFER_SZ];
            int read;
            while ((read = istream.read(buffer, 0, MAX_BUFFER_SZ)) > -1) {
                ostream.write(buffer,0,read);
            }
            result = destination;
        }
        return result;
    }
}
