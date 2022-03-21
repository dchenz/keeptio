package com.keeptio.components;

import com.keeptio.entities.Secret;

import javafx.scene.control.ListCell;

public class SecretListCell extends ListCell<Secret> {
	
	@Override
	public void updateItem(Secret item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
		} else {
			setText(item.getName());
		}
	}

}
