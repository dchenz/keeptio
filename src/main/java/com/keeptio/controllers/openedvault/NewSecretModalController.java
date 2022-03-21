package com.keeptio.controllers.openedvault;

import java.util.function.Consumer;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Setter;

public class NewSecretModalController implements Controller {

	private final Vault vault;

	@Setter
	private Consumer<Secret> onClose;

	public NewSecretModalController(Vault vault) {
		this.vault = vault;
	}

	@FXML
	private VBox pwContainer;
	
	@FXML
	private PasswordToggleField pwField;
	
	@FXML
	private TextField secretDisplayNameField;

	@FXML
	private ComboBox<Group> secretGroupDropdown;

	@FXML
	private PasswordToggleField secretValueField;

	@FXML
	private CheckBox enablePwCheckbox;

	@FXML
	private TextArea secretDescriptionField;

	@FXML
	private Button createButton;

	@FXML
	private Button cancelButton;

	@FXML
	public void initialize() {
		// Clear errors if applied (only 1 errors shows at a time)
		secretDisplayNameField.textProperty().addListener((e) -> {
			ErrorDecorator.remove(secretDisplayNameField);
		});
		secretValueField.textProperty().addListener((e) -> {
			ErrorDecorator.remove(secretValueField);
		});

		secretGroupDropdown.setButtonCell(new GroupListCell());
		secretGroupDropdown.setCellFactory(x -> new GroupListCell());

		// Populate ChoiceBox drop-down with names of groups
		secretGroupDropdown.getItems().add(null);
		secretGroupDropdown.getItems().addAll(vault.getGroups());

		pwContainer.visibleProperty().bind(enablePwCheckbox.selectedProperty());
		enablePwCheckbox.selectedProperty().addListener((obs, oldValue, newValue) -> {
			pwField.setText("");
			pwField.setToggle(false);
		});
		
		// Set event handlers for buttons
		createButton.setOnAction(this::handleCreateButtonClick);
		cancelButton.setOnAction((e) -> onClose.accept(null));
	}

	private void handleCreateButtonClick(ActionEvent e) {
		String name = secretDisplayNameField.getText();
		String value = secretValueField.getText();
		String password = pwField.getText();
		String description = secretDescriptionField.getText();
		Group selectedGroup = secretGroupDropdown.getValue();
		// Password should already be cleared when check-box isn't selected
		if (!enablePwCheckbox.isSelected()) {
			password = "";
		}
		// Only whitespace is not accepted for display name but valid for secret value
		if (name.isBlank()) {
			ErrorDecorator.apply(secretDisplayNameField, "This field is required.");
		} else if (value.isEmpty()) {
			ErrorDecorator.apply(secretValueField, "This field is required.");
		} else {
			Secret s = vault.createSecret(name, value, description, password);
			if (selectedGroup != null) {
				vault.addSecretToGroup(s, selectedGroup);
			}
			vault.saveChanges();
			onClose.accept(s);
		}
	}
	
}
