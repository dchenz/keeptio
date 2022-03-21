package com.keeptio;

import com.keeptio.screens.HomeScreen;
import com.keeptio.util.StageHelper;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			Image icon = new Image(getClass().getResource("/media/icon.png").toExternalForm());
			primaryStage.getIcons().add(icon);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Keeptio Secrets Manager");
			
			StageHelper.setStage(primaryStage);
			StageHelper.changeScreen(new HomeScreen());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
