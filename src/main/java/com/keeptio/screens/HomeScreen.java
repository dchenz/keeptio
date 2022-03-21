package com.keeptio.screens;

import java.io.IOException;

import com.keeptio.controllers.HomeController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class HomeScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private HomeController controller;
	
	public HomeScreen() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeScreen.fxml"));
			controller = new HomeController();
			loader.setController(controller);
			content = loader.load();
			content.getStylesheets().add(getClass().getResource("/styles/home.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
