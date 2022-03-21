package com.keeptio.entities;

import java.util.Base64;

import com.keeptio.services.security.DecryptionException;
import com.keeptio.services.security.EncryptionService;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class EncryptedSecret extends Secret {

	protected EncryptedSecret(String name, String value, String description, String password) {
		super(name, value, description);
		if (password == null) {
			throw new IllegalArgumentException("Password cannot be NULL");
		}
		super.setValue(encryptString(value, password));
		if (!description.isEmpty()) {
			// Only encrypt the description if it exists
			super.setDescription(encryptString(description, password));	
		}
	}

	public Secret retrieve(String password) {
		if (password == null) {
			throw new IllegalArgumentException("Password cannot be NULL");
		}
		String value = decryptString(super.getValue(), password);
		if (value == null) {
			return null;
		}
		String description = super.getDescription(); 
		if (!description.isEmpty()) {
			description = decryptString(super.getDescription(), password);
			if (description == null) {
				return null;
			}		
		}
	
		// Create a new Secret object and return it with decrypted value
		Secret s = new Secret(super.getName(), value, description);	
		
		// Must be an exact copy of this object except value/description is decrypted
		s.setId(super.getId());
		s.setCreatedTimestamp(super.getCreatedTimestamp());
		s.setGroupId(super.getGroupId());
		
		// Any changes to this new secret object must be updated in original encrypted version
		
		s.nameProperty().addListener((obs, oldValue, newValue) -> {
			super.setName(newValue);
		});
		s.valueProperty().addListener((obs, oldValue, newValue) -> {
			super.setValue(encryptString(newValue, password));
		});
		s.descriptionProperty().addListener((obs, oldValue, newValue) -> {
			super.setDescription(encryptString(newValue, password));
		});
		s.groupIdProperty().addListener((obs, oldValue, newValue) -> {
			super.setGroupId(newValue);
		});
		
		return s;
	}

	@Override
	public boolean isEncrypted() {
		return true;
	}
	
	private static final EncryptionService encryptionService = new EncryptionService(); 
	
	private String encryptString(String plainText, String password) {
		// Default configuration
		EncryptionConfig encryptionConfig = new EncryptionConfig(
			SupportedCiphers.AES_256, 
			SupportedHashes.SHA_256,
			10000
		);
		byte[] encryptedValueBytes = encryptionService.encrypt(plainText.getBytes(), password, encryptionConfig);
		// Encode to base64 string and set secret to encoded encrypted string
		return Base64.getEncoder().encodeToString(encryptedValueBytes);
	}
	
	private String decryptString(String cipherText, String password) {
		String text = null;
		try {
			// 1. Decode from base64 string
			// 2. Decrypt (encryption configuration is included in cipher text)
			byte[] encryptedDescBytes = Base64.getDecoder().decode(cipherText);
			byte[] descriptionBytes = encryptionService.decrypt(encryptedDescBytes, password);
			text = new String(descriptionBytes);
		} catch (DecryptionException e) {
			return null;
		}
		return text;
	}

}
