package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.openedvault.NewGroupModalController;
import com.keeptio.entities.Vault;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class NewGroupScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private NewGroupModalController controller;
	
	public NewGroupScreen(Vault vault) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/NewGroupModal.fxml"));
			controller = new NewGroupModalController(vault);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
