package org.javalaboratories.core.cryptography;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

public class CryptographyTest {

    private static final String AES_ENCRYPTED_FILE = "/aes-encrypted-file.enc";

    private static final String DATA = "The quick brown fox jumped over the fence";
    private static final String BASE64_ENCRYPTED_DATA = "nxLonzqOu4gjzuccbhc9qyZEJmtPp8h78rzK2wHmemOe3M6UMZIZf0KW9MNpMm7K";
    private static final String SECRET_KEY = "012345";

    @Test
    public void testAesCryptography_EncryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getAdvancedEncryptionStandard();

        // When
        byte[] result = cryptography.encrypt(SECRET_KEY, DATA.getBytes(UTF_8));

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(BASE64_ENCRYPTED_DATA, Base64.encodeBase64String(result));
    }

    @Test
    public void testAesCryptography_DecryptString_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getAdvancedEncryptionStandard();

        // When
        byte[] result = cryptography.decrypt(SECRET_KEY, Base64.decodeBase64(BASE64_ENCRYPTED_DATA));
        String data = new String(result);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals(DATA,data);
    }

    @Test
    public void testAesCryptography_EncryptStream_Pass() throws IOException {
        // Given
        Cryptography cryptography = CryptographyFactory.getAdvancedEncryptionStandard();
        InputStream istream = new ByteArrayInputStream(DATA.getBytes());
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.encrypt(SECRET_KEY, istream, ostream);

        // Then
        assertEquals(48,ostream.size());
    }

    @Test
    public void testAesCryptography_DecryptStream_Pass() {
        // Given
        Cryptography cryptography = CryptographyFactory.getAdvancedEncryptionStandard();
        InputStream istream = this.getClass().getResourceAsStream(AES_ENCRYPTED_FILE);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

        // When
        cryptography.decrypt(SECRET_KEY,istream,ostream);

        // Then
        assertEquals(DATA, ostream.toString());
    }
}
