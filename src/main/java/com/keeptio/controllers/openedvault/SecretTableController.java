package com.keeptio.controllers.openedvault;

import java.util.List;
import java.util.function.Consumer;

import com.keeptio.controllers.Controller;
import com.keeptio.entities.Secret;

import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Setter;

public class SecretTableController implements Controller {

	private String groupName;

	private List<Secret> members;

	@Setter
	private Consumer<Secret> onSelect;

	public SecretTableController(String groupName, List<Secret> members) {
		this.groupName = groupName;
		this.members = members;
		initialize();
	}

	private TableView<Secret> itemView;

	// Return a reference to TableView since this screen renders using code
	// instead of using an FXML file
	public Parent initialize() {
		// Set column names to be attributes in Secret
		itemView = new TableView<>();
		// Make columns take up all width
		itemView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Column 1: Name of secret
		TableColumn<Secret, String> tc1 = new TableColumn<>("Name");
		tc1.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Column 2: Creation time-stamp
		TableColumn<Secret, String> tc2 = new TableColumn<>("Created on");
		tc2.setCellValueFactory(new PropertyValueFactory<>("createdTimestamp"));

		TableColumn<Secret, String> joinedColumn = new TableColumn<>(groupName);
		joinedColumn.getColumns().add(tc1);
		joinedColumn.getColumns().add(tc2);
		itemView.getColumns().add(joinedColumn);

		// Allow selection of table rows to open secrets
		itemView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				onSelect.accept(newValue);
			}
		});

		// Add all secrets belong to a group
		for (Secret s : members) {
			itemView.getItems().add(s);
		}

		// By default, sort rows by ascending order of name and time-stamp
		tc1.setSortType(TableColumn.SortType.ASCENDING);
		tc2.setSortType(TableColumn.SortType.ASCENDING);
		itemView.getSortOrder().add(tc1);
		itemView.getSortOrder().add(tc2);
		itemView.sort();

		return itemView;
	}

}
