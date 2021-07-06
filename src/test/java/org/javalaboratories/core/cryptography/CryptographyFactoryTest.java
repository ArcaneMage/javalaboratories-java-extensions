package org.javalaboratories.core.cryptography;

import org.apache.commons.codec.binary.Base64;
import org.javalaboratories.core.cryptography.keys.PrivateKeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private static final String BASE64_ASYMMETRIC_ENCRYPTED_DATA = "VJz19362IovaDlyLgM01IShz6Z64a6X98kifQEiSACu+m6+rLG55G" +
            "gX43yeD2NMIboWEqXuXNU9bwmKkEhRAzTVJKnP/wYW9abPJW5RXhZO4LMJBoB4HLYsv4XY6gXIzSS91gvCckaSzuha+xlW32Dx7B0C82/9on" +
            "zMZt+uavFO/dwsTbCGtwRcsoMReml+0S5R+KRaTyUIRN7qyM8ohoLN5ao5Y7WheSyiN7SNOrYZs7u4VKI+uw8aBP5bS5NR0ZM3XbSBG6I+r9" +
            "P71Up4AAIigZrlc0caAGhBqsiGDXccquKOER3/NZEUUrZcL246x9Yx2nfVXgiLynQ4ba1W85g==";
    
    private static final String SECRET_KEY = "012345";
    private static final String PRIVATE_KEY_PASSWORD = "65533714";

    private static final String PUBLIC_X509_CERTIFICATE = "/javalaboratories-org.cer";
    private static final String KEYSTORE_FILE = "/keystore.jks";

    private PrivateKeyStore keyStore;

    @BeforeEach
    public void setup() {
        keyStore = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_FILE))
                .storePassword("changeit")
                .keyAlias("javalaboratories-org")
                .keyPassword(PRIVATE_KEY_PASSWORD)
                .build();
    }

    @Test
    public void testMdCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getSunMdCryptography();

        // When
        byte[] result = cryptography.encrypt(DATA.getBytes(UTF_8));

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
    public void testSunRsaCryptography_EncryptString_Pass() throws CertificateException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunRsaAsymmetricCryptography();
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(this.getClass().getResourceAsStream(PUBLIC_X509_CERTIFICATE));

        // When
        byte[] result = cryptography.encrypt(certificate,DATA.getBytes());

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    public void testSunRsaCryptography_DecryptStream_Pass() throws CertificateException, KeyStoreException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunRsaAsymmetricCryptography();
        InputStream istream = this.getClass().getResourceAsStream(RSA_ENCRYPTED_FILE);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        PrivateKey key = keyStore.getKey();

        // When
        cryptography.decrypt(key,istream,ostream);

        // Then
        assertEquals(DATA,ostream.toString());
    }

    @Test
    public void testSunRsaCryptography_EncryptStream_Pass() throws CertificateException, KeyStoreException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunRsaAsymmetricCryptography();
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate = factory.generateCertificate(this.getClass().getResourceAsStream(PUBLIC_X509_CERTIFICATE));
        InputStream istream = new ByteArrayInputStream(DATA.getBytes());
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.encrypt(certificate,istream,ostream);

        // Then
        assertEquals(ostream.size(), 256);
    }

    @Test
    public void testSunRsaCryptography_DecryptString_Pass() throws KeyStoreException {
        // Given
        AsymmetricCryptography cryptography = CryptographyFactory.getSunRsaAsymmetricCryptography();
        PrivateKey key = keyStore.getKey();

        // When
        byte[] result = cryptography.decrypt(key,Base64.decodeBase64(BASE64_ASYMMETRIC_ENCRYPTED_DATA.getBytes()));
        String data = new String(result);

        // Then
        assertEquals(DATA,data);
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_StorePassword_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_FILE))
                .storePassword("wrong-password")
                .keyAlias("javalaboratories-org")
                .keyPassword(PRIVATE_KEY_PASSWORD)
                .build();

        assertThrows(IllegalArgumentException.class, store::getKey);
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_UnrecoverableKey_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_FILE))
                .storePassword("changeit")
                .keyAlias("javalaboratories-org")
                .keyPassword("wrong-password")
                .build();

        assertThrows(IllegalArgumentException.class, store::getKey);
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_KeyStoreFileCorrupted_Fail() {
        // Given
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(RSA_ENCRYPTED_FILE)) // <-- not a keystore file
                .storePassword("changeit")
                .keyAlias("javalaboratories-org")
                .keyPassword(PRIVATE_KEY_PASSWORD)
                .build();

        assertThrows(IllegalArgumentException.class, store::getKey);
    }

    @Test
    public void testSunRsaCryptography_PrivateKeyStore_Equality_Pass() {
        PrivateKeyStore store = PrivateKeyStore.builder()
                .keyStoreStream(this.getClass().getResourceAsStream(KEYSTORE_FILE))
                .storePassword("changeit")
                .keyAlias("javalaboratories-org")
                .keyPassword(PRIVATE_KEY_PASSWORD)
                .build();

        assertEquals(keyStore,store);
    }
}
