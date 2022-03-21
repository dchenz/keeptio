package com.keeptio.controllers;

import java.io.File;

import com.keeptio.models.VaultGeneralConfig;
import com.keeptio.screens.AuthPromptScreen;
import com.keeptio.screens.newvault.VaultCreationScreen;
import com.keeptio.util.StageHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class HomeController implements Controller {
	
	private File selectedFile;
	
	@FXML
	private Button newVaultButton;
	
	@FXML
	private Button openVaultButton;
	
	@FXML
	public void initialize() {
		newVaultButton.setOnAction(this::handleNewVaultButtonClick);
		openVaultButton.setOnAction(this::handleOpenVaultButtonClick);
	}
	
	private void handleNewVaultButtonClick(ActionEvent e) {
		StageHelper.changeScreen(new VaultCreationScreen());
	}
	
	private void handleOpenVaultButtonClick(ActionEvent e) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File(System.getProperty("user.home")));
		chooser.getExtensionFilters().add(new ExtensionFilter("Keeptio Vault", "*" + VaultGeneralConfig.FILE_EXT));
		File selected = chooser.showOpenDialog(openVaultButton.getScene().getWindow());
		if (selected != null) {
			selectedFile = selected;
			if (selectedFile.isFile()) {
				StageHelper.changeScreen(new AuthPromptScreen(selectedFile));
			}
			// Display error for bad file selection
		}
	}
	
}
