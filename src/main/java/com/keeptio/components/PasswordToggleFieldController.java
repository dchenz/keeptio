package com.keeptio.components;

import org.kordamp.ikonli.javafx.FontIcon;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

class PasswordToggleFieldController {
	
	@FXML
	private TextField pwVisibleField;
	
	@FXML
	private PasswordField pwHiddenField;

	@FXML
	private ToggleButton toggleButton;
	
	@FXML
	public void initialize() {
		pwVisibleField.textProperty().bindBidirectional(pwHiddenField.textProperty());
		toggleButton.setOnAction((e) -> toggle());
		toggle();
	}
	
	private void toggle() {
		if (toggleButton.isSelected()) {
			// Show secret
			pwVisibleField.toFront();
			toggleButton.setGraphic(new FontIcon("bi-eye-slash-fill"));
		} else {
			// Hide secret
			pwHiddenField.toFront();
			toggleButton.setGraphic(new FontIcon("bi-eye"));
		}
	}
	
	StringProperty textProperty() {
		return pwHiddenField.textProperty();
	}
	
	BooleanProperty toggleProperty() {
		return toggleButton.selectedProperty();
	}
	
}
