package com.keeptio.controllers.newvault;

import com.keeptio.controllers.WizardController;
import com.keeptio.models.VaultEncryptionConfig;
import com.keeptio.models.VaultGeneralConfig;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReviewScreenController implements WizardController {
	
	private VaultGeneralConfig general;

	private VaultEncryptionConfig encryption;
	
	public ReviewScreenController(VaultGeneralConfig general, VaultEncryptionConfig encryption) {
		this.general = general;
		this.encryption = encryption;
	}

	@FXML
	private Label dbNameLabel;

	@FXML
	private Label locationLabel;

	@FXML
	private Label cipherLabel;

	@FXML
	private Label hashLabel;

	@FXML
	private Label pbkdfRoundsLabel;

	@FXML
	public void initialize() {
		dbNameLabel.textProperty().bind(general.getNameProperty());
		locationLabel.textProperty().bind(general.getPathProperty());
		cipherLabel.textProperty().bind(encryption.getCipherAlgorithmProperty());
		hashLabel.textProperty().bind(encryption.getHashAlgorithmProperty());
		pbkdfRoundsLabel.textProperty().bind(Bindings.convert(encryption.getPbkdfRoundsProperty()));
	}

	@Override
	public boolean tryPrevious() {
		return true;
	}

	@Override
	public boolean tryNext() {
		return true;
	}
	
	@Override
	public String getTitle() {
		return "Review settings";
	}

}
