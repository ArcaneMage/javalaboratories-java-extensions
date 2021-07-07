package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.util.Arguments;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES Cryptography class to enable encryption and decryption of bytes and
 * streams.
 * <p>
 * The advanced encryption standard is used as the basis of the encryption and
 * decryption uses the cipher block chaining (CBC) with PKCS5 padding. It is
 * advisable to use the Cryptography Factory {@link CryptographyFactory} to
 * create an instance of this class. It provides a standard interface allowing
 * for alternative implementations.
 * <p>
 * The class supports encryption and decryption of byte arrays as well as
 * decryption and decryption of Input/Output streams, which is useful for
 * files, but it only supports symmetric keys.
 *
 * Example below illustrates usage :-
 * <pre>
 *     {@code
 *          Cryptography cryptography = CryptographyFactory.getSunCryptography();
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
public final class SunAesSymmetricCryptography extends SunCryptography implements Cryptography, SymmetricCryptography {

    private static final String KEY = "0246810121416180";

    // The algorithm name / Cipher Block Chaining (CBC)  / data filling method
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final AesKeyLengths keyLength;

    SunAesSymmetricCryptography(final AesKeyLengths keyLength) {
        Objects.requireNonNull(keyLength,"AesKeyLengths?");
        this.keyLength = keyLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encrypt(final String key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, istream and ostream",key,istream,ostream);
        try {
            String k = normaliseKey(key);
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8),"AES"),iv);

            OutputStream cstream = new CipherOutputStream(ostream,cipher);
            write(istream,cstream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrypt(final String key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, stream",key,istream,ostream);
        try {
            String k = normaliseKey(key);
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8),"AES"),iv);

            InputStream cstream = new CipherInputStream(istream,cipher);
            write(cstream,ostream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to decrypt stream",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encrypt(final byte[] data) {
        return encrypt(KEY,data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encrypt(final String key, final byte[] data) {
        Arguments.requireNonNull("Expected both bytes data to encrypt and Key objects",data,key);
        byte[] result;
        try {
            String k = normaliseKey(key);
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8), "AES"), iv);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptographyException("Failed to encrypt content",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decrypt(final byte[] data)  {
        return decrypt(KEY,data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decrypt(final String key, final byte[] data)  {
        Arguments.requireNonNull("Expected both a String to decrypt and a Key",data,key);
        byte[] result;
        try {
            String k = normaliseKey(key);
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(k.getBytes(UTF_8), "AES"),iv);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptographyException("Failed to decrypt content",e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decrypt(final SecretKey key, final byte[] data) {
        Arguments.requireNonNull("Requires key and data",key,data);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        decrypt(key,new ByteArrayInputStream(data),result);
        return result.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decrypt(final SecretKey key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, istream and ostream",key,istream,ostream);
        try {
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, key,iv);

            InputStream cstream = new CipherInputStream(istream,cipher);
            write(cstream,ostream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to decrypt stream",e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encrypt(final SecretKey key, final byte[] data) {
        Arguments.requireNonNull("Requires key and data",key,data);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        encrypt(key,new ByteArrayInputStream(data),result);
        return result.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encrypt(final SecretKey key, final InputStream istream, final OutputStream ostream) {
        Arguments.requireNonNull("Requires key, istream and ostream",key,istream,ostream);
        try {
            Cipher cipher = getCipher(AES_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            OutputStream cstream = new CipherOutputStream(ostream,cipher);
            write(istream,cstream);
        } catch(Exception e) {
            throw new CryptographyException("Failed to encrypt stream",e);
        }
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
}
