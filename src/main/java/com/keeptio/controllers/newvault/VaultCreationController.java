package com.keeptio.controllers.newvault;

import java.util.List;

import com.keeptio.controllers.Controller;
import com.keeptio.controllers.WizardController;
import com.keeptio.models.NewVaultConfig;
import com.keeptio.screens.HomeScreen;
import com.keeptio.screens.Screen;
import com.keeptio.screens.newvault.AuthScreen;
import com.keeptio.screens.newvault.EncryptionScreen;
import com.keeptio.screens.newvault.GeneralScreen;
import com.keeptio.screens.newvault.ReviewScreen;
import com.keeptio.screens.openedvault.OpenedVaultScreen;
import com.keeptio.services.VaultService;
import com.keeptio.util.PaneHelper;
import com.keeptio.util.StageHelper;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class VaultCreationController implements Controller {

	private PaneHelper paneHelper;

	private NewVaultConfig model;
	
	public VaultCreationController() {
		model = new NewVaultConfig();
	}

	// Sub-components used to create wizard screens
	
	private List<Screen> innerWizardScreens;
	
	private List<WizardController> innerWizardControllers;

	private IntegerProperty screenIndexProperty;

	// FXML components

	@FXML
	private ListView<String> sideNavList;

	@FXML
	private Pane configFormArea;

	@FXML
	private Button previousButton;

	@FXML
	private Button nextButton;

	@FXML
	private Button cancelButton;

	@FXML
	public void initialize() {

		paneHelper = new PaneHelper(configFormArea);

		// User cannot select ListView items themselves in the side navigation panel
		sideNavList.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				arg0.consume();
			}
		});

		previousButton.setOnAction(this::handlePreviousButtonClick);
		nextButton.setOnAction(this::handleNextButtonClick);
		cancelButton.setOnAction(this::handleCancelButtonClick);

		ButtonBar.setButtonData(cancelButton, ButtonData.CANCEL_CLOSE);
		ButtonBar.setButtonData(previousButton, ButtonData.BACK_PREVIOUS);
		ButtonBar.setButtonData(nextButton, ButtonData.NEXT_FORWARD);
		
		innerWizardScreens = List.of(
			new GeneralScreen(model.getGeneral()), 
			new AuthScreen(model.getAuth()),
			new EncryptionScreen(model.getEncryption()), 
			new ReviewScreen(model)
		);
		
		innerWizardControllers = innerWizardScreens.stream()
			.map(x -> (WizardController) x.getController())
			.toList();

		for (WizardController wc : innerWizardControllers) {
			sideNavList.getItems().add(wc.getTitle());
		}

		// Display first wizard page
		screenIndexProperty = new SimpleIntegerProperty(0);
		screenIndexProperty.addListener((obs, oldValue, newValue) -> handleScreenChange(newValue.intValue()));
		handleScreenChange(screenIndexProperty.intValue());
		
	}
	
	private void handleScreenChange(int i) {
		if (i == 0) {
			// Hide previous button if on 1st page
			previousButton.setVisible(false);	
		}
		if (i == 1) {
			// Show previous button if going away from 1st page
			previousButton.setVisible(true);
		}
		// Change back to a Next button when going back from last screen
		if (i == innerWizardScreens.size() - 2) {
			nextButton.getStyleClass().remove("primary");
			nextButton.setText("Next");
			nextButton.setOnAction(this::handleNextButtonClick);
		}
		// Change to a Submit button when going to last screen
		if (i == innerWizardScreens.size() - 1) {
			nextButton.getStyleClass().add("primary");
			nextButton.setText("Submit");
			nextButton.setOnAction(this::handleSubmitButtonClick);
		}
		// Change the selected item on ListView
		sideNavList.getSelectionModel().select(i);
		// Change screen's node that's currently rendered to pane
		paneHelper.changeNode(innerWizardScreens.get(i));
	}

	private void handlePreviousButtonClick(ActionEvent e) {
		int i = screenIndexProperty.getValue();
		boolean isScreenAbleToChange = innerWizardControllers.get(i).tryPrevious();
		if (isScreenAbleToChange) {	
			// Move screen number to previous screen
			screenIndexProperty.setValue(i - 1);
		}
	}

	private void handleNextButtonClick(ActionEvent e) {
		int i = screenIndexProperty.getValue();
		boolean isScreenAbleToChange = innerWizardControllers.get(i).tryNext();
		if (isScreenAbleToChange) {
			// Move screen number to next screen
			screenIndexProperty.setValue(i + 1);
		}
	}
	
	private void handleSubmitButtonClick(ActionEvent e) {
		VaultService vaultService = new VaultService();
		StageHelper.changeScreen(new OpenedVaultScreen(vaultService.create(model)));
	}

	private void handleCancelButtonClick(ActionEvent e) {
		StageHelper.changeScreen(new HomeScreen());
	}

}
