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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.cryptography.CryptographyException;

import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

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
@EqualsAndHashCode(callSuper = true)
public final class PrivateKeyStore extends AbstractKeyStore implements Serializable {

    private static final long serialVersionUID = -2784170191850769687L;

    /**
     * Constructs an instance this {@link PrivateKeyStore}.
     * <p>
     * It is preferable to use the {@code builder} object to construct this
     * object. If {@code keyPassword} is null, then {@code storePassed} is
     * assumed.
     *
     * @param keyStoreStream input stream to keystore.
     * @param keyStoreType keystore type, for example "jks" (optional).
     * @param storePassword keystore password.
     */
    @Builder
    public PrivateKeyStore(final InputStream keyStoreStream, final String keyStoreType, final String storePassword) {
        super(keyStoreStream,keyStoreType,storePassword);
    }

    /**
     * @return returns PrivateKey from keystore encapsulated in {@link Maybe}
     * object.
     * <p>
     * {@code Maybe} object will be empty if key is not found in keystore.
     * @param keyAlias alias of key
     * @param keyPassword password of key
     * @throws CryptographyException keystore processing errors (file i/o,
     * algorithm errors), an issue with original arguments.
     */
    public Maybe<PrivateKey> getKey(final String keyAlias, final String keyPassword) {
        Maybe<PrivateKey> key;
        try {
            key = Maybe.ofNullable((PrivateKey) getKeyStore().getKey(keyAlias,keyPassword.toCharArray()));
        } catch (KeyStoreException e) {
            throw new CryptographyException("Failed to read keystore",e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographyException("No such algorithm", e);
        } catch(UnrecoverableKeyException e) {
            throw new CryptographyException("Unrecoverable key",e);
        } catch (ClassCastException e) {
            throw new CryptographyException("Expected asymmetric private key",e);
        }
        return key;
    }
}
