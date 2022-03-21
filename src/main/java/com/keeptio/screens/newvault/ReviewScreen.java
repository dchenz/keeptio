package com.keeptio.screens.newvault;

import java.io.IOException;

import com.keeptio.controllers.newvault.ReviewScreenController;
import com.keeptio.models.NewVaultConfig;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class ReviewScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private ReviewScreenController controller;
	
	public ReviewScreen(NewVaultConfig config) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newvault/ChildScreenReview.fxml"));
			controller = new ReviewScreenController(config.getGeneral(), config.getEncryption());
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
