package com.keeptio.screens.newvault;

import java.io.IOException;

import com.keeptio.controllers.newvault.AuthScreenController;
import com.keeptio.models.VaultAuthConfig;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class AuthScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private AuthScreenController controller;
	
	public AuthScreen(VaultAuthConfig model) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newvault/ChildScreenAuth.fxml"));
			controller = new AuthScreenController(model);
			loader.setController(controller);
			content = loader.load();
			content.getStylesheets().add(getClass().getResource("/styles/auth.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
