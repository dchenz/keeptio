package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.openedvault.OpenedSecretController;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class OpenedSecretScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private OpenedSecretController controller;
	
	public OpenedSecretScreen(Vault vault, Secret secret) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/OpenedSecret.fxml"));
			controller = new OpenedSecretController(vault, secret);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
