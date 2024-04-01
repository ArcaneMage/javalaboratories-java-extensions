package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.cryptography.keys.RsaKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Notes for creating public and private key files using {@code openssl} commands:
 * <pre>
 *     {@code
 *          // Generate RSA private key
 *          openssl genrsa -out rsa-private-key.pem 2048
 *
 *          // Convert to PKCS8 format, output PEM
 *          openssl pkcs8 -topk8 -inform PEM -in rsa-private-key.pem -outform PEM -out rsa-private-key-pkcs8.pem
 *
 *          // Extract public key from private key
 *          openssl rsa -in rsa-private-key-pkcs8.pem -pubout -outform PEM -out rsa-public-key.pem
 *     }
 * </pre>
 */
public class RsaAsymmetricCryptographyTest {

    private static final String PRIVATE_KEY_FILE = "rsa-private-key-pkcs8.pem";
    private static final String PUBLIC_KEY_FILE = "rsa-public-key.pem";

    private static final String AES_UNENCRYPTED_FILE = "aes-unencrypted-file.txt";
    private static final String RSA_ENCRYPTED_TEST_FILE = "rsa-encrypted-test-file.tmp";
    private static final String RSA_UNENCRYPTED_TEST_FILE = "rsa-unencrypted-test-file.tmp";

    private static final String CIPHER_KEY="WpmHWkCker2ZpoNZViBEUc/oLm8NOKgDFjN0egfjw727rdGLQ/PC3GfVLtK/+t2c3LnyQi3mmsuy" +
            "3iGrPlMO/ixq7XdVuPe2jrfQMRno5J4Dyy+JwEYNmwwWO9tH1kmBP9yirqKY5wF9KHtdTvqwr4EowwK4Xlqb8d0lF3ASzJXnsed+3FgAig6" +
            "Q4PwQt0K7MvQH48IGVM16FY75bDD386SAHWnAAnOgS18+XssZYgyEpGfnaaY3D3AERVCeSvt1zM/ni4IpqCEqK1HV++vbVk2NJLuEmljFRg" +
            "5ZVkZ/4RqFKPUc/XzyU26JWAuBEbc47qqC69+cCCzQ0IbGdQH08g==";
    private static final String CIPHERTEXT="xCJQkWTO68ohV5i/tnOnTqogJZGWp+kZEieSjoyHnLKK9DUewsfxF7897ldp35hnd9H+VcBss+/V" +
            "nh7EKO//BfGCs0ygxKSTeJmOctscmceL7HUR6tVjKbuyGlcJiA22vUFeGJQ/Qz+BvBKJCuuh+g==";
    private static final String TEXT = "The quick brown fox jumped over the fence and then back again, just for a laugh.";
    private static final String FILE_TEXT = "This is a test file with encrypted data -- TOP SECRET!";
    private RsaHybridCryptography cryptography;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    @BeforeEach
    public void setup() throws URISyntaxException {
        ClassLoader classLoader = RsaAsymmetricCryptographyTest.class.getClassLoader();
        File privateKeyfile = Paths.get(classLoader.getResource(PRIVATE_KEY_FILE).toURI()).toFile();
        File publicKeyfile = Paths.get(classLoader.getResource(PUBLIC_KEY_FILE).toURI()).toFile();

        privateKey = RsaKeys.getPrivateKeyFrom(privateKeyfile);
        publicKey = RsaKeys.getPublicKeyFrom(publicKeyfile);
        cryptography = CryptographyFactory.getAsymmetricHybridCryptography();
    }

    @Test
    public void testStringEncryption_Pass() {
        StringCryptographyResult<PublicKey> result = cryptography.encrypt(publicKey,TEXT);

        StringCryptographyResult<PrivateKey> stringResult = cryptography.decrypt(privateKey,result.geCipherKeyAsBase64(),
                result.getBytesAsBase64());
        assertEquals(TEXT, stringResult.getString().orElseThrow());
    }

    @Test
    public void testStringDecryption_Pass() {
        StringCryptographyResult<PrivateKey> result = cryptography.decrypt(privateKey,CIPHER_KEY,CIPHERTEXT);

        assertEquals(TEXT, result.getString().orElseThrow());
    }

    @Test
    public void testStreamEncryption_Pass() {
        StreamCryptographyResult<PublicKey,ByteArrayOutputStream> result = cryptography
                .encrypt(publicKey, new ByteArrayInputStream(TEXT.getBytes()), new ByteArrayOutputStream());

        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result2 = cryptography
                .decrypt(privateKey,result.geCipherKeyAsBase64(),new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result2.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Pass() {
        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result = cryptography
                .decrypt(privateKey, CIPHER_KEY, new ByteArrayInputStream(Base64.getDecoder().decode(CIPHERTEXT)),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result.getStream().toString());
    }

    @Test
    public void testFileCryptography_Pass() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaAsymmetricCryptographyTest.class.getClassLoader();
        File file = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File cipherFile = Paths.get(classLoader.getResource(RSA_ENCRYPTED_TEST_FILE).toURI()).toFile();
        File decipheredFile = Paths.get(classLoader.getResource(RSA_UNENCRYPTED_TEST_FILE).toURI()).toFile();

        FileCryptographyResult<PublicKey> result = cryptography
                .encrypt(publicKey,file,cipherFile);

        FileCryptographyResult<PrivateKey> result2 = cryptography
                .decrypt(privateKey,result.geCipherKeyAsBase64(),cipherFile,decipheredFile);

        String s = Files.lines(result2.getFile().toPath())
                .collect(Collectors.joining());

        assertEquals(FILE_TEXT,s);

    }

}
