package org.javalaboratories.core.cryptography;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

public class CryptographyFactoryTest {

    private static final String AES_ENCRYPTED_FILE = "/aes-encrypted-file.enc";

    private static final String DATA = "The quick brown fox jumped over the fence";
    private static final String BASE64_ENCRYPTED_DATA = "nxLonzqOu4gjzuccbhc9qyZEJmtPp8h78rzK2wHmemOe3M6UMZIZf0KW9MNpMm7K";
    private static final String BASE64_NO_KEY_ENCRYPTED_DATA = "i8e4gBtwVfQiegX3oNQ/N4unpRPKPrIfa9NTg4Ghrlc4/xgnPQDJ5aqznx68Kn3r";
    private static final String BASE64_MD_ENCRYPTED_DATA = "szXZywC4mbxlE+zbshhwhw==";
    private static final String SECRET_KEY = "012345";

    @Test
    public void testAesSymmetricCryptography_EncryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getAesSymmetricCryptography();

        // When
        byte[] result = cryptography.encrypt(SECRET_KEY, DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testAesSymmetricCryptography_DecryptString_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getAesSymmetricCryptography();

        // When
        byte[] result = cryptography.decrypt(SECRET_KEY, Base64.decodeBase64(BASE64_ENCRYPTED_DATA));
        String data = new String(result);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,data);
    }

    @Test
    public void testAesSymmetricCryptography_EncryptStream_Pass() throws IOException {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getAesSymmetricCryptography();
        InputStream istream = new ByteArrayInputStream(DATA.getBytes());
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.encrypt(SECRET_KEY, istream, ostream);

        // Then
        assertEquals(48,ostream.size());
    }

    @Test
    public void testAesSymmetricCryptography_DecryptStream_Pass() {
        // Given
        SymmetricCryptography cryptography = CryptographyFactory.getAesSymmetricCryptography();
        InputStream istream = this.getClass().getResourceAsStream(AES_ENCRYPTED_FILE);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.decrypt(SECRET_KEY,istream,ostream);

        // Then
        assertEquals(DATA, ostream.toString());
    }

    @Test
    public void testAesCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getAesCryptography();

        // When
        byte[] result = cryptography.encrypt(DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_NO_KEY_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testAesCryptography_DecryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getAesCryptography();

        // When
        byte[] result = cryptography.decrypt(Base64.decodeBase64(BASE64_NO_KEY_ENCRYPTED_DATA));
        String data = new String(result);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,data);
    }

    @Test
    public void testMdCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getMdCryptography();

        // When
        byte[] result = cryptography.encrypt(DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_MD_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testMdCryptography_DecryptString_Fail() {
        // Given
        Cryptography cryptography = CryptographyFactory.getMdCryptography();

        // When/Then
        assertThrows(UnsupportedOperationException.class, () -> cryptography.decrypt(Base64.decodeBase64(BASE64_MD_ENCRYPTED_DATA)));
    }
}
