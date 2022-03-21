package com.keeptio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.keeptio.entities.EncryptionConfig;
import com.keeptio.services.security.EncryptionService;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;

public class TestEncryption {

	private EncryptionService encService;

	public TestEncryption() {
		encService = new EncryptionService();
	}

	@Test
	public void test_reverse_AES256_SHA256() {

		String plainText = "TESTING 1234567890!!!!!!";

		String password = "password_123";

		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);

		runTestReverse(plainText, password, config);

	}

	@Test
	public void test_reverse_AES256_SHA512() {

		String plainText = "TESTING 1234567890!!!!!!";

		String password = "password_123";

		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_512, 10000);

		runTestReverse(plainText, password, config);

	}

	private void runTestReverse(String plainText, String password, EncryptionConfig config) {
		String decryptedText = null;
		try {
			byte[] encryptedBytes = encService.encrypt(plainText.getBytes(), password, config);
			byte[] decryptedBytes = encService.decrypt(encryptedBytes, password);
			decryptedText = new String(decryptedBytes);
		} catch (Exception e) {
			fail("Could not reverse the encryption");
		}
		assertEquals(plainText, decryptedText);
	}

	@Test
	public void test_invalid_noPlainText() {

		assertThrows(IllegalArgumentException.class, () -> {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt(null, "password_123", config);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt("".getBytes(), "password_123", config);
		});

		try {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt(new byte[1], "password_123", config);
		} catch (IllegalArgumentException e) {
			fail("Text of 1 byte should be accepted");
		}

	}

	@Test
	public void test_invalid_noPassword() {

		assertThrows(IllegalArgumentException.class, () -> {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt("test".getBytes(), null, config);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt("test".getBytes(), "", config);
		});

		try {
			EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, 10000);
			encService.encrypt("test".getBytes(), "1", config);
		} catch (IllegalArgumentException e) {
			fail("Password of 1 character should be accepted");
		}

	}
	
	@Test
	public void test_invalid_noConfig() {

		assertThrows(IllegalArgumentException.class, () -> {
			encService.encrypt("test".getBytes(), "password", null);
		});

	}

}
