package com.keeptio.components;

import com.keeptio.entities.Displayable;

import javafx.scene.control.TreeCell;

public class NavigationTreeCell extends TreeCell<Displayable> {

	@Override
	public void updateItem(Displayable item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
		} else {
			setText(item.getName());
		}
	}

}
