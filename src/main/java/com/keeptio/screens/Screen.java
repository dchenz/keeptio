package com.keeptio.screens;

import com.keeptio.controllers.Controller;

import javafx.scene.Parent;

public interface Screen {
	public Parent getContent();
	public Controller getController();
}
