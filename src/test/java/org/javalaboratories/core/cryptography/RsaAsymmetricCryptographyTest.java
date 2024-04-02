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

    private static final String CIPHERTEXT="AAABAIE6T+0umXOuKxAae8iD3Vd1JWL6s6Bldj7wti5C2bT/IQ+V2ICzRV34PYJfxi5TjTun/4Sp" +
            "JYDER7tGO+/evm++QOC2D76ic0nlU+PlcJT3WwR5teiMJHl9cSvYczQsPuayBSKRIhrVra5KiCEs0pHWpej5rfUOIA+baC+qyoazfdxLWgg" +
            "wTe6x8KuEGpvB/7TTL0mEebpZAbVIegK0KBCcxzb6MTRKOkSJBo2qYLYissq7Y5ey8xdJf7mgUVevL68f8anIeMzyMYiaRSV03Q/84e+Q4E" +
            "AD7v6F7A1Qd/yecyu+Ppi+LRAOSfcy3DGw/YpkHJOS7S8vboNHCHAufQCrGLRi8aw0AKI4eKutO4vexP04WviBO9x6N/QSjxdTNZ4QLFmlH" +
            "0sd1jsHPCZ4Aw11z944a9xWebwzEVaeGroxU0ywz9Zq4Vflh1gxY4LObguFd1Xcy4qopfhdiZ9trEfhtu7G+vvmQwr64J966LvU";
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

        StringCryptographyResult<PrivateKey> stringResult = cryptography.decrypt(privateKey, result.getBytesAsBase64());
        assertEquals(TEXT, stringResult.getString().orElseThrow());
    }

    @Test
    public void testStringDecryption_Pass() {
        StringCryptographyResult<PrivateKey> result = cryptography.decrypt(privateKey,CIPHERTEXT);

        assertEquals(TEXT, result.getString().orElseThrow());
    }

    @Test
    public void testStreamEncryption_Pass() {
        StreamCryptographyResult<PublicKey,ByteArrayOutputStream> result = cryptography
                .encrypt(publicKey, new ByteArrayInputStream(TEXT.getBytes()), new ByteArrayOutputStream());

        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result2 = cryptography
                .decrypt(privateKey,new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result2.getStream().toString());
    }

    @Test
    public void testStreamDecryption_Pass() {
        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result = cryptography
                .decrypt(privateKey, new ByteArrayInputStream(Base64.getDecoder().decode(CIPHERTEXT)),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result.getStream().toString());
    }

    @Test
    public void testFileCryptography_Pass() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaAsymmetricCryptographyTest.class.getClassLoader();
        File file = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File cipherFile = Paths.get(classLoader.getResource(RSA_ENCRYPTED_TEST_FILE).toURI()).toFile();
        File decipheredFile = Paths.get(classLoader.getResource(RSA_UNENCRYPTED_TEST_FILE).toURI()).toFile();

        cryptography.encrypt(publicKey,file,cipherFile);

        FileCryptographyResult<PrivateKey> result2 = cryptography
                .decrypt(privateKey,cipherFile,decipheredFile);

        String s = Files.lines(result2.getFile().toPath())
                .collect(Collectors.joining());

        assertEquals(FILE_TEXT,s);

    }

}
