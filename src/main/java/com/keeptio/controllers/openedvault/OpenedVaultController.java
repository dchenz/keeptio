package com.keeptio.controllers.openedvault;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.keeptio.components.ConfirmPrompt;
import com.keeptio.components.NavigationTreeCell;
import com.keeptio.controllers.Controller;
import com.keeptio.entities.Displayable;
import com.keeptio.entities.EncryptedSecret;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.screens.HomeScreen;
import com.keeptio.screens.openedvault.EncryptedPromptScreen;
import com.keeptio.screens.openedvault.NewGroupScreen;
import com.keeptio.screens.openedvault.NewSecretScreen;
import com.keeptio.screens.openedvault.OpenedGroupScreen;
import com.keeptio.screens.openedvault.OpenedSecretScreen;
import com.keeptio.screens.openedvault.SettingsScreen;
import com.keeptio.util.Logging;
import com.keeptio.util.PaneHelper;
import com.keeptio.util.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class OpenedVaultController implements Controller {

	private final Vault vault;

	private PaneHelper paneHelper;

	public OpenedVaultController(Vault v) {
		if (v == null) {
			throw new IllegalArgumentException("Vault cannot be NULL");
		}
		vault = v;
		StageHelper.setTitle(vault.getName());
	}

	private Node currentOpenModal;

	private HashMap<UUID, TreeItem<Displayable>> groupTreeIndex;

	private Group lastSelectedGroup;

	@FXML
	private StackPane windowWrapper;

	@FXML
	private BorderPane mainWindow;

	@FXML
	private TreeView<Displayable> itemTreeView;

	@FXML
	private StackPane contentView;

	@FXML
	private Button homeToolbarButton;
	
	@FXML
	private Button newEntryToolbarButton;

	@FXML
	private Button newGroupToolbarButton;
	
	@FXML
	private Button settingsToolbarButton;

	@FXML
	public void initialize() {

		paneHelper = new PaneHelper(contentView);
		
		// Set event handlers for tool-bar buttons
		homeToolbarButton.setOnAction(this::handleHomeButtonClick);
		newEntryToolbarButton.setOnAction(this::handleNewEntryButtonClick);
		newGroupToolbarButton.setOnAction(this::handleNewGroupButtonClick);
		settingsToolbarButton.setOnAction(this::handleSettingsButtonClick);

		renderTreeView();

		itemTreeView.setShowRoot(false);
		itemTreeView.setCellFactory(x -> new NavigationTreeCell());

		// Listen for clicks on the TreeView so the center VBox can update with content
		itemTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			Displayable item = newValue == null ? null : newValue.getValue();
			if (item == null) {
				// Nothing is selected, display empty view (null)
				showGroupSelectedView(null);
			} else if (item.getClass().equals(Group.class)) {
				// Group is selected, display list of member secrets
				showGroupSelectedView((Group) item);
			} else {
				// Secret is selected, display secret or prompt for password
				showSecretSelectedView((Secret) item, false);
			}
		});

	}

	private void showSecretSelectedView(Secret secret, boolean ignoreEncryption) {
		Runnable closeAndShowGroup = () -> {
			if (secret.getGroupId() == null) {
				// Secret does not have a group, show empty view
				showGroupSelectedView(lastSelectedGroup);
			} else {
				// Secret has a group, show list of member secrets
				Group g = vault.findGroup(secret.getGroupId());
				showGroupSelectedView(g);
			}
		};
		if (!ignoreEncryption && secret.isEncrypted()) {
			EncryptedSecret encrypted = (EncryptedSecret) secret;
			// Encrypted secret requires prompt for password, continues to show secret if
			// successfully decrypted
			EncryptedPromptScreen sc = new EncryptedPromptScreen(encrypted);
			// Show secret after it is decrypted
			sc.getController().setOnSuccess((decrypted) -> showSecretSelectedView(decrypted, false));
			// Select the group to show upon clicking Cancel
			sc.getController().setOnCancel(closeAndShowGroup);
			// Show the secret in encrypted form (don't decrypt) if user chooses to view
			sc.getController().setOnView(() -> showSecretSelectedView(encrypted, true));
			// Display prompt for password
			paneHelper.changeNode(sc);	
		} else {
			// Secret is shown on the view, does not require password
			OpenedSecretScreen sc = new OpenedSecretScreen(vault, secret);
			// Select the group to show upon clicking Cancel
			sc.getController().setOnClose(closeAndShowGroup);
			// Set the confirmation pop-up for deletion
			sc.getController().setPrompt(new ConfirmPrompt(windowWrapper));
			// Display the secret's information
			paneHelper.changeNode(sc);
		}
	}

	private void showGroupSelectedView(Group group) {
		if (group == null) {
			// No group is selected, show empty view
			paneHelper.removeNode();
			// Select the default group (root tree item)
			itemTreeView.getSelectionModel().select(groupTreeIndex.get(null));
		} else {
			// Get secret objects using IDs
			List<Secret> memberSecrets = group.getMembers().stream().map(x -> vault.findSecret(x)).toList();
			// Group is selected, show list of member secrets on the view
			OpenedGroupScreen sc = new OpenedGroupScreen(group.getName(), memberSecrets);
			// When the user selects a secret, it should be opened
			sc.getController().setOnSelect((secret) -> showSecretSelectedView(secret, false));
			paneHelper.changeNode(sc);
			// Group becomes selected on the TreeView using its ID
			itemTreeView.getSelectionModel().select(groupTreeIndex.get(group.getId()));
		}
		lastSelectedGroup = group;
	}

	private void renderTreeView() {
		
		// Display the tree view of secrets and groups
		TreeItem<Displayable> treeRoot = new TreeItem<>();
		treeRoot.setExpanded(true);
		itemTreeView.setRoot(treeRoot);

		// Map the group's UUID to the TreeItem for easy access
		groupTreeIndex = new HashMap<>();

		// NULL maps to root for unassigned secrets (Secret.getGroupId() == NULL)
		groupTreeIndex.put(null, treeRoot);

		// Add every group to the tree first + add to HashMap
		for (Group g : vault.getGroups()) {
			createGroupTreeItem(g);
		}

		// Add every secret to groups, if they are assigned to one.
		for (Secret s : vault.getSecrets()) {
			createSecretTreeItem(s);
		}
		
		vault.secretsProperty().addListener(new ListChangeListener<>() {

			@Override
			public void onChanged(Change<? extends Secret> ch) {
				while (ch.next()) {
					if (ch.wasAdded()) {
						for (int i = ch.getFrom(); i < ch.getTo(); i++) {
							Logging.debug("Secret added at index: " + i);
							createSecretTreeItem(ch.getList().get(i));
						}
					} else if (ch.wasRemoved()) {
						for (Secret s : ch.getRemoved()) {
							TreeItem<Displayable> groupItem = groupTreeIndex.get(s.getGroupId());
							for (TreeItem<Displayable> td : groupItem.getChildren()) {
								if (td.getValue().getId().equals(s.getId())) {
									Logging.debug("Secret removed: " + s.getName());
									groupItem.getChildren().remove(td);
									break;
								}
							}
						}
					}
				}
			}
			
		});
		
		vault.groupsProperty().addListener(new ListChangeListener<>() {

			@Override
			public void onChanged(Change<? extends Group> ch) {
				while (ch.next()) {
					if (ch.wasAdded()) {
						for (int i = ch.getFrom(); i < ch.getTo(); i++) {
							Logging.debug("Group added at index: " + i);
							createGroupTreeItem(ch.getList().get(i));
						}
					}
				}
			}
			
		});

	}

	private void createSecretTreeItem(Secret s) {
		TreeItem<Displayable> secretTreeItem = new TreeItem<>(s);
		// Should update its display value if secret's name is changed
		s.nameProperty().addListener((obs) -> {
			TreeModificationEvent<Displayable> event = new TreeModificationEvent<>(TreeItem.valueChangedEvent(),
					secretTreeItem);
			Event.fireEvent(secretTreeItem, event);
		});
		// Should move to another group's TreeItem if group membership is changed
		s.groupIdProperty().addListener((obs, oldValue, newValue) -> {
			groupTreeIndex.get(oldValue).getChildren().remove(secretTreeItem);
			groupTreeIndex.get(newValue).getChildren().add(secretTreeItem);
		});
		// Add to group's TreeItem using secret's parent group UUID
		groupTreeIndex.get(s.getGroupId()).getChildren().add(secretTreeItem);
		// TODO: Detect when a secret gets deleted from vault
	}
	
	private void createGroupTreeItem(Group g) {
		TreeItem<Displayable> groupTreeItem = new TreeItem<>(g);
		groupTreeIndex.put(g.getId(), groupTreeItem);
		itemTreeView.getRoot().getChildren().add(groupTreeItem);
	}
	
	private void handleHomeButtonClick(ActionEvent e) {
		StageHelper.changeScreen(new HomeScreen());
	}

	private void handleNewEntryButtonClick(ActionEvent e) {
		Consumer<Secret> createSecretAndClose = (secret) -> {
			if (secret == null) {
				// No secret created - cancel button
				showGroupSelectedView(lastSelectedGroup);
			} else if (secret.getGroupId() == null) {
				// Secret created - no group
				//createSecretTreeItem(secret);
				showGroupSelectedView(lastSelectedGroup);
			} else {
				// Secret created - has group
				//createSecretTreeItem(secret);
				showGroupSelectedView(vault.findGroup(secret.getGroupId()));
			}
			closeModal();
		};
		NewSecretScreen sc = new NewSecretScreen(vault);
		sc.getController().setOnClose(createSecretAndClose);
		openModal(sc.getContent());
	}

	private void handleNewGroupButtonClick(ActionEvent e) {
		Consumer<Group> createGroupAndClose = (group) -> {
			if (group == null) {
				// No group created - cancel button
				showGroupSelectedView(lastSelectedGroup);
			} else {
				// Group created
				//createGroupTreeItem(group);
				showGroupSelectedView(group);
			}
			closeModal();
		};
		NewGroupScreen sc = new NewGroupScreen(vault);
		sc.getController().setOnClose(createGroupAndClose);
		openModal(sc.getContent());
	}
	
	private void handleSettingsButtonClick(ActionEvent e) {
		SettingsScreen sc = new SettingsScreen();
		openModal(sc.getContent());
	}

	private void openModal(Node n) {
		windowWrapper.getChildren().add(n);
		currentOpenModal = n;
		mainWindow.setDisable(true);
		mainWindow.setVisible(false);
	}

	private void closeModal() {
		windowWrapper.getChildren().remove(currentOpenModal);
		currentOpenModal = null;
		mainWindow.setDisable(false);
		mainWindow.setVisible(true);
	}

}

