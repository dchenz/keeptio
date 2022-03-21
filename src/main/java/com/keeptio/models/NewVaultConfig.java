package com.keeptio.models;

import lombok.Getter;

@Getter

public class NewVaultConfig {

	private VaultGeneralConfig general;
	
	private VaultAuthConfig auth;
	
	private VaultEncryptionConfig encryption;
	
	public NewVaultConfig() {
		general = new VaultGeneralConfig();
		auth = new VaultAuthConfig();
		encryption = new VaultEncryptionConfig();
	}
	
}
