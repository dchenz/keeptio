package com.keeptio.screens.openedvault;

import java.io.IOException;

import com.keeptio.controllers.Controller;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class SettingsScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private Controller controller;

	public SettingsScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/openedvault/SettingsScreen.fxml"));
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
