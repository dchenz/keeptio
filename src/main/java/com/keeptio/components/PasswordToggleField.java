package com.keeptio.components;

import javafx.beans.property.ReadOnlyProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class PasswordToggleField extends VBox {

	private PasswordToggleFieldController controller;

	public PasswordToggleField() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/keeptio/components/PasswordToggleField.fxml"));
		controller = new PasswordToggleFieldController();
		loader.setController(controller);
		try {
			getChildren().add(loader.load());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getText() {
		return controller.textProperty().getValue();
	}

	public void setText(String text) {
		controller.textProperty().setValue(text);
	}
	
	public ReadOnlyProperty<String> textProperty() {
		return controller.textProperty();
	}
	
	public boolean getToggle() {
		return controller.toggleProperty().getValue();
	}
	
	public void setToggle(boolean toggle) {
		controller.toggleProperty().setValue(toggle);
	}

	public ReadOnlyProperty<Boolean> toggleProperty() {
		return controller.toggleProperty();
	}
	
}
