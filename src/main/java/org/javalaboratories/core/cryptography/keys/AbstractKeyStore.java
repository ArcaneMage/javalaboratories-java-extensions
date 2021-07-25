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

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Eval;
import org.javalaboratories.core.cryptography.CryptographyException;
import org.javalaboratories.core.util.Arguments;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * A utility object to process private keys stored in KeyStores.
 * <p>
 * Example usage is as follows:
 * <pre>
 *     {@code
 *        PrivateKeyStore store = PrivateKeyStore.builder()
 *             .keyStoreStream(new FileInputStream(KEYSTORE_FILE))
 *             .storePassword("changeit")
 *             .build();
 *
 *        PrivateKey key = store.getEncryptedKey("javalaboratories-org",PRIVATE_KEY_PASSWORD);
 *     }
 * </pre>
 */
@EqualsAndHashCode
public abstract class AbstractKeyStore implements Serializable {

    private static final long serialVersionUID = 1082789795503155768L;

    private final String keyStoreType;
    private final String storePassword;

    @EqualsAndHashCode.Exclude
    private final InputStream keyStoreStream;
    @EqualsAndHashCode.Exclude
    private final Eval<KeyStore> lazyKeyStore;

    /**
     * Constructs an instance this {@link AbstractKeyStore}.
     * <p>
     * It is preferable to use the {@code builder} object to construct this
     * object. If {@code keyPassword} is null, then {@code storePassed} is
     * assumed.
     *
     * @param keyStoreStream input stream to keystore.
     * @param keyStoreType keystore type, for example "jks" (optional).
     * @param storePassword keystore password.
     */
    public AbstractKeyStore(final InputStream keyStoreStream, final String keyStoreType, final String storePassword) {
        Arguments.requireNonNull("Parameters keyStoreStream and storePassword are mandatory",keyStoreStream,
                storePassword);
        this.keyStoreStream = keyStoreStream;
        this.keyStoreType = keyStoreType == null ? KeyStore.getDefaultType() : keyStoreType;
        this.storePassword = storePassword;
        this.lazyKeyStore = Eval.later(this::initialise);
    }

    /**
     * Returns an initialised KeyStore, ready for use by derived classes.
     * @return KeyStore instance that is ready for use.
     *
     * @throws CryptographyException for keystore read failures, no such algorithm
     * or input/output failures.
     */
    protected KeyStore getKeyStore() {
        return lazyKeyStore.get();
    }

    private KeyStore initialise() {
        KeyStore result;
        try {
            result = KeyStore.getInstance(keyStoreType);
            result.load(keyStoreStream, storePassword.toCharArray());
        } catch (KeyStoreException e) {
            throw new CryptographyException("Failed to read keystore",e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographyException("No such algorithm", e);
        } catch(IOException | CertificateException e) {
            throw new CryptographyException("Input/output or certificate error", e);
        }
        return result;
    }
}
