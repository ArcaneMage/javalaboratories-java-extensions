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
import org.javalaboratories.core.cryptography.json.JsonHelper;
import org.javalaboratories.core.cryptography.json.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class MessageAuthenticationTest {

    private static final String SIGNING_PRIVATE_KEY_FILE = "rsa-signing-private-key-pkcs8.pem";

    private static final String PUBLIC_KEY_FILE = "rsa-public-key.pem";
    private static final String PRIVATE_KEY_FILE = "rsa-private-key-pkcs8.pem";

    private static final String AES_UNENCRYPTED_FILE = "aes-unencrypted-file.txt";

    private static final String INVALID_FILE = "aes-encrypted-file-does-not-exist.tmp";

    private static final String TEXT = "The quick brown fox jumped over the fence and then back again, just for a laugh.";
    private static final String TEXT_BAD_SIGNATURE = "YXHUzLl+JyXp3sVDRBA3BAyD+JHG3kYTunQhE72epuFVPTFE6yymrGiox/+ZdIyhdmUs7Y" +
            "IA+UI1JOJZFM0++AeBb+EiOsZyxwZFnnSVcct8rrf9G1+Fi5lYbSIsp1Xc61yvniEwGYYYN/yH0jBGcdgdbzyryt00Ql1ZJEG/lBxTjdH4r" +
            "RLSqt6hJs1/zCGUcUfveumoMdoxIDS3ung2taJW62YnMIU8IOHYu9vfitW+kbYWXTD1uUh4K8BNfyb8PzT96oLtjGV4qBwlO+R4eg57nciP" +
            "htPoQaBeglhf1Hat7/Dju1hAErbPRy9dHBKBHbQqLoaKUtsyk0XNBgZBYw==";

    private static final String TEXT_SIGNED = "AAABJjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALXvARIORX4JcyvIqqy6uJWZFJ" +
            "0/PkOrTGFBTOXIjUr86OeJawYyo04Qr0fA4TJHrryf3nhlMPWyAj7mTUhx8BkkxvS0n0jGM/MWLrlt3FVel1GuGlKMNkV5uJE+/+NP+C/l5j" +
            "wQd3zWDICYwEBEOd9xus9CwcgjGETCQhFtSd47nQJlk9GicevKSh1WjobLCORgUhA8b94ugTazSHVzL9XCoVOOiNoKOXgAZz+qXPu+BCxbCg" +
            "3fY1YWeL8TJomCo3t38p/j/Trybtsw5dhbi/O8CNHOYkqxHTZR3sR0MC9CSCrS8W0F29kpzvDMCVmeinD9+2Kec3VB2l96wnnYDOkCAwEAAQA" +
            "AAQBdcdTMuX4nJenexUNEEDcEDIP4kcbeRhO6dCETvZ6m4VU9MUTrLKasaKjH/5l0jKF2ZSztggD5QjUk4lkUzT74B4Fv4SI6xnLHBkWedJVx" +
            "y3yut/0bX4WLmVhtIiynVdzrXK+eITAZhhg3/IfSMEZx2B1vPKvK3TRCXVkkQb+UHFON0fitEtKq3qEmzX/MIZRxR+966agx2jEgNLe6eDa1o" +
            "lbrZicwhTwg4di729+K1b6RthZdMPW5SHgrwE1/Jvw/NP3qgu2MZXioHCU75Hh6DnudyI+G0+hBoF6CWF/Udq3v8OO7WEASts9HL10cEoEdtC" +
            "ouhopS2zKTRc0GBkFjAAABAItkUeOM7jLxHxH3kBTIk64fQ+K2c/Zod9ltZu21CKf2NSveFx4sh7ujQCaGVg8+oFPnbgp9QzIhDEI8lJJdN2P" +
            "CHGTpbaRCUDVu25bSMJA1GZLadVv5DYlad4Nd6YDTJvyI25pXooW9fzOEiIGADxxyevX2vzASZEkAm4aCH7Km6H7cuhlaem7Od8aLYxfx7Omhd" +
            "6mZMB3CAqfPgzherQ55FDrt+A2X6a/DoUKahcIBm7LW5M8O+i4PsgKbyNxzpnNL+TDts/Ik4mhiOL3g0/AUW3FaxlYthr/MnCi8yHQjdu75ukt" +
            "8PUTycoP9bb8+BISQrW4fR8itxRGt1n8qX7dgfisL41K74H18nkP20cqeGk3+CTgi6es965PYPkK2PQ1RshisdatAC/S1iY21S4yU3MasjPwmAK" +
            "wk49G1wjZi0M9vqqMbftwzBRhaT9vEE4qhvBbrCJt2S4LQAGwmeDxxFuj5EZbO7BJw+Xo=";

    private static final String FILE_TEXT = "This is a test file with encrypted data -- TOP SECRET!";

    private PrivateKey signingKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private Message message;
    private RsaMessageSigner signer;
    private RsaMessageVerifier verifier;

    @BeforeEach
    public void setup() throws URISyntaxException {
        ClassLoader classLoader = MessageAuthenticationTest.class.getClassLoader();
        File signingPrivateKeyFile = Paths.get(classLoader.getResource(SIGNING_PRIVATE_KEY_FILE).toURI()).toFile();
        File publicKeyFile = Paths.get(classLoader.getResource(PUBLIC_KEY_FILE).toURI()).toFile();
        File privateKeyFile = Paths.get(classLoader.getResource(PRIVATE_KEY_FILE).toURI()).toFile();

        signingKey = RsaKeys.getPrivateKeyFrom(signingPrivateKeyFile);

        publicKey = RsaKeys.getPublicKeyFrom(publicKeyFile);
        privateKey = RsaKeys.getPrivateKeyFrom(privateKeyFile);

        message = new Message(Base64.getDecoder().decode(TEXT_SIGNED));

        signer = CryptographyFactory.getMessageSigner(signingKey);
        verifier = CryptographyFactory.getMessageVerifier();
    }

    @Test
    public void testStringEncrypt_Pass() {
        Message message =  signer.encrypt(publicKey,TEXT);

        assertNotNull(message);
        assertNotNull(message.getSigned());
    }

    @Test
    public void testFileEncrypt_Pass() throws URISyntaxException{
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File source = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File ciphertext = new File("%s.enc".formatted(source.getAbsolutePath()));

        boolean signed = signer.encrypt(publicKey,source,ciphertext);

        assertTrue(signed);
    }

    @Test
    public void testAuthenticatorState_Pass() {
        assertEquals(MessageDigestAlgorithms.SHA256, ((MessageRsaAuthentication) signer).getAlgorithm());
    }

    @Test
    public void testSignerEquality_Pass() {
        RsaMessageSigner authenticator2 = new DefaultRsaMessageSigner(signingKey);
        assertEquals(authenticator2, signer);
    }

    @Test
    public void testSignerToString_Pass() {
        assertEquals("[RsaMessageSigner,SHA256]",signer.toString());
    }

    @Test
    public void testFileEncrypt_withBadFile_Fail() {
        assertThrows(CryptographyException.class, () -> signer.encrypt(publicKey, new File(INVALID_FILE), new File(INVALID_FILE)));
    }

    @Test
    void testStringEncrypt_withBadAlgorithm_Fail() {
        try (MockedStatic<KeyFactory> keyFactory = Mockito.mockStatic(KeyFactory.class)) {
            keyFactory.when(() -> KeyFactory.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> signer.encrypt(publicKey,TEXT));
        }
    }

    @Test
    void testStringEncrypt_withBadSigningAlgorithm_Fail() {
        try (MockedStatic<Signature> signature = Mockito.mockStatic(Signature.class)) {
            signature.when(() -> Signature.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> signer.encrypt(publicKey,TEXT));
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------ Decryption Unit Tests
    //------------------------------------------------------------------------------------------------------------------

    @Test
    public void testStringDecrypt_Pass() {
        String s = verifier.decryptAsString(privateKey,TEXT_SIGNED);

        assertEquals(TEXT, s);
    }

    @Test
    void testStringDecrypt_withBadAlgorithm_Fail() {
        try (MockedStatic<Signature> signature = Mockito.mockStatic(Signature.class)) {
            signature.when(() -> Signature.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class,() -> verifier.decryptAsString(privateKey,TEXT_SIGNED));
        }
    }

    @Test
    void testStringDecrypt_withBadSignature_Fail() {
        Message spyMessage = spy(new Message(Base64.getDecoder().decode(TEXT_SIGNED)));
        doReturn(Base64.getDecoder().decode(TEXT_BAD_SIGNATURE)).when(spyMessage).getSignature();

        assertThrows(MessageSignatureException.class, () -> verifier.decryptAsString(privateKey, spyMessage));
    }

    @Test
    public void testMessageDecrypt_Pass() {
        String s = verifier.decryptAsString(privateKey,message);

        assertEquals(TEXT, s);
    }

    @Test
    public void testBytes64Decrypt_Pass() {
        byte[] b = verifier.decrypt(privateKey,message.getSignedAsBase64());

        assertNotNull(b);
        assertArrayEquals(TEXT.getBytes(), b);
    }

    @Test
    public void testBytesDecrypt_Pass() {
        byte[] b = verifier.decrypt(privateKey,message.getSigned());

        assertNotNull(b);
        assertArrayEquals(TEXT.getBytes(), b);
    }

    @Test
    public void testJsonDecrypt_Pass() {
        String json = JsonHelper.messageToJson(message);

        byte[] b = verifier.decryptJson(privateKey,json);
        assertArrayEquals(TEXT.getBytes(), b);
    }

    @Test
    public void testFileDecrypt_Pass() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File source = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File ciphertext = new File("%s.enc".formatted(source.getAbsolutePath()));

        File output = new File("%s.decrypted".formatted(source.getAbsolutePath()));
        try {
            signer.encrypt(publicKey,source,ciphertext);

            boolean decrypted = verifier.decrypt(privateKey, ciphertext, output);

            String s = Files.lines(output.toPath())
                    .collect(Collectors.joining());

            assertEquals(FILE_TEXT, s);
            assertTrue(decrypted);
        } finally {
            output.delete();
            ciphertext.delete();
        }
    }

    @Test
    public void testFileDecrypt_withBadFile_Fail() {
        assertThrows(CryptographyException.class, () -> verifier.decrypt(privateKey, new File(INVALID_FILE), new File(INVALID_FILE)));
    }

    @Test
    public void testVerifierEquality_Pass() {
        RsaMessageVerifier authenticator2 = new DefaultRsaMessageVerifier();
        assertEquals(authenticator2, verifier);
    }

    @Test
    public void testVerifierToString_Pass() {
        assertEquals("[RsaMessageVerifier,SHA256]",verifier.toString());
    }
}

