package com.keeptio.controllers.openedvault;

import java.util.function.Consumer;

import com.keeptio.components.PasswordToggleField;
import com.keeptio.controllers.Controller;
import com.keeptio.entities.EncryptedSecret;
import com.keeptio.entities.Secret;
import com.keeptio.util.ErrorDecorator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import lombok.Setter;

public class EncryptedPromptController implements Controller {

	private EncryptedSecret secret;

	@Setter
	private Runnable onView;
	
	@Setter
	private Consumer<Secret> onSuccess;

	@Setter
	private Runnable onCancel;

	public EncryptedPromptController(EncryptedSecret secret) {
		if (secret == null) {
			throw new IllegalArgumentException("Encrypted secret cannot be NULL");
		}
		this.secret = secret;
	}

	@FXML
	private PasswordToggleField pwField;

	@FXML
	private Button viewButton;

	@FXML
	private Button cancelButton;

	@FXML
	private Button submitButton;

	@FXML
	public void initialize() {
		pwField.textProperty().addListener((obs) -> {
			ErrorDecorator.remove(pwField);
		});
		viewButton.setOnAction((e) -> onView.run());
		cancelButton.setOnAction((e) -> onCancel.run());
		submitButton.setOnAction(this::handleSubmitButtonClick);
		// Display view button on the left while others stay on the right
		ButtonBar.setButtonData(viewButton, ButtonData.LEFT);
	}

	private void handleSubmitButtonClick(ActionEvent e) {
		if (pwField.getText().isEmpty()) {
			ErrorDecorator.apply(pwField, "This field is required.");
		} else {
			Secret retrieved = secret.retrieve(pwField.getText());
			if (retrieved == null) {
				ErrorDecorator.apply(pwField, "Incorrect password.");
			} else {
				onSuccess.accept(retrieved);
			}
		}
	}

}
