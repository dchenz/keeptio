package com.keeptio.util;

import com.keeptio.screens.Screen;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class PaneHelper {

	private Pane parent;
	
	private Node current;
	
	public PaneHelper(Pane targetNode) {
		if (targetNode == null) {
			throw new RuntimeException("Pane cannot be NULL");
		}
		this.parent = targetNode;
	}
	
	public void changeNode(Screen screen) {
		removeNode();
		current = screen.getContent();
		parent.getChildren().add(current);
	}
	
	public void removeNode() {
		if (current != null) {
			parent.getChildren().remove(current);	
		}
	}
	
}
