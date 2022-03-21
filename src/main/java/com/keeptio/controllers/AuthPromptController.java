package com.keeptio.controllers;

import java.io.File;

import com.keeptio.components.PasswordToggleField;
import com.keeptio.entities.Vault;
import com.keeptio.screens.HomeScreen;
import com.keeptio.screens.openedvault.OpenedVaultScreen;
import com.keeptio.services.VaultService;
import com.keeptio.util.ErrorDecorator;
import com.keeptio.util.StageHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AuthPromptController implements Controller {

	private String path;
	
	public AuthPromptController(File vaultFile) {
		if (vaultFile == null) {
			throw new IllegalArgumentException("Vault file cannot be NULL");
		}
		this.path = vaultFile.getAbsolutePath();
	}
	
	@FXML
	private Label vaultPathLabel;
	
	@FXML
	private PasswordToggleField pwField;
	
	@FXML
	private Button backButton;
	
	@FXML
	private Button submitButton;
	
	@FXML
	public void initialize() {
		vaultPathLabel.setText(path);
		pwField.textProperty().addListener((obs) -> {
			ErrorDecorator.remove(pwField);
		});
		backButton.setOnAction(this::handleBackButtonClick);
		submitButton.setOnAction(this::handleSubmitButtonClick);
	}
	
	private void handleBackButtonClick(ActionEvent e) {
		StageHelper.changeScreen(new HomeScreen());
	}
	
	private void handleSubmitButtonClick(ActionEvent e) {
		if (pwField.getText().isEmpty()) {
			ErrorDecorator.apply(pwField, "This field is required.");
		} else {
			VaultService vService = new VaultService();
			Vault fetchedVault = vService.read(path, pwField.getText());	
			if (fetchedVault == null) {
				// Failed to open
				ErrorDecorator.apply(pwField, "Incorrect password.");
			} else {
				// Success
				StageHelper.changeScreen(new OpenedVaultScreen(fetchedVault));
			}
		}
	}
	
}
