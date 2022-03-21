package com.keeptio.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import com.keeptio.util.Logging;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Secret implements StringSerializable, Displayable {

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private UUID id;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String createdTimestamp;

	private ReadOnlyObjectWrapper<String> nameProperty;

	private ReadOnlyObjectWrapper<String> valueProperty;

	private ReadOnlyObjectWrapper<String> descriptionProperty;

	private ReadOnlyObjectWrapper<UUID> groupIdProperty;

	protected Secret(String name, String value, String description) {
		id = UUID.randomUUID();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		createdTimestamp = df.format(new Date());
		this.nameProperty = new ReadOnlyObjectWrapper<>(name);
		this.valueProperty = new ReadOnlyObjectWrapper<>(value);
		this.descriptionProperty = new ReadOnlyObjectWrapper<>(description);
		this.groupIdProperty = new ReadOnlyObjectWrapper<>();
		Logging.addPropertyLogging("Secret Name", nameProperty);
		Logging.addPropertyLogging("Secret Value", valueProperty);
		Logging.addPropertyLogging("Description", descriptionProperty);
		Logging.addPropertyLogging("Parent Group ID", groupIdProperty);
	}
	
	public boolean isEncrypted() {
		return false;
	}

	// Getters

	public String getName() {
		return nameProperty.getValue();
	}

	public String getValue() {
		return valueProperty.getValue();
	}

	public String getDescription() {
		return descriptionProperty.getValue();
	}

	public UUID getGroupId() {
		return groupIdProperty.getValue();
	}

	// Setters

	public void setName(String name) {
		this.nameProperty.setValue(name);
	}

	public void setValue(String value) {
		this.valueProperty.setValue(value);
	}

	public void setDescription(String description) {
		this.descriptionProperty.setValue(description);
	}

	protected void setGroupId(UUID groupId) {
		this.groupIdProperty.setValue(groupId);
	}

	// Property getters

	public ReadOnlyProperty<String> nameProperty() {
		return nameProperty.getReadOnlyProperty();
	}

	public ReadOnlyProperty<String> valueProperty() {
		return valueProperty.getReadOnlyProperty();
	}

	public ReadOnlyProperty<String> descriptionProperty() {
		return descriptionProperty.getReadOnlyProperty();
	}

	public ReadOnlyProperty<UUID> groupIdProperty() {
		return groupIdProperty.getReadOnlyProperty();
	}

	// Persistence methods

	@Override
	public String stringify() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("created", createdTimestamp);
		json.put("name", getName());
		json.put("value", getValue());
		json.put("description", getDescription());
		json.put("group", getGroupId() == null ? JSONObject.NULL : getGroupId());
		json.put("encrypted", isEncrypted());
		return json.toString();
	}

	public static Secret parse(String s) throws ClassNotFoundException {
		Secret secret = null;
		try {
			JSONObject json = new JSONObject(s);
			if (json.getBoolean("encrypted")) {
				secret = new EncryptedSecret();
			} else {
				secret = new Secret();
			}
			secret.id = UUID.fromString(json.getString("id"));
			secret.createdTimestamp = json.getString("created");
			secret.nameProperty = new ReadOnlyObjectWrapper<>(json.getString("name"));
			secret.valueProperty = new ReadOnlyObjectWrapper<>(json.getString("value"));
			secret.descriptionProperty = new ReadOnlyObjectWrapper<>(json.getString("description"));
			UUID gId = json.get("group") == JSONObject.NULL ? null : UUID.fromString(json.getString("group"));
			secret.groupIdProperty = new ReadOnlyObjectWrapper<>(gId);
		} catch (Exception e) {
			Logging.warning("Cannot load Secret from JSON: " + e.getMessage());
			throw new ClassNotFoundException();
		}
		return secret;
	}

}
