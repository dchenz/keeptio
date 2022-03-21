package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.openedvault.EncryptedPromptController;
import com.keeptio.entities.EncryptedSecret;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class EncryptedPromptScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private EncryptedPromptController controller;
	
	public EncryptedPromptScreen(EncryptedSecret secret) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/EncryptedPromptScreen.fxml"));
			controller = new EncryptedPromptController(secret);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
