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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

public final class SymmetricSecretKey extends SecretKeySpec {

    @Serial
    private static final long serialVersionUID = 6879954897958082210L;

    private static final String DEFAULT_SALT = "75586321";
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 8;

    public enum SaltMode {AUTO_GENERATE, NO_SALT}

    public static SymmetricSecretKey from(final String password, final String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            KeySpec spec = new PBEKeySpec(Objects.requireNonNull(password).toCharArray(),salt.getBytes(),65536,256);
            return new SymmetricSecretKey(factory.generateSecret(spec).getEncoded(),"AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptographyException("Failed to creat secret key",e);
        }
    }

    public static SymmetricSecretKey from(final String password) {
        return from(password,SaltMode.NO_SALT);
    }

    public static SymmetricSecretKey from(final String password, final SaltMode mode) {
        String p = Objects.requireNonNull(password,"Expected password");
        return switch(mode) {
            case AUTO_GENERATE -> {
                SecureRandom r = new SecureRandom();
                byte[] bytes = new byte[SALT_BYTES];
                r.nextBytes(bytes);
                yield from(p, Base64.getEncoder().encodeToString(bytes));
            }
            case NO_SALT -> from(p,DEFAULT_SALT);
        };
    }

    public static SymmetricSecretKey from(final InputStream is) {
        try (ObjectInputStream ois = new ObjectInputStream(Objects.requireNonNull(is))) {
            return (SymmetricSecretKey) ois.readObject();
        } catch (IOException e) {
            throw new CryptographyException("Failed to read secret key from stream",e);
        } catch (ClassNotFoundException e) {
            throw new CryptographyException("Failed to read secret key from stream: this is not a secret key",e);
        }
    }

    public void write(final OutputStream os) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Objects.requireNonNull(os, "Expected Output stream"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new CryptographyException("Failed to write secret key to stream",e);
        }
    }

    public void write(final File file) {
        try (FileOutputStream fos = new FileOutputStream(Objects.requireNonNull(file,"Expected file object"))) {
            write(fos);
        } catch (IOException e) {
            throw new CryptographyException("Failed to write key file",e);
        }
    }

    private SymmetricSecretKey(final byte[] key, final String algorithm) {
        super(key,algorithm);
    }
}
