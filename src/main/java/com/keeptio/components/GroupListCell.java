package com.keeptio.components;

import com.keeptio.entities.Group;

import javafx.scene.control.ListCell;

public class GroupListCell extends ListCell<Group> {
	
	@Override
	public void updateItem(Group item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
		} else {
			setText(item.getName());
		}
	}

}
