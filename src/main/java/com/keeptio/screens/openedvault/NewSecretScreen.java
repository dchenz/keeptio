package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.openedvault.NewSecretModalController;
import com.keeptio.entities.Vault;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class NewSecretScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private NewSecretModalController controller;
	
	public NewSecretScreen(Vault vault) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/NewSecretModal.fxml"));
			controller = new NewSecretModalController(vault);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
