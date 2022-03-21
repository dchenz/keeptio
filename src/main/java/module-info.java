module keeptio {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires lombok;
	requires org.kordamp.ikonli.javafx;
	requires org.json;
	// requires org.kordamp.bootstrapfx.core;
	
	opens com.keeptio to javafx.graphics, javafx.fxml;
	opens com.keeptio.controllers to javafx.fxml;
	opens com.keeptio.controllers.newvault to javafx.fxml;
	opens com.keeptio.controllers.openedvault to javafx.fxml;
	opens com.keeptio.entities to javafx.base;
	opens com.keeptio.components to javafx.fxml;
}
