package com.keeptio.screens.newvault;

import java.io.IOException;

import com.keeptio.controllers.newvault.EncryptionScreenController;
import com.keeptio.models.VaultEncryptionConfig;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class EncryptionScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private EncryptionScreenController controller;
	
	public EncryptionScreen(VaultEncryptionConfig model) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newvault/ChildScreenEncryption.fxml"));
			controller = new EncryptionScreenController(model);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
