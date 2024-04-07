/*
 * Copyright 2024 Kevin Henry
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

import org.javalaboratories.core.cryptography.keys.RsaKeys;
import org.javalaboratories.core.cryptography.transport.SignedTransitMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

public class MessageRsaSignerTest {

    private static final String SIGNING_PRIVATE_KEY_FILE = "rsa-signing-private-key-pkcs8.pem";
    private static final String PUBLIC_KEY_FILE = "rsa-public-key.pem";

    private static final String AES_UNENCRYPTED_FILE = "aes-unencrypted-file.txt";

    private static final String TEXT = "The quick brown fox jumped over the fence and then back again, just for a laugh.";
    private static final String TEXT_SIGNATURE = "XXHUzLl+JyXp3sVDRBA3BAyD+JHG3kYTunQhE72epuFVPTFE6yymrGiox/+ZdIyhdmUs7Y" +
            "IA+UI1JOJZFM0++AeBb+EiOsZyxwZFnnSVcct8rrf9G1+Fi5lYbSIsp1Xc61yvniEwGYYYN/yH0jBGcdgdbzyryt00Ql1ZJEG/lBxTjdH4r" +
            "RLSqt6hJs1/zCGUcUfveumoMdoxIDS3ung2taJW62YnMIU8IOHYu9vfitW+kbYWXTD1uUh4K8BNfyb8PzT96oLtjGV4qBwlO+R4eg57nciP" +
            "htPoQaBeglhf1Hat7/Dju1hAErbPRy9dHBKBHbQqLoaKUtsyk0XNBgZBYw==";
    private static final String TEXT_SIGNATURE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAte8BEg5FfglzK8i" +
            "qrLq4lZkUnT8+Q6tMYUFM5ciNSvzo54lrBjKjThCvR8DhMkeuvJ/eeGUw9bICPuZNSHHwGSTG9LSfSMYz8xYuuW3cVV6XUa4aUow2RXm4kT" +
            "7/40/4L+XmPBB3fNYMgJjAQEQ533G6z0LByCMYRMJCEW1J3judAmWT0aJx68pKHVaOhssI5GBSEDxv3i6BNrNIdXMv1cKhU46I2go5eABnP" +
            "6pc+74ELFsKDd9jVhZ4vxMmiYKje3fyn+P9OvJu2zDl2FuL87wI0c5iSrEdNlHexHQwL0JIKtLxbQXb2SnO8MwJWZ6KcP37Yp5zdUHaX3rC" +
            "edgM6QIDAQAB";
    private static final String FILE_TEXT = "This is a test file with encrypted data -- TOP SECRET!";

    private PrivateKey signingKey;
    private PublicKey publicKey;

    private MessageRsaSigner signer;

    @BeforeEach
    public void setup() throws URISyntaxException {
        ClassLoader classLoader = MessageRsaSignerTest.class.getClassLoader();
        File privateKeyfile = Paths.get(classLoader.getResource(SIGNING_PRIVATE_KEY_FILE).toURI()).toFile();
        File publicKeyfile = Paths.get(classLoader.getResource(PUBLIC_KEY_FILE).toURI()).toFile();

        signingKey = RsaKeys.getPrivateKeyFrom(privateKeyfile);
        publicKey = RsaKeys.getPublicKeyFrom(publicKeyfile);

        signer = new MessageRsaSigner(signingKey);
    }

    @Test
    public void testStringEncrypt_Pass() {
       SignedTransitMessage<String> signedMessage =  signer.encrypt(publicKey,TEXT);

       assertNotNull(signedMessage.data());
       assertEquals(signedMessage.signature(),TEXT_SIGNATURE);
       assertEquals(signedMessage.publicKey(),TEXT_SIGNATURE_PUBLIC_KEY);
    }

    @Test
    public void testFileEncrypt_Pass() throws URISyntaxException{
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File source = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File ciphertext = new File(STR."\{source.getAbsolutePath()}.enc");

        boolean signed = signer.encrypt(publicKey,source,ciphertext);

        assertTrue(signed);
    }

    @Test
    public void testAuthenticatorState_Pass() {
        assertEquals(MessageDigestAlgorithms.SHA256, signer.getAlgorithm());
        assertEquals(signingKey, signer.getPrivateKey());
    }

    @Test
    public void testAuthenticatorEquality_Pass() {
        MessageRsaSigner authenticator2 = new MessageRsaSigner(signingKey);
        assertEquals(authenticator2, signer);
    }
}
