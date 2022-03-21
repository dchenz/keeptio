package com.keeptio.services.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class AESCipher implements SymmetricCipher {
	
	private static final int BLOCK_SIZE = 16;
	
	private static final int KEY_SIZE = 32;
	
	@Override
	public byte[] encrypt(byte[] plainText, byte[] key) {
		if (key.length < KEY_SIZE) {
			throw new IllegalArgumentException("Key must be at least 32 bytes (excess truncated)");
		}
		byte[] outputBytes = null;
		try {
			// Generate random IV
			byte[] ivBytes = SecureRandomUtils.randBytes(BLOCK_SIZE);

			// Get 32 bytes from key (truncate any excess)
			byte[] aesKey = Arrays.copyOf(key, KEY_SIZE);
			
			// Encrypt the bytes using key + IV
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(ivBytes));
			byte[] cipherText = cipher.doFinal(plainText);
			
			// Join salt + IV + cipher text
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(ivBytes);
			baos.write(cipherText);
			outputBytes = baos.toByteArray();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputBytes;
	}

	@Override
	public byte[] decrypt(byte[] cipherText, byte[] key) throws DecryptionException {
		if (key.length < KEY_SIZE) {
			throw new IllegalArgumentException("Key must be at least 32 bytes (excess truncated)");
		}
		byte[] outputBytes = null;
		try {
			// Get IV + cipher text
			ByteArrayInputStream bais = new ByteArrayInputStream(cipherText);
			byte[] ivBytes = bais.readNBytes(BLOCK_SIZE);
			byte[] encryptedBytes = bais.readAllBytes();
	
			// Decrypt the bytes using key + IV
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(ivBytes));
			outputBytes = cipher.doFinal(encryptedBytes);
			
		} catch (Exception e) {
			throw new DecryptionException();
		}
		return outputBytes;
	}

}
