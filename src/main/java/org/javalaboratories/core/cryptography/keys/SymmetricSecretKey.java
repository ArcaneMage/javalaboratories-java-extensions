/*
 * Copyright 2020 Kevin Henry
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
package org.javalaboratories.core.cryptography.keys;

import org.javalaboratories.core.cryptography.CryptographyException;
import org.javalaboratories.core.cryptography.SymmetricCryptography;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * This class represents the {@code SecretKey} to be used with the
 * {@code SymmetricCryptography} objects.
 * <p>
 * All methods in the {@code SymmetricCryptography} interface utilise this
 * object to both encrypt and decrypt data. It is a
 * {@link javax.crypto.SecretKey} object but it primarily designed
 * to ease the use of the {@code SymmetricCryptography} interface. It is
 * very easy to create the {@code SecretKey} object from a {@code password} or
 * {@code password} and {@code salt} combination, and then use it to encrypt
 * or decrypt data.
 * <p>
 * {code @SymmetricSecretKey} is persistable to storage or a stream, making
 * it retrievable for later use.
 * <pre>
 *     {@code
 *          SymmetricSecretKey key = SymmetricSecretKey.from(password)
 *          SymmetricCryptography cryptography = CryptographyFactory.getSymmetricCryptography();
 *          cryptography.encrypt(key,...);
 *          ...
 *          key.write("aes-encryption.key");
 *     }
 * </pre>
 * To increase the security of the {@code key}, supply a {@code salt}, which
 * can be either auto-generated or supplied manually.
 * <pre>
 *     {@code
 *          SymmetricSecretKey key = SymmetricSecretKey.from(password, SaltMode.AUTO_GENERATE);
 *     }
 * </pre>
 */
public final class SymmetricSecretKey extends SecretKeySpec {

    @Serial
    private static final long serialVersionUID = 6879954897958082210L;

    private static final String DEFAULT_SALT = "75586321";
    private static final String KEY_ALGORITHM = "AES";
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA256";

    private static final int AUTO_PASSWORD_BYTES = 32;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 8;

    public enum SaltMode {AUTO_GENERATE, DEFAULT}

    /**
     * Creates a {@link SymmetricSecretKey} object with an auto-generated
     * password and salt.
     * <p>
     * The {@code key} is designed to be used with the {@link
     * SymmetricCryptography} interface.
     *
     * @return the resultant {@code secret key} is completely made up from
     * securely randomised password and salt.
     */
    public static SymmetricSecretKey newInstance() {
        return from(Base64.getEncoder().encodeToString(getSecureRandomBytes(AUTO_PASSWORD_BYTES)),
                SaltMode.AUTO_GENERATE);
    }

    /**
     * Creates a {@link SymmetricSecretKey} object from a given {@code password}
     * and {@code salt}.
     * <p>
     * The {@code key} is designed to be used with the {@link 
     * SymmetricCryptography} interface. 
     * <p>
     * All {@code keys} have a {@code salt} supplied; in the case of the {@code 
     * keys} created a password only, a default salt is supplied. Although, in 
     * such cases, it is encouraged to supply a good, strong password/passphrase. 
     * In fact, whether the salt is auto-generated or not, a good, strong password
     * is still recommended.
     *
     * @param password the password.
     * @param salt the salt.
     * @return {@link SymmetricSecretKey} object encapsulating the encrypted key.
     * @throws NullPointerException when password and/or salt is null.
     * @throws CryptographyException key creation failure.
     */
    public static SymmetricSecretKey from(final String password, final String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            KeySpec spec = new PBEKeySpec(Objects.requireNonNull(password).toCharArray(),salt.getBytes(),65536,KEY_LENGTH);
            return new SymmetricSecretKey(factory.generateSecret(spec).getEncoded(),KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptographyException("Failed to create secret key",e);
        }
    }

    /**
     * Creates a {@link SymmetricSecretKey} object from a given {@code password}.
     * <p>
     * The {@code key} is designed to be used with the {@link
     * SymmetricCryptography} interface. 
     * <p>
     * All {@code keys} have a {@code salt} supplied; in the case of the {@code
     * keys} created a password only, a default salt is supplied. Although, in 
     * such cases, it is encouraged to supply a good, strong password/passphrase. 
     *
     * @param password the password.
     * @return {@link SymmetricSecretKey} object encapsulating the encrypted key.
     * @throws NullPointerException when password is null.
     * @throws CryptographyException key creation failure.
     */
    public static SymmetricSecretKey from(final String password) {
        return from(password,SaltMode.DEFAULT);
    }

