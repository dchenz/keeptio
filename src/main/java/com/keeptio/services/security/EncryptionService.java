package com.keeptio.services.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.keeptio.entities.EncryptionConfig;

public class EncryptionService {

	private static final int SALT_SIZE = 16;

	/**
	 * Encrypts a byte array using a password. Specify the encryption strategy to
	 * customise cipher algorithm, hash algorithm and number of PBKDF2 HMAC rounds.
	 * 
	 * @param plainText - Bytes to be encrypted.
	 * @param password  - Password used to encrypt/decrypt plain text.
	 * @param config    - Details of encryption strategy (cipher, hash, rounds).
	 * 
	 * @return Encrypted cipher text. Includes EncryptionConfig, password salt and
	 *         IV (if applicable) appended to the encrypted bytes in that order.
	 */
	public byte[] encrypt(byte[] plainText, String password, EncryptionConfig config) {

		if (plainText == null || plainText.length == 0) {
			throw new IllegalArgumentException("Text to encrypt cannot be NULL or empty");
		}

		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be NULL or empty");
		}

		if (config == null) {
			throw new IllegalArgumentException("Configuration cannot be NULL");
		}

		// Use the configuration to instantiate cipher/hash algorithms
		SymmetricCipher cipher = SymmetricCipherFactory.getInstance(config.getCipherAlgorithm());
		PasswordHashing hash = new PBKDF2HashSHA(config.getHashAlgorithm(), config.getPbkdfRounds());

		// Generate a 16-byte salt for PBKDF2
		byte[] keySalt = SecureRandomUtils.randBytes(SALT_SIZE);

		// Derive the key from password and salt, then encrypt the data
		byte[] encryptedBytes = cipher.encrypt(plainText, hash.digest(password, keySalt));

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			// Save 1 byte cipher algorithm
			baos.write(config.getCipherAlgorithm().getId());
			// Save 1 byte hash algorithm
			baos.write(config.getHashAlgorithm().getId());
			// Save 4 byte integer
			dos.writeInt(config.getPbkdfRounds());
			// Save 16 byte key salt
			baos.write(keySalt);
			// Save the encrypted data
			baos.write(encryptedBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	/**
	 * Exact opposite of {@link EncryptionService#encrypt}.
	 * 
	 * @param cipherText - Bytes to be decrypted. Must contain the added header.
	 * @param password   - Password used to encrypt/decrypt plain text.
	 * 
	 * @return Decrypted plain text. Removes the header included during encryption.
	 * 
	 * @throws DecryptionException
	 */
	public byte[] decrypt(byte[] cipherText, String password) throws DecryptionException {

		if (cipherText == null || cipherText.length == 0) {
			throw new IllegalArgumentException("Text to decrypt cannot be NULL or empty");
		}

		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be NULL or empty");
		}

		EncryptionConfig config = null;
		byte[] keySalt = null;
		byte[] encryptedBytes = null;

		ByteArrayInputStream bais = new ByteArrayInputStream(cipherText);
		DataInputStream dis = new DataInputStream(bais);
		try {
			// Read 6 bytes of header into encryption settings
			SupportedCiphers cipher = SupportedCiphers.get((char) bais.read());
			SupportedHashes hash = SupportedHashes.get((char) bais.read());
			int rounds = dis.readInt();
			config = new EncryptionConfig(cipher, hash, rounds);
			// Read 16 bytes of key salt
			keySalt = bais.readNBytes(SALT_SIZE);
			// Read all encrypted data bytes
			encryptedBytes = bais.readAllBytes();
		} catch (IOException e) {
			throw new DecryptionException();
		}

		// Use the configuration to instantiate cipher/hash algorithms
		SymmetricCipher cipher = SymmetricCipherFactory.getInstance(config.getCipherAlgorithm());
		PasswordHashing hash = new PBKDF2HashSHA(config.getHashAlgorithm(), config.getPbkdfRounds());

		// Derive the key from password and salt, then decrypt the data
		return cipher.decrypt(encryptedBytes, hash.digest(password, keySalt));
	}

}
