package com.keeptio.models;

import com.keeptio.util.Logging;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter

public class VaultEncryptionConfig {

	private StringProperty cipherAlgorithmProperty;

	private StringProperty hashAlgorithmProperty;

	private IntegerProperty pbkdfRoundsProperty;

	public VaultEncryptionConfig() {
		cipherAlgorithmProperty = new SimpleStringProperty();
		hashAlgorithmProperty = new SimpleStringProperty();
		pbkdfRoundsProperty = new SimpleIntegerProperty();
		Logging.addPropertyLogging("Cipher", cipherAlgorithmProperty);
		Logging.addPropertyLogging("Hash", hashAlgorithmProperty);
		Logging.addPropertyLogging("PBKDF2 Rounds", pbkdfRoundsProperty);
	}
	
	public VaultEncryptionConfig(String cipherAlgorithm, String hashAlgorithm, Integer pbkdfRounds) {
		this();
		cipherAlgorithmProperty.setValue(cipherAlgorithm);
		hashAlgorithmProperty.setValue(hashAlgorithm);
		pbkdfRoundsProperty.setValue(pbkdfRounds);
	}

	public String getCipherAlgorithm() {
		return cipherAlgorithmProperty.getValue();
	}
	
	public String getHashAlgorithm() {
		return hashAlgorithmProperty.getValue();
	}
	
	public Integer getPbkdfRounds() {
		return pbkdfRoundsProperty.getValue();
	}
	
}
