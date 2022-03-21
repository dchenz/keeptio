package com.keeptio.models;

import com.keeptio.util.Logging;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

@Getter

public class VaultGeneralConfig {

	public static final String FILE_EXT = ".kptx";
	
	private StringProperty nameProperty;
	
	private StringProperty pathProperty;
	
	public VaultGeneralConfig() {
		nameProperty = new SimpleStringProperty();
		pathProperty = new SimpleStringProperty();
		Logging.addPropertyLogging("Database Name", nameProperty);
		Logging.addPropertyLogging("Path", pathProperty);
	}
	
	public String getName() {
		return nameProperty.getValue();
	}
	
	public String getPath() {
		return pathProperty.getValue();
	}
	
}
