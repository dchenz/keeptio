package com.keeptio.util;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ErrorDecorator {

	private static final String ERROR_ELEMENT_ID = "error-message";

	/**
	 * Creates an error message and applies it to the Node. If there is already
	 * an error, then it will be updated with the new message.
	 * 
	 * @param field   - Node to show error under
	 * @param message - Error message to display to user
	 */
	public static void apply(Node node, String message) {
		Pane parent = (Pane) node.getParent();
		Label errorLabel = getErrorLabel(parent);
		if (errorLabel == null) {
			errorLabel = createErrorLabel(parent.getClass());
			parent.getChildren().add(errorLabel);
		}
		errorLabel.setText(message);
	}

	/**
	 * Removes the error message from Node, if it exists.
	 * 
	 * @param field - Node to remove message from
	 */
	public static void remove(Node node) {
		Pane parent = (Pane) node.getParent();
		parent.getChildren().remove(getErrorLabel(parent));
	}

	/**
	 * Get the Label containing error message, if it exists, else NULL.
	 * 
	 * @param fieldSection - Immediate parent element of TextField
	 * @return Label
	 */
	private static Label getErrorLabel(Pane parent) {
		return (Label) parent.lookup("#" + ERROR_ELEMENT_ID);
	}

	/**
	 * Create an empty styled Label to hold an error message.
	 * 
	 * @return Label
	 */
	private static Label createErrorLabel(Class<? extends Pane> parentType) {
		Label lbl = new Label();
		lbl.setId(ERROR_ELEMENT_ID);
		lbl.getStyleClass().add("kpt-error-message");
		if (parentType.equals(HBox.class)) {
			lbl.setPadding(new Insets(0, 0, 0, 15));
		}
		return lbl;
	}

}
