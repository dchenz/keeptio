package com.keeptio.controllers.newvault;

import java.io.File;

import com.keeptio.controllers.WizardController;
import com.keeptio.models.VaultGeneralConfig;
import com.keeptio.util.ErrorDecorator;
import com.keeptio.util.FileUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class GeneralScreenController implements WizardController {

	private VaultGeneralConfig model;
	
	public GeneralScreenController(VaultGeneralConfig model) {
		this.model = model;
	}

	private File selectedFile;

	@FXML
	private TextField dbNameField;

	@FXML
	private Label savePathField;

	@FXML
	private Button browseDirectoryButton;

	@FXML
	public void initialize() {
		model.getNameProperty().bind(dbNameField.textProperty());
		model.getPathProperty().bind(savePathField.textProperty());
		// Remove error if it exists when the user types something
		dbNameField.textProperty().addListener((e) -> {
			ErrorDecorator.remove(dbNameField);
		});
		// Set initial file path
		String initDir = System.getProperty("user.home");
		selectedFile = FileUtils.getNonExistantFile(initDir, "Secrets", VaultGeneralConfig.FILE_EXT);
		savePathField.setText(selectedFile.getAbsolutePath());
		// Set event handlers
		browseDirectoryButton.setOnAction(this::handleBrowseDirectoryButtonClick);
	}

	/**
	 * Allow the user to select a directory to save their vault database. Initial
	 * path is set to user's home directory.
	 */
	private void handleBrowseDirectoryButtonClick(ActionEvent e) {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(selectedFile.getParentFile());
		chooser.setInitialFileName(selectedFile.getName());
		chooser.getExtensionFilters().add(new ExtensionFilter("Keeptio Vault", "*" + VaultGeneralConfig.FILE_EXT));
		File selected = chooser.showSaveDialog(browseDirectoryButton.getScene().getWindow());
		if (selected != null) {
			selectedFile = selected;
			if (selectedFile.exists()) {
				ErrorDecorator.apply(savePathField, "Warning: File will be overwritten!");
			} else {
				ErrorDecorator.remove(savePathField);
			}
		}
		savePathField.setText(selectedFile.getAbsolutePath());
	}

	@Override
	public boolean tryPrevious() {
		return false;
	}

	@Override
	public boolean tryNext() {
		if (dbNameField.getText().isBlank()) {
			// Generate error on text field
			ErrorDecorator.apply(dbNameField, "This field is required.");
			return false;
		} 
		return true;
	}

	@Override
	public String getTitle() {
		return "General";
	}

}
