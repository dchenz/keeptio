package com.keeptio.models;

import com.keeptio.util.Logging;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter

public class VaultAuthConfig {

	private StringProperty passwordProperty;
	
	public VaultAuthConfig() {
		passwordProperty = new SimpleStringProperty();
		Logging.addPropertyLogging("Password", passwordProperty);
	}
	
	public String getPassword() {
		return passwordProperty.getValue();
	}
	
}
