package com.keeptio.controllers.openedvault;

import com.keeptio.components.ConfirmPrompt;
import com.keeptio.components.GroupListCell;
import com.keeptio.components.PasswordToggleField;
import com.keeptio.controllers.Controller;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.util.ErrorDecorator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Setter;

public class OpenedSecretController implements Controller {
	
	private Vault vault;
	
	private Secret openedSecret;
	
	@Setter
	private Runnable onClose;
	
	@Setter
	private ConfirmPrompt prompt;
	
	public OpenedSecretController(Vault vault, Secret secret) {
		this.vault = vault;
		openedSecret = secret;
	}
	
	@FXML
	private Label idLabel;
	
	@FXML
	private Label createdTsLabel;
	
	@FXML
	private TextField secretDisplayNameField;

	@FXML
	private ComboBox<Group> secretGroupDropdown;
	
	@FXML
	private PasswordToggleField pwField;
	
	@FXML
	private TextArea secretDescriptionField;

	@FXML
	private Button saveButton;

	@FXML
	private Button cancelButton;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	public void initialize() {
		
		// Populate initial values for Secret
		idLabel.setText(openedSecret.getId().toString());
		createdTsLabel.setText(openedSecret.getCreatedTimestamp());
		secretDisplayNameField.setText(openedSecret.getName());
		pwField.setText(openedSecret.getValue());
		secretDescriptionField.setText(openedSecret.getDescription());
		
		// If the secret is still encrypted, disable the fields 
		// and clear the encrypted values to show as empty
		if (openedSecret.isEncrypted()) {
			pwField.setText("");
			pwField.setDisable(true);
			secretDescriptionField.setText("");
			secretDescriptionField.setDisable(true);
		} else {
			pwField.textProperty().addListener((obs) -> {
				ErrorDecorator.remove(pwField);
			});
		}
		
		secretGroupDropdown.setButtonCell(new GroupListCell());
		secretGroupDropdown.setCellFactory(x -> new GroupListCell());
		
		// Populate ChoiceBox drop-down with names of groups	
		secretGroupDropdown.getItems().add(null);
		secretGroupDropdown.getItems().addAll(vault.getGroups());
		
		if (openedSecret.getGroupId() != null) {
			Group parentGroup = vault.findGroup(openedSecret.getGroupId());
			secretGroupDropdown.getSelectionModel().select(parentGroup);	
		}
		
		saveButton.setOnAction(this::handleSaveButtonClick);
		cancelButton.setOnAction((e) -> onClose.run());
		deleteButton.setOnAction((e) -> {
			prompt.show((isConfirmed) -> {
				if (isConfirmed) {
					handleDeleteButtonClick();
				} else {
					onClose.run();
				}
			});
			
		});
		
		// Delete button shows on the left while others show on the right
		ButtonBar.setButtonData(deleteButton, ButtonData.LEFT);
	}
	
	private void handleSaveButtonClick(ActionEvent e) {
		String name = secretDisplayNameField.getText();
		String description = secretDescriptionField.getText();
		String value = pwField.getText();
		Group selectedGroup = secretGroupDropdown.getValue();
		// Encrypted values should not change
		if (openedSecret.isEncrypted()) {
			value = openedSecret.getValue();
			description = openedSecret.getDescription();
		}
		// Only whitespace is not accepted for display name but valid for secret value
		if (name.isBlank()) {
			ErrorDecorator.apply(secretDisplayNameField, "This field is required.");
		} else if (!openedSecret.isEncrypted() && value.isEmpty()) {
			ErrorDecorator.apply(pwField, "This field is required.");
		} else {
			openedSecret.setName(name);
			openedSecret.setValue(value);
			openedSecret.setDescription(description);
			if (selectedGroup == null) {
				// If the group is NULL, there is no group
				vault.removeSecretFromGroup(openedSecret);	
			} else if (!selectedGroup.getId().equals(openedSecret.getGroupId())) {
				// If the group has changed from the current selected group
				vault.removeSecretFromGroup(openedSecret);	
				vault.addSecretToGroup(openedSecret, selectedGroup);	
			}
			vault.saveChanges();
			onClose.run();
		}
	}
	
	private void handleDeleteButtonClick() {
		// Delete secret
		vault.deleteSecret(openedSecret.getId());
		// Write changes to disk
		vault.saveChanges();
		// Close the pane
		onClose.run();
	}
	
}
