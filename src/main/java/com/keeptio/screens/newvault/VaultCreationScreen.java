package com.keeptio.screens.newvault;

import java.io.IOException;

import com.keeptio.controllers.newvault.VaultCreationController;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class VaultCreationScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private VaultCreationController controller;
	
	public VaultCreationScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newvault/VaultCreationScreen.fxml"));
			controller = new VaultCreationController();
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
