package com.keeptio.screens;

import java.io.File;
import java.io.IOException;

import com.keeptio.controllers.AuthPromptController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class AuthPromptScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private AuthPromptController controller;
	
	public AuthPromptScreen(File selectedFile) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VaultAuthPromptScreen.fxml"));
			controller = new AuthPromptController(selectedFile);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
