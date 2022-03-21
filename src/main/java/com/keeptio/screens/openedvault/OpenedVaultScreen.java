package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.openedvault.OpenedVaultController;
import com.keeptio.entities.Vault;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class OpenedVaultScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private OpenedVaultController controller;
	
	public OpenedVaultScreen(Vault v) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/OpenedVaultScreen.fxml"));
			controller = new OpenedVaultController(v);
			loader.setController(controller);
			content = loader.load();
			content.getStylesheets().add(getClass().getResource("/styles/vault.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
