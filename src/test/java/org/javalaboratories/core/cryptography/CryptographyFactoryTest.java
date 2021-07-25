package org.javalaboratories.core.cryptography;

import org.apache.commons.codec.binary.Base64;
import org.javalaboratories.core.cryptography.keys.PrivateKeyStore;
import org.javalaboratories.core.cryptography.keys.SecretKeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

public class CryptographyFactoryTest {

    private static final String AES_ENCRYPTED_FILE = "/aes-encrypted-file.enc";
    private static final String RSA_ENCRYPTED_FILE = "/rsa-encrypted-file.enc";

    private static final String DATA = "The quick brown fox jumped over the fence";
    private static final String BASE64_MESSAGE_DIGEST_ENCRYPTED_DATA = "szXZywC4mbxlE+zbshhwhw==";
    private static final String BASE64_SYMMETRIC1_ENCRYPTED_DATA = "nxLonzqOu4gjzuccbhc9qyZEJmtPp8h78rzK2wHmemOe3M6UMZIZf0KW9MNpMm7K";
    private static final String BASE64_SYMMETRIC2_ENCRYPTED_DATA = "i8e4gBtwVfQiegX3oNQ/N4unpRPKPrIfa9NTg4Ghrlc4/xgnPQDJ5aqznx68Kn3r";
    private static final String BASE64_SYMMETRIC3_ENCRYPTED_DATA = "6R7uflFiLugzwIiyZW/kmt5ExUvgRFtunkA0wrIcsUyq8VDSRtxe4vkjb7WxnnXp"; // SecretKey

    private static final String BASE64_ASYMMETRIC_ENCRYPTED_DATA = "xEaVNC58vPWDWD+tJ2ZrHL0EoP+F1ltUca9GcdXTVuITyyiqLNY2noqay100UGfj";
    private static final String BASE64_ASYMMETRIC_ENCRYPTED_KEY  = "D3HvZI4OTiwUqymzBa6HPlTJCAYaezV7aJ3tvD1b1xnc05tf6h1Z0kskUqhjDCkN" +
            "bo9lrD37yp6yBSgSdgu5l10T9bC9v4/3CPNlCIzEd/R4/5Lihtm831T4eDLcV0GiGoJ0HwryVCPVIb8+GesVKkhB6bcaFdCgjE8UJCKG2aa+Xf1ulwzHNOg" +
            "2R2Q+i3uLyYr/29RCfvQtiD3VQ3hHzh4q3JwHF1L7uON0VaSTjRSp219CBoJG2qFcqFV5bsL025MeYapy3Gu28p7XJmwtd6KQlwXEpapEHk4ecDPq6pq2Q3" +
            "a56IKpyVWoFSvGpvatpcnaZrH0S5MDEkJlC7kh2A==";
    
    private static final String SECRET_KEY = "012345";
    private static final String PRIVATE_KEY_ALIAS = "javalaboratories-org";
    private static final String PRIVATE_KEY_PASSWORD = "65533714";
    private static final String SECRET_KEY_ALIAS = "secret-key";
    private static final String SECRET_KEY_PASSWORD = "012345";

    private static final String PUBLIC_X509_CERTIFICATE = "/javalaboratories-org.cer";
    private static final String KEYSTORE_JKS_FILE = "/keystore.jks";
    private static final String KEYSTORE_JCEKS_FILE = "/keystore.jceks";

    private PrivateKeyStore privateKeyStore;
    private SecretKeyStore secretKeyStore;

