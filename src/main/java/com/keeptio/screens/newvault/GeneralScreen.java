package com.keeptio.screens.newvault;

import java.io.IOException;

import com.keeptio.controllers.newvault.GeneralScreenController;
import com.keeptio.models.VaultGeneralConfig;
import com.keeptio.screens.Screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.Getter;

public class GeneralScreen implements Screen {

	@Getter
	private Parent content;
	
	@Getter
	private GeneralScreenController controller;
	
	public GeneralScreen(VaultGeneralConfig model) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newvault/ChildScreenGeneral.fxml"));
			controller = new GeneralScreenController(model);
			loader.setController(controller);
			content = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