    /**
     * Creates a {@link SymmetricSecretKey} object from a given {@code password}
     * and {@code salt mode}.
     * <p>
     * The {@code key} is designed to be used with the {@link
     * SymmetricCryptography} interface. 
     * <p>
     * All {@code keys} have a {@code salt} supplied; in the case of the {@code
     * keys} created a password only, a default salt is supplied. Although, in 
     * such cases, it is encouraged to supply a good, strong password/passphrase. 
     * In fact, whether the salt is auto-generated or not, a good, strong password
     * is still recommended.
     *
     * @param password the password.
     * @param mode the salt mode: AUTO_GENERATE creates a random salt; DEFAULT 
     *             uses an internal salt value.
     * @return {@link SymmetricSecretKey} object encapsulating the encrypted key.
     * @throws NullPointerException when password is null.
     * @throws CryptographyException key creation failure.
     */
    public static SymmetricSecretKey from(final String password, final SaltMode mode) {
        String p = Objects.requireNonNull(password,"Expected password");
        return switch(mode) {
            case AUTO_GENERATE -> from(p,Base64.getEncoder().encodeToString(getSecureRandomBytes(SALT_BYTES)));
            case DEFAULT -> from(p,DEFAULT_SALT);
        };
    }

    /**
     * Creates a {@link SymmetricSecretKey} object from the given {@link 
     * InputStream}.
     * <p>
     * The {@code key} is designed to be used with the {@link
     * SymmetricCryptography} interface. 
     * <p>
     * The {@code key} would have been persisted to the stream with the 
     * use of the {@link SymmetricSecretKey#write(OutputStream)} method.
     *
     * @param inputStream the input stream.
     * @return {@link SymmetricSecretKey} object encapsulating the encrypted key.
     * @throws NullPointerException when the inputStream is null. 
     * @throws CryptographyException key creation failure due to stream failure
     * or invalid key encountered.
     */
    public static SymmetricSecretKey from(final InputStream inputStream) {
        try (InputStream is = Objects.requireNonNull(inputStream,"Expected input stream")) {
            return new SymmetricSecretKey(Base64.getDecoder().decode(is.readAllBytes()),KEY_ALGORITHM);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read secret key from stream",e);
        } catch (IllegalArgumentException e) {
            throw new CryptographyException("Invalid key encountered",e);
        }
    }

    /**
     * Creates a {@link SymmetricSecretKey} object from the given {@link File}.
     * <p>
     * The {@code key} is designed to be used with the {@link
     * SymmetricCryptography} interface. 
     * <p>
     * The {@code key} would have been persisted to the file with the 
     * use of the {@link SymmetricSecretKey#write(File)} method.
     *
     * @param file the file.
     * @return {@link SymmetricSecretKey} object encapsulating the encrypted key.
     * @throws NullPointerException when the file reference is null.
     * @throws CryptographyException key creation failure.
     */
    public static SymmetricSecretKey from(final File file) {
        try (FileInputStream fis = new FileInputStream(Objects.requireNonNull(file))) {
            return from(fis);
        } catch (IOException e) {
            throw new CryptographyException("Failed to read secret key from file",e);
        }
    }

    /**
     * Persists a {@link SymmetricSecretKey} object to a given {@link
     * OutputStream}.
     * <p>
     * The key can be read from the stream with the use of {@link
     * SymmetricSecretKey#from(InputStream)}.
     *
     * @param outputStream the output stream.
     * @throws NullPointerException when the {@code outputStream} reference is
     * null.
     * @throws CryptographyException key creation failure.
     */
    public void write(final OutputStream outputStream) {
        try (OutputStream os = Objects.requireNonNull(outputStream, "Expected Output stream")) {
            os.write(Base64.getEncoder().encodeToString(this.getEncoded()).getBytes());
        } catch (IOException e) {
            throw new CryptographyException("Failed to write secret key to stream",e);
        }
    }

    /**
     * Persists a {@link SymmetricSecretKey} object to a given {@link File}.
     * <p>
     * The key can be read from the stream with the use of {@link
     * SymmetricSecretKey#from(File)}.
     *
     * @param file the file.
     * @throws NullPointerException when the {@code file} reference is
     * null.
     * @throws CryptographyException key creation failure.
     */
    public void write(final File file) {
        try (FileOutputStream fos = new FileOutputStream(Objects.requireNonNull(file,"Expected file object"))) {
            write(fos);
        } catch (IOException e) {
            throw new CryptographyException("Failed to write key file",e);
        }
    }

    private static byte[] getSecureRandomBytes(int bytes) {
        SecureRandom r = new SecureRandom();
        byte[] result = new byte[bytes];
        r.nextBytes(result);
        return result;
    }

    private SymmetricSecretKey(final byte[] key, final String algorithm) {
        super(key,algorithm);
    }
}
