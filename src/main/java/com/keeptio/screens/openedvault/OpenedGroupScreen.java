package com.keeptio.screens.openedvault;

import java.util.List;

import com.keeptio.controllers.openedvault.SecretTableController;
import com.keeptio.entities.Secret;
import com.keeptio.screens.Screen;

import javafx.scene.Parent;
import lombok.Getter;

public class OpenedGroupScreen implements Screen {

	@Getter
	private Parent content;

	@Getter
	private SecretTableController controller;
	
	public OpenedGroupScreen(String groupName, List<Secret> members) {
		controller = new SecretTableController(groupName, members);
		content = controller.initialize();
	}
	
}
