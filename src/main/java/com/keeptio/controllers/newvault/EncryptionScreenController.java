package com.keeptio.controllers.newvault;

import com.keeptio.controllers.WizardController;
import com.keeptio.models.VaultEncryptionConfig;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;
import com.keeptio.util.ErrorDecorator;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class EncryptionScreenController implements WizardController {

	private VaultEncryptionConfig model;
	
	public EncryptionScreenController(VaultEncryptionConfig model) {
		this.model = model;
	}

	private final int INIT_PBKDF_ROUNDS = 10000;

	@FXML
	private ChoiceBox<String> cipherDropdown;

	@FXML
	private ChoiceBox<String> hashDropdown;

	@FXML
	private TextField pbkdfRoundsField;

	@FXML
	public void initialize() {

		model.getCipherAlgorithmProperty().bind(cipherDropdown.valueProperty());
		model.getHashAlgorithmProperty().bind(hashDropdown.valueProperty());

		pbkdfRoundsField.textProperty().addListener((obs, oldValue, newValue) -> {
			String digitsOnlyValue = newValue.replaceAll("[^\\d]", "");
			pbkdfRoundsField.setText(digitsOnlyValue);
			if (digitsOnlyValue.isEmpty()) {
				model.getPbkdfRoundsProperty().setValue(0);
			} else {
				model.getPbkdfRoundsProperty().setValue(Integer.parseInt(digitsOnlyValue));	
			}
			ErrorDecorator.remove(pbkdfRoundsField);
		});

		// Set supported cipher algorithms
		for (SupportedCiphers sc : SupportedCiphers.values()) {
			cipherDropdown.getItems().add(sc.toString());
		}

		// Select AES as default
		cipherDropdown.getSelectionModel().select(0);

		// Set supported hash algorithms
		for (SupportedHashes sh : SupportedHashes.values()) {
			hashDropdown.getItems().add(sh.toString());
		}

		// Set SHA-256 as default
		hashDropdown.getSelectionModel().select(0);

		// Set initial PBKDF rounds
		pbkdfRoundsField.setText(Integer.toString(INIT_PBKDF_ROUNDS));

	}

	@Override
	public boolean tryPrevious() {
		return true;
	}

	@Override
	public boolean tryNext() {
		int rounds = 0;
		if (!pbkdfRoundsField.getText().isEmpty()) {
			rounds = Integer.parseInt(pbkdfRoundsField.getText());
		}
		if (rounds < 10000) {
			ErrorDecorator.apply(pbkdfRoundsField, "Must use at least 10000 rounds");
			return false;
		}
		return true;
	}

	@Override
	public String getTitle() {
		return "Encryption";
	}

}
