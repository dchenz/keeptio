package com.keeptio.controllers.newvault;

import java.util.List;

import com.keeptio.components.PasswordToggleField;
import com.keeptio.controllers.WizardController;
import com.keeptio.models.VaultAuthConfig;
import com.keeptio.util.ErrorDecorator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class AuthScreenController implements WizardController {
	
	private VaultAuthConfig model;

	public AuthScreenController(VaultAuthConfig model) {
		this.model = model;
	}

	@FXML
	private PasswordToggleField pwField;

	@FXML
	private PasswordField pwConfirmField;

	@FXML
	private Label pwRuleLength;

	@FXML
	private Label pwRuleLowercase;

	@FXML
	private Label pwRuleUppercase;

	@FXML
	private Label pwRuleNumbers;

	@FXML
	private Label pwRuleSymbols;

	@FXML
	public void initialize() {
		
		model.getPasswordProperty().bind(pwField.textProperty());
		
		// Remove password rule error if it's applied
		pwField.textProperty().addListener((obs, oldValue, newValue) -> {
			ErrorDecorator.remove(pwField);
			ErrorDecorator.remove(pwConfirmField);
			updatePasswordRuleStatuses(newValue);
		});

		// Remove matching password error if it's applied
		pwConfirmField.textProperty().addListener((e) -> {
			ErrorDecorator.remove(pwConfirmField);
		});

		// User data is authoritative for "condition met"
		pwRuleLength.setUserData(Boolean.FALSE);
		pwRuleLowercase.setUserData(Boolean.FALSE);
		pwRuleUppercase.setUserData(Boolean.FALSE);
		pwRuleNumbers.setUserData(Boolean.FALSE);
		pwRuleSymbols.setUserData(Boolean.FALSE);

	}

	private void updatePasswordRuleStatuses(String pw) {

		setRuleStatus(pwRuleLength, pw.length() >= 8);

		setRuleStatus(pwRuleLowercase, pw.matches(".*[a-z].*"));

		setRuleStatus(pwRuleUppercase, pw.matches(".*[A-Z].*"));

		setRuleStatus(pwRuleNumbers, pw.matches(".*\\d.*"));

		// TODO: Re-factor this section

		boolean found = false;
		for (char c : pw.toCharArray()) {
			if (c >= 32 && c <= 126) {
				if (!(c >= 'a' && c <= 'z')) {
					if (!(c >= 'A' && c <= 'Z')) {
						if (!(c >= '0' && c <= '9')) {
							found = true;
						}
					}
				}
			}
		}

		setRuleStatus(pwRuleSymbols, found);

	}

	private void setRuleStatus(Label lbl, boolean isSatisfied) {
		List<String> cls = lbl.getStyleClass();
		// Remove all status classes, then re-apply
		cls.removeIf((c) -> c.startsWith("pw-rule-status-"));
		if (isSatisfied) {
			cls.add("pw-rule-status-success");
			lbl.setUserData(Boolean.TRUE);
		} else {
			cls.add("pw-rule-status-fail");
			lbl.setUserData(Boolean.FALSE);
		}
	}

	@Override
	public boolean tryPrevious() {
		return true;
	}

	@Override
	public boolean tryNext() {
		String pw = pwField.getText();
		if (!pw.equals(pwConfirmField.getText())) {
			ErrorDecorator.apply(pwConfirmField, "Passwords do not match.");
			return false;
		}
		if (!(Boolean) pwRuleLength.getUserData()) {
			ErrorDecorator.apply(pwField, "Password does not meet requirements: " + pwRuleLength.getText());
			return false;
		}
		List<Boolean> conditions = List.of(
			(Boolean) pwRuleLowercase.getUserData(),
			(Boolean) pwRuleUppercase.getUserData(), 
			(Boolean) pwRuleNumbers.getUserData(),
			(Boolean) pwRuleSymbols.getUserData()
		);
		// Count true values
		long nSatisfied = conditions.stream()
			.filter(x -> x == true)
			.count();
		if (nSatisfied < 3) {
			ErrorDecorator.apply(pwField, "Password does not meet requirements: Must satisfy at least 3 rules");
			return false;
		} 
		return true;
	}

	@Override
	public String getTitle() {
		return "Authentication";
	}

}
