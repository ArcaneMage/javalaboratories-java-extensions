package org.javalaboratories.core.cryptography;

import org.javalaboratories.core.cryptography.keys.RsaKeys;
import org.javalaboratories.core.util.Bytes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

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
public class RsaHybridCryptographyTest {

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

    private final InputStream mockInputStream = mock(InputStream.class);
    private final InputStream mockEndOfInputStream = mock(InputStream.class);
    private final OutputStream mockOutputStream = mock(OutputStream.class);

    @BeforeEach
    public void setup() throws GeneralSecurityException, URISyntaxException, IOException {
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File privateKeyfile = Paths.get(classLoader.getResource(PRIVATE_KEY_FILE).toURI()).toFile();
        File publicKeyfile = Paths.get(classLoader.getResource(PUBLIC_KEY_FILE).toURI()).toFile();

        privateKey = RsaKeys.getPrivateKeyFrom(privateKeyfile);
        publicKey = RsaKeys.getPublicKeyFrom(publicKeyfile);

        cryptography = CryptographyFactory.getAsymmetricHybridCryptography();

        when(mockInputStream.read(any())).thenThrow(IOException.class);
        doThrow(IOException.class).when(mockOutputStream).write(any());
        when(mockEndOfInputStream.read(any())).thenReturn(-1);
    }

    @Test
    public void testStringEncryption_Pass() {
        ByteCryptographyResult<PublicKey> result = cryptography.encrypt(publicKey,TEXT);

        ByteCryptographyResult<PrivateKey> stringResult = cryptography.decrypt(privateKey, result.getBytesAsBase64());

        assertNotNull(result.getKey());
        assertTrue(result.getSessionKey().isPresent());
        assertEquals(TEXT, stringResult.getString().orElseThrow());
    }

    @Test
    public void testStringEncryption_withGeneralSecurityException_Fail() {
        try (MockedStatic<Cipher> cipher = Mockito.mockStatic(Cipher.class)) {
            cipher.when(() -> Cipher.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class, () -> cryptography.encrypt(publicKey, TEXT));
        }
    }

    @Test
    public void testStringDecryption_Pass() {
        ByteCryptographyResult<PrivateKey> result = cryptography.decrypt(privateKey,CIPHERTEXT);

        assertEquals(TEXT, result.getString().orElseThrow());
    }

    @Test
    public void testStringDecryption_withGeneralSecurityException_Fail() {
        try (MockedStatic<Cipher> cipher = Mockito.mockStatic(Cipher.class)) {
            cipher.when(() -> Cipher.getInstance(anyString())).thenThrow(NoSuchAlgorithmException.class);

            assertThrows(CryptographyException.class, () -> cryptography.decrypt(privateKey, CIPHERTEXT));
        }
    }

    @Test
    public void testStringDecryption_withBase64Exception_Fail() {
        assertThrows(CryptographyException.class, () -> cryptography.decrypt(privateKey, TEXT));
    }

    @Test
    public void testStreamEncryption_Pass() {
        StreamCryptographyResult<PublicKey,ByteArrayOutputStream> result = cryptography
                .encrypt(publicKey, new ByteArrayInputStream(TEXT.getBytes()), new ByteArrayOutputStream());

        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result2 = cryptography
                .decrypt(privateKey,new ByteArrayInputStream(result.getStream().toByteArray()),
                        new ByteArrayOutputStream());

        assertNotNull(result.getKey());
        assertTrue(result.getSessionKey().isPresent());
        assertEquals(TEXT,result2.getStream().toString());
    }

    @Test
    public void testStreamEncryption_withIOException_Fail() {
        assertThrows(CryptographyException.class, () -> cryptography
                .encrypt(publicKey, mockInputStream, mockOutputStream));
    }

    @Test
    public void testStreamDecryption_Pass() {
        StreamCryptographyResult<PrivateKey,ByteArrayOutputStream> result = cryptography
                .decrypt(privateKey, new ByteArrayInputStream(Base64.getDecoder().decode(CIPHERTEXT)),
                        new ByteArrayOutputStream());

        assertEquals(TEXT,result.getStream().toString());
    }

    @Test
    public void testStreamDecryption_withIOExceptionEOS_Fail() {
        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(privateKey, mockEndOfInputStream, mockOutputStream));
    }

    @Test
    public void testStreamDecryption_withCorruptedSessionKey_Fail() {
        try (MockedStatic<Bytes> bytes = Mockito.mockStatic(Bytes.class)) {
            bytes.when(() -> Bytes.fromBytes(any())).thenReturn(127);

            assertThrows(CryptographyException.class, () -> cryptography
                    .decrypt(privateKey, new ByteArrayInputStream(TEXT.getBytes()), new ByteArrayOutputStream()));
        }
    }

    @Test
    public void testStreamDecryption_withIOException_Fail() {
        assertThrows(CryptographyException.class, () -> cryptography
                .decrypt(privateKey, mockInputStream, mockOutputStream));
    }

    @Test
    public void testFileCryptography_Pass() throws URISyntaxException, IOException {
        ClassLoader classLoader = RsaHybridCryptographyTest.class.getClassLoader();
        File file = Paths.get(classLoader.getResource(AES_UNENCRYPTED_FILE).toURI()).toFile();
        File cipherFile = Paths.get(classLoader.getResource(RSA_ENCRYPTED_TEST_FILE).toURI()).toFile();
        File decipheredFile = Paths.get(classLoader.getResource(RSA_UNENCRYPTED_TEST_FILE).toURI()).toFile();

        FileCryptographyResult<PublicKey> result = cryptography.encrypt(publicKey,file,cipherFile);

        FileCryptographyResult<PrivateKey> result2 = cryptography
                .decrypt(privateKey,cipherFile,decipheredFile);

        String s = Files.lines(result2.getFile().toPath())
                .collect(Collectors.joining());

        assertNotNull(result.getFile());
        assertNotNull(result.getKey());
        assertTrue(result.getSessionKey().isPresent());

        assertNotNull(result2.getKey());
        assertTrue(result2.getSessionKey().isPresent());
        assertEquals(FILE_TEXT,s);
    }

}
