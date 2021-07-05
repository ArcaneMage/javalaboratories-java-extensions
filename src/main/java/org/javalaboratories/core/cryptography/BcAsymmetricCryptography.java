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

import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;
import org.javalaboratories.core.util.Arguments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class BcAsymmetricCryptography implements AsymmetricCryptography {

    private static final String BOUNCY_CASTLE_PROVIDER = "BC";

    BcAsymmetricCryptography() {
        if (Arrays.stream(Security.getProviders())
                .noneMatch(p -> p.getName().equals("BC"))) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Override
    public byte[] decrypt(PrivateKey key, byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] encrypt(Certificate certificate, byte[] data) {
        Arguments.requireNonNull("certificate, data?",certificate,data);
        byte[] result;
        try {
            CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();
            JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator((X509Certificate) certificate);
            cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
            CMSTypedData msg = new CMSProcessableByteArray(data);
            OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                    .setProvider(BOUNCY_CASTLE_PROVIDER)
                    .build();
            CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
            result = cmsEnvelopedData.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new CryptographyException("Certificate encoding problem",e);
        } catch (CMSException e) {
            throw new CryptographyException("Failed to create encryptor", e);
        } catch (IOException e) {
            throw new CryptographyException("Byte encoding failure",e);
        } catch (ClassCastException e) {
            throw new CryptographyException("Requires X509 certificate type", e);
        }
        return result;
    }

    @Override
    public void decrypt(PrivateKey key, InputStream istream, OutputStream ostream) {

    }

    @Override
    public void encrypt(Certificate certificate, InputStream istream, OutputStream ostream) {

    }
}
