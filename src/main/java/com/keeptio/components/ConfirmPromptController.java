package com.keeptio.components;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ConfirmPromptController {
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private Button confirmButton;
	
	public void setOnSelect(Consumer<Boolean> onSelect) {
		cancelButton.setOnAction((e) -> {
			onSelect.accept(false);
		});
		confirmButton.setOnAction((e) -> {
			onSelect.accept(true);
		});
	}
	
}
