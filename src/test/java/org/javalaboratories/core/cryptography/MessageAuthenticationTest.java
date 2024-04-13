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
import org.javalaboratories.core.cryptography.transport.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MessageAuthenticationTest {

    private static final String SIGNING_PRIVATE_KEY_FILE = "rsa-signing-private-key-pkcs8.pem";

    private static final String PUBLIC_KEY_FILE = "rsa-public-key.pem";
    private static final String PRIVATE_KEY_FILE = "rsa-private-key-pkcs8.pem";

    private static final String AES_UNENCRYPTED_FILE = "aes-unencrypted-file.txt";

    private static final String TEXT = "The quick brown fox jumped over the fence and then back again, just for a laugh.";
    private static final String TEXT_SIGNATURE = "XXHUzLl+JyXp3sVDRBA3BAyD+JHG3kYTunQhE72epuFVPTFE6yymrGiox/+ZdIyhdmUs7Y" +
            "IA+UI1JOJZFM0++AeBb+EiOsZyxwZFnnSVcct8rrf9G1+Fi5lYbSIsp1Xc61yvniEwGYYYN/yH0jBGcdgdbzyryt00Ql1ZJEG/lBxTjdH4r" +
            "RLSqt6hJs1/zCGUcUfveumoMdoxIDS3ung2taJW62YnMIU8IOHYu9vfitW+kbYWXTD1uUh4K8BNfyb8PzT96oLtjGV4qBwlO+R4eg57nciP" +
            "htPoQaBeglhf1Hat7/Dju1hAErbPRy9dHBKBHbQqLoaKUtsyk0XNBgZBYw==";

    private static final String TEXT_SIGNED = "AAABJjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALXvARIORX4JcyvIqqy6uJWZF" +
            "J0/PkOrTGFBTOXIjUr86OeJawYyo04Qr0fA4TJHrryf3nhlMPWyAj7mTUhx8BkkxvS0n0jGM/MWLrlt3FVel1GuGlKMNkV5uJE+/+NP+C/l" +
            "5jwQd3zWDICYwEBEOd9xus9CwcgjGETCQhFtSd47nQJlk9GicevKSh1WjobLCORgUhA8b94ugTazSHVzL9XCoVOOiNoKOXgAZz+qXPu+BCx" +
            "bCg3fY1YWeL8TJomCo3t38p/j/Trybtsw5dhbi/O8CNHOYkqxHTZR3sR0MC9CSCrS8W0F29kpzvDMCVmeinD9+2Kec3VB2l96wnnYDOkCAw" +
            "EAAQAAAQBdcdTMuX4nJenexUNEEDcEDIP4kcbeRhO6dCETvZ6m4VU9MUTrLKasaKjH/5l0jKF2ZSztggD5QjUk4lkUzT74B4Fv4SI6xnLHB" +
            "kWedJVxy3yut/0bX4WLmVhtIiynVdzrXK+eITAZhhg3/IfSMEZx2B1vPKvK3TRCXVkkQb+UHFON0fitEtKq3qEmzX/MIZRxR+966agx2jEg" +
            "NLe6eDa1olbrZicwhTwg4di729+K1b6RthZdMPW5SHgrwE1/Jvw/NP3qgu2MZXioHCU75Hh6DnudyI+G0+hBoF6CWF/Udq3v8OO7WEASts9" +
            "HL10cEoEdtCouhopS2zKTRc0GBkFjAAABAC1LTS3xvi4qVmdL4xNBBeGLvWzmB/V8QC1j3m4pBRd9k2+8rNaa5Hre2FH4mGJJQ067XMkWDG" +
            "LCiLSh6aNSw/qEa+c5xDHUah5QBO36fTvIb4rcn68zFtz4LasA2Eu8MSkPa95MDvuWwSPucc+WKUJs6NiExo8CCcUkjXcgu+5XH5JALUJ97" +
            "3R1xJH6Gmv7hlB7rF0nuXIWHuFj1hwElVTJJSEeAlzv5zqOxmazUmla2UbgbW/EJu/MgKuehAqubz8jhWyP51VmOzQOky5p0kYRDOoNmbsz" +
            "SC3qKrZMVI81oQs7Z9tiSrRrgIuoRKw9N9+nihCwW+lZI8wc7Spn2MhRk21pfPdBwbzXx4Uu+9X0dXDn4gNVL7BBV4v5tjObDR8zh5L4L++" +
            "tWBXjyUyJVTQOchlSKpnJzDqsFVjz9YL2glN2JcnQrI1QjiqGaewjRXlCPVvV9GrY/oswSa/i5ziSKxRc15jcHgFl+bpOqGbZ";
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
        RsaMessageSigner authenticator2 = new DefaultRsaMessageSigner(signingKey);
        assertEquals(authenticator2, signer);
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
    public void testMessageDecrypt_Pass() {
        String s = verifier.decryptAsString(privateKey,message);

        assertEquals(TEXT, s);
    }

    @Test
    public void testBytes64Decrypt_Pass() {
        byte[] b = verifier.decrypt(privateKey,message.getSignedAsBase64());

        assertNotNull(b);
        assertTrue(Arrays.equals(TEXT.getBytes(),b));
    }

    @Test
    public void testBytesDecrypt_Pass() {
        byte[] b = verifier.decrypt(privateKey,message.getSigned());

        assertNotNull(b);
        assertTrue(Arrays.equals(TEXT.getBytes(),b));
    }

    @Test
    public void testFileDecrypt_Pass() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File source = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File ciphertext = new File(STR."\{source.getAbsolutePath()}.enc");

        File output = new File(STR."\{source.getAbsolutePath()}.decrypted");
        try {
            boolean decrypted = verifier.decrypt(privateKey, ciphertext, output);

            String s = Files.lines(output.toPath())
                    .collect(Collectors.joining());

            assertEquals(FILE_TEXT, s);
            assertTrue(decrypted);
        } finally {
            output.delete();
        }
    }
}

