package com.keeptio.components;

import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class ConfirmPrompt extends AnchorPane {
	
	private StackPane pane;
	
	private ConfirmPromptController controller;

	public ConfirmPrompt(StackPane pane) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/keeptio/components/ConfirmPrompt.fxml"));
		controller = new ConfirmPromptController();
		loader.setController(controller);
		try {
			getChildren().add(loader.load());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.pane = pane;
	}
	
	public void show(Consumer<Boolean> onSelect) {
		controller.setOnSelect((isConfirmed) -> {
			pane.getChildren().remove(this);
			onSelect.accept(isConfirmed);
		});
		pane.getChildren().add(this);
	}
	
}
