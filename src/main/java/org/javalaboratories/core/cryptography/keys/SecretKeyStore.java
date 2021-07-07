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
import org.javalaboratories.core.cryptography.CryptographyException;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.Serializable;
import java.security.*;

/**
 * A utility object to process private keys stored in KeyStores.
 * <p>
 * Example usage is as follows:
 * <pre>
 *     {@code
 *        SecretKeyStore store = SecretKeyStore.builder()
 *             .keyStoreStream(new FileInputStream(KEYSTORE_FILE))
 *             .storePassword("changeit")
 *             .build();
 *
 *        SecreteKey key = store.getKey("javalaboratories-org",SECRET_KEY_PASSWORD);
 *     }
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
public final class SecretKeyStore extends AbstractKeyStore implements Serializable {

    private static final long serialVersionUID = -6166501481797114545L;
    private static final String DEFAULT_KEYSTORE_TYPE = "jceks";

    /**
     * Constructs an instance this {@link SecretKeyStore}.
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
    public SecretKeyStore(final InputStream keyStoreStream, final String keyStoreType, final String storePassword) {
        super(keyStoreStream,keyStoreType == null ? DEFAULT_KEYSTORE_TYPE : keyStoreType,storePassword);
    }

    /**
     * @return returns SecretKey from keystore.
     *
     * @throws CryptographyException keystore processing errors (file i/o,
     * algorithm errors), an issue with original arguments.
     */
    public SecretKey getKey(final String keyAlias, final String keyPassword) {
        SecretKey key;
        try {
            key = (SecretKey) getKeyStore().getKey(keyAlias,keyPassword.toCharArray());
        } catch (KeyStoreException e) {
            throw new CryptographyException("Failed to read keystore",e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographyException("No such algorithm", e);
        } catch(UnrecoverableKeyException e) {
            throw new CryptographyException("Unrecoverable key",e);
        } catch (ClassCastException e) {
            throw new CryptographyException("Expected symmetric secret key",e);
        }
        return key;
    }
}
