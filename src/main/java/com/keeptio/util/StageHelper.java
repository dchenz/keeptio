package com.keeptio.util;

import com.keeptio.screens.Screen;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageHelper {

	private static Stage stage;
	
	public static void changeScreen(Screen screen) {
		if (stage == null) {
			throw new RuntimeException("Stage has not been set");
		}
		if (screen == null) {
			throw new IllegalArgumentException("Screen cannot be NULL");
		}
		Scene scene = new Scene(screen.getContent(), 1000, 600);
		scene.getStylesheets().add(StageHelper.class.getResource("/styles/bootstrap3.css").toExternalForm());
		scene.getStylesheets().add(StageHelper.class.getResource("/styles/global.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}
	
	public static void removeScreen() {
		if (stage == null) {
			throw new RuntimeException("Stage has not been set");
		}
		stage.setScene(null);
	}
	
	public static void setStage(Stage stage) {
		if (stage == null) {
			throw new IllegalArgumentException("Stage cannot be NULL");
		}
		StageHelper.stage = stage;
	}
	
	public static void setTitle(String name) {
		stage.setTitle(name);
	}
	
}
