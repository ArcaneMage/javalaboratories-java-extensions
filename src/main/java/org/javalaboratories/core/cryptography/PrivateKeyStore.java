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
package org.javalaboratories.core.cryptography;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Eval;

import java.io.IOException;
import java.io.InputStream;
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
 *             .keyAlias("javalaboratories-org")
 *             .keyPassword(PRIVATE_KEY_PASSWORD)
 *             .build();
 *
 *        PrivateKey key = store.getKey();
 *     }
 * </pre>
 */
@EqualsAndHashCode
public final class PrivateKeyStore {
    private final InputStream keyStoreStream;
    private final String storePassword;
    private final String keyAlias;
    private final String keyPassword;
    private final Eval<PrivateKey> privateKey;

    /**
     * Constructs an instance this {@link PrivateKeyStore}.
     * <p>
     * It is preferable to use the {@code builder} object to construct this
     * object.
     * @param keyStoreStream input stream to keystore.
     * @param storePassword keystore password.
     * @param keyAlias alias of private key.
     * @param keyPassword password of private key.
     */
    @Builder
    public PrivateKeyStore(final InputStream keyStoreStream,final String storePassword,final String keyAlias,
                           final String keyPassword) {
        this.keyStoreStream = keyStoreStream;
        this.storePassword = storePassword;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword == null ? storePassword : keyPassword;
        this.privateKey = Eval.later(this::readKeyStore);
    }

    /**
     * @return lazily returns PrivateKey from keystore. Note: if key has already
     * been retrieved, they cached key will be returned instead.
     */
    public PrivateKey getKey() {
        return privateKey.get();
    }

    private PrivateKey readKeyStore() {
        PrivateKey key;
        try {
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(keyStoreStream, storePassword.toCharArray());
            key = (PrivateKey) store.getKey(keyAlias,keyPassword.toCharArray());
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("Failed to read keystore",e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm", e);
        } catch(IOException | CertificateException e) {
            throw new IllegalArgumentException("Input/output or certificate error",e);
        } catch(UnrecoverableKeyException e) {
            throw new IllegalStateException("Unrecoverable key",e);
        }
        return key;
    }
}
