package com.keeptio.controllers.openedvault;

import java.util.List;
import java.util.function.Consumer;

import com.keeptio.components.SecretListCell;
import com.keeptio.controllers.Controller;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.util.ErrorDecorator;
import com.keeptio.util.Logging;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import lombok.Setter;

public class NewGroupModalController implements Controller {
	
	private final Vault vault;

	@Setter
	private Consumer<Group> onClose;

	public NewGroupModalController(Vault vault) {
		this.vault = vault;
	}

	@FXML
	private TextField groupDisplayNameField;

	@FXML
	private ColorPicker groupColorPicker;

	@FXML
	private ListView<Secret> memberListView;

	@FXML
	private Button createButton;

	@FXML
	private Button cancelButton;

	@FXML
	public void initialize() {
	
		memberListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		
		
		memberListView.setCellFactory(x -> new SecretListCell());
		
		memberListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<> () {

			@Override
			public void onChanged(Change<? extends Secret> arg0) {
				for (Secret s : arg0.getList()) {
					Logging.debug("> " + s.getName());
				}
			}


		});
		
		List<Secret> secretsWithoutGroups = vault.getSecrets().stream()
			.filter(x -> x.getGroupId() == null)
			.toList();
		memberListView.getItems().addAll(secretsWithoutGroups);
		
		// Set event handlers for buttons
		createButton.setOnAction(this::handleCreateButtonClick);
		cancelButton.setOnAction((e) -> onClose.accept(null));
	}
	
	private void handleCreateButtonClick(ActionEvent e) {
		String name = groupDisplayNameField.getText();
		List<Secret> members = memberListView.getSelectionModel().getSelectedItems();
		// Only whitespace is not accepted for display name but valid for secret value
		if (name.isBlank()) {
			ErrorDecorator.apply(groupDisplayNameField, "This field is required.");
		}  else {
			Group group = vault.createGroup(name);
			onClose.accept(group);
			for (Secret member : members) {
				vault.addSecretToGroup(member, group);
			}
			vault.saveChanges();
		}
	}

}