    @BeforeEach
    public void setup() {
        privateKeyStore = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JKS_FILE))
                .storePassword("changeit")
                .build();

        secretKeyStore = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JCEKS_FILE))
                .storePassword("changeit")
                .build();
    }

    @Test
    public void testMdCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getSunMdCryptography();

        // When
        byte[] result = cryptography.encrypt(DATA.getBytes());

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_MESSAGE_DIGEST_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testMdCryptography_DecryptString_Fail() {
        // Given
        Cryptography cryptography = CryptographyFactory.getSunMdCryptography();

        // When/Then
        assertThrows(UnsupportedOperationException.class, () -> cryptography.decrypt(Base64.decodeBase64(BASE64_MESSAGE_DIGEST_ENCRYPTED_DATA)));
    }

    @Test
    public void testSunAesSymmetricCryptography_EncryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();

        // When
        byte[] result = cryptography.encrypt(SECRET_KEY, DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_SYMMETRIC1_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testSunAesSymmetricCryptography_DecryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();

        // When
        byte[] result = cryptography.decrypt(SECRET_KEY, Base64.decodeBase64(BASE64_SYMMETRIC1_ENCRYPTED_DATA));
        String data = new String(result);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,data);
    }

    @Test
    public void testSunAesSymmetricCryptography_EncryptStream_Pass() throws IOException {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        InputStream istream = new ByteArrayInputStream(DATA.getBytes());
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.encrypt(SECRET_KEY, istream, ostream);

        // Then
        assertEquals(48,ostream.size());
    }

    @Test
    public void testSunAesSymmetricCryptography_DecryptStream_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        InputStream istream = this.getClass().getResourceAsStream(AES_ENCRYPTED_FILE);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.decrypt(SECRET_KEY,istream,ostream);

        // Then
        assertEquals(DATA, ostream.toString());
    }

    @Test
    public void testSunAesCryptography_SecretKey_EncryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        SecretKey key = secretKeyStore.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD)
                .orElseThrow();

        // When
        byte[] result = cryptography.encrypt(key, DATA.getBytes());

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    public void testSunAesCryptography_SecretKey_DecryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getSunSymmetricCryptography();
        SecretKey key = secretKeyStore.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD)
                .orElseThrow();

        // When
        byte[] result = cryptography.decrypt(key, Base64.decodeBase64(BASE64_SYMMETRIC3_ENCRYPTED_DATA));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,new String(result));
    }

    @Test
    public void testSunAesCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getSunCryptography();

        // When
        byte[] result = cryptography.encrypt(DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_SYMMETRIC2_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testSunAesCryptography_DecryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getSunCryptography();

        // When
        byte[] result = cryptography.decrypt(Base64.decodeBase64(BASE64_SYMMETRIC2_ENCRYPTED_DATA));
        String data = new String(result);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,data);
    }

    @Test
    public void testSunAesCryptography_SecretKeyStore_StorePassword_Fail() {
        // Given
        SecretKeyStore store = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JCEKS_FILE))
                .storePassword("wrong-password")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD));
    }

    @Test
    public void testSunAesCryptography_SecretKeyStore_UnrecoverableKey_Fail() {
        // Given
        SecretKeyStore store = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JCEKS_FILE))
                .storePassword("changeit")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(SECRET_KEY_ALIAS,"wrong-password"));
    }

    @Test
    public void testSunAesCryptography_SecretKeyStore_KeyStoreFileCorrupted_Fail() {
        // Given
        SecretKeyStore store = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(RSA_ENCRYPTED_FILE)) // <-- not a keystore file
                .storePassword("changeit")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD));
    }

    @Test
    public void testSunAesCryptography_SecretKeyStore_KeyStoreException_Fail() {
        // Given
        SecretKeyStore store = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JCEKS_FILE))
                .keyStoreType("jks2") // <-- no such type should cause KeyStoreException
                .storePassword("changeit")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(SECRET_KEY_ALIAS,SECRET_KEY_PASSWORD));
    }

    @Test
    public void testSunAesCryptography_SecretKeyStore_Equality_Pass() {
        SecretKeyStore store = SecretKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JCEKS_FILE))
                .storePassword("changeit")
                .build();

        assertEquals(secretKeyStore,store);
    }


    ///////////// RSA Encryption tests


    @Test
    public void testSunRsaCryptography_EncryptString_Pass() throws CertificateException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(this.getClass().getResourceAsStream(PUBLIC_X509_CERTIFICATE));

        // When
        CryptographyResult result = cryptography.encrypt(certificate,DATA.getBytes());

        // Then
        assertNotNull(result);
        assertTrue(result.getData().length > 0);
        System.out.println("getData="+Base64.encodeBase64String(result.getData()));
        System.out.println("getEncryptedKey="+Base64.encodeBase64String(result.getEncryptedKey().getEncoded()));
    }

    @Test
    public void testSunRsaCryptography_DecryptString_Pass() throws KeyStoreException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
        PrivateKey key = privateKeyStore.getKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD)
                .orElseThrow();
        EncryptedAesKey encryptedKey = new EncryptedAesKey(Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_KEY));

        // When
        CryptographyResult result = cryptography.decrypt(key,encryptedKey,Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_DATA.getBytes()));
        String data = new String(result.getData());

        // Then
        assertEquals(DATA,data);
    }

    @Test
    public void testSunRsaCryptography_DecryptStream_Pass() throws CertificateException, KeyStoreException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
        InputStream istream = new ByteArrayInputStream(Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_DATA.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        PrivateKey key = privateKeyStore.getKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD)
                .orElseThrow();
        EncryptedAesKey encryptedKey = new EncryptedAesKey(Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_KEY));

        // When
        cryptography.decrypt(key,encryptedKey,istream,ostream);

        // Then
        assertEquals(DATA,ostream.toString());
    }

    @Test
    public void testSunRsaCryptography_EncryptStream_Pass() throws CertificateException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunAsymmetricCryptography();
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(this.getClass().getResourceAsStream(PUBLIC_X509_CERTIFICATE));
        InputStream istream = new ByteArrayInputStream(DATA.getBytes());
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        EncryptedAesKey aesKey = cryptography.encrypt(certificate,istream,ostream);

        // Then
        assertEquals(ostream.size(), 48);
        assertEquals(256, aesKey.getEncoded().length);
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_StorePassword_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JKS_FILE))
                .storePassword("wrong-password")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD));
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_UnrecoverableKey_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JKS_FILE))
                .storePassword("changeit")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(PRIVATE_KEY_ALIAS,"wrong-password"));
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_KeyStoreFileCorrupted_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(RSA_ENCRYPTED_FILE)) // <-- not a keystore file
                .storePassword("changeit")
                .build();

        assertThrows(CryptographyException.class, () -> store.getKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD));
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_KeyStoreException_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JKS_FILE))
                .keyStoreType("jks2") // <-- no such type should cause KeyStoreException
                .storePassword("changeit")
                .build();
        assertThrows(CryptographyException.class, () -> store.getKey(PRIVATE_KEY_ALIAS,PRIVATE_KEY_PASSWORD));
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_Equality_Pass() {
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_JKS_FILE))
                .storePassword("changeit")
                .build();

        assertEquals(privateKeyStore,store);
    }
}
