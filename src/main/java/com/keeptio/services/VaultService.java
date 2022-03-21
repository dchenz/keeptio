package com.keeptio.services;

import java.io.IOException;

import com.keeptio.entities.EncryptionConfig;
import com.keeptio.entities.Vault;
import com.keeptio.models.NewVaultConfig;
import com.keeptio.services.security.DecryptionException;
import com.keeptio.services.security.EncryptionService;
import com.keeptio.util.FileUtils;
import com.keeptio.util.Logging;

public class VaultService {
	
	private EncryptionService encryptionService;
	
	public VaultService() {
		encryptionService = new EncryptionService();
	}
	
	public Vault create(NewVaultConfig config) {

		EncryptionConfig encConfig = new EncryptionConfig(config.getEncryption());
		
		Vault createdVault = new Vault(config.getGeneral().getName(), encConfig);
		
		String password = config.getAuth().getPasswordProperty().getValue();
		
		String path = config.getGeneral().getPath();
		
		// Cache these values to allow the vault to be saved later
		// Setter only - there's no getter method
		createdVault.setCachedPassword(password);
		createdVault.setCachedFileLocation(path);
		
		save(createdVault, password, path);
		
		return createdVault;
	}

	public Vault read(String path, String password) {
		
		Vault fetchedVault = null;
		
		try {
		
			// Read the encrypted bytes from file
			byte[] encryptedJsonBytes = FileUtils.readFromFile(path);
			
			// Decrypt the bytes using password
			byte[] jsonBytes = encryptionService.decrypt(encryptedJsonBytes, password);
			
			// Get JSON from bytes
			String jsonText = new String(jsonBytes);
			
			// Load JSON into vault object
			fetchedVault = Vault.parse(jsonText);
			
			// Cache these values to allow the vault to be saved later
			// Setter only - there's no getter method
			fetchedVault.setCachedPassword(password);
			fetchedVault.setCachedFileLocation(path);
			
		} catch (ClassNotFoundException | DecryptionException | IOException e) {
			// Failure to decrypt vault file!
			Logging.warning("Failed to open: " + path);
			return null;
		}

		return fetchedVault;
	}

	public void save(Vault vault, String password, String path) {
		
		// Dump vault object as JSON
		String json = vault.stringify();
		
		// Get JSON as bytes
		byte[] jsonBytes = json.getBytes();
		
		// Encrypt the bytes using password and vault's encryption configuration
		byte[] encryptedJsonBytes = encryptionService.encrypt(jsonBytes, password, vault.getEncryption());
		
		// Save the encrypted bytes to file
		FileUtils.writeToFile(path, encryptedJsonBytes);

	}

}
