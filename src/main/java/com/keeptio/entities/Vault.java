package com.keeptio.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.keeptio.services.VaultService;
import com.keeptio.util.Logging;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access=AccessLevel.PRIVATE)

public class Vault implements StringSerializable {
	
	@Getter
	@Setter
	private String name;

	@Getter
	private EncryptionConfig encryption;

	private ListProperty<Secret> secretsProperty;
	
	private ListProperty<Group> groupsProperty;
	
	private Map<UUID, Secret> secrets;

	private Map<UUID, Group> groups;
	
	@Setter
	private String cachedPassword;
	
	@Setter
	private String cachedFileLocation;

	public Vault(String name, EncryptionConfig config) {
		this.name = name;
		this.encryption = config;
		secrets = new HashMap<>();
		groups = new HashMap<>();
		
		secretsProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
		groupsProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
	}
	
	public Secret createSecret(String name, String value, String description, String password) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Secret's cannot be NULL or blank");
		}
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Secret's value cannot be NULL or empty");
		}
		if (description == null) {
			throw new IllegalArgumentException("Secret's description cannot be NULL");
		}
		if (password == null) {
			throw new IllegalArgumentException("Secret's password cannot be NULL");
		}
		Secret s = null;
		if (password.isEmpty()) {
			s = new Secret(name, value, description);
		} else {
			s = new EncryptedSecret(name, value, description, password);
		}
		secrets.put(s.getId(), s);
		secretsProperty.add(s);
		return s;
	}

	public void deleteSecret(UUID id) {
		if (!secrets.containsKey(id)) {
			throw new IllegalArgumentException("Secret " + id + " could not be found");
		}
		Secret s = secrets.remove(id);
		if (s.getGroupId() != null) {
			Group group = findGroup(s.getGroupId());
			group.removeMember(s);
		}
		secretsProperty.remove(s);
	}

	public Secret findSecret(UUID id) {
		if (!secrets.containsKey(id)) {
			throw new IllegalArgumentException("Secret " + id + " could not be found");
		}
		return secrets.get(id);
	}

	public Group createGroup(String name) {
		Group g = new Group(name);
		groups.put(g.getId(), g);
		groupsProperty.add(g);
		return g;
	}

	public void deleteGroup(UUID id) {
		if (!groups.containsKey(id)) {
			throw new IllegalArgumentException("Group " + id + " could not be found");
		}
		Group g = groups.get(id);
		for (UUID sId : g.getMembers()) {
			deleteSecret(sId);
		}
		groups.remove(id);
		groupsProperty.remove(g);
	}

	public Group findGroup(UUID id) {
		if (!groups.containsKey(id)) {
			throw new IllegalArgumentException("Group " + id + " could not be found");
		}
		return groups.get(id);
	}
	
	public void addSecretToGroup(Secret s, Group group) {
		if (s == null) {
			throw new IllegalArgumentException("Secret cannot be NULL");
		}
		if (group == null) {
			throw new IllegalArgumentException("Group cannot be NULL");
		}
		if (group.isMember(s)) {
			throw new IllegalArgumentException("Secret is already a member of group");
		}
		group.addMember(s);
	}
	
	public void removeSecretFromGroup(Secret s) {
		if (s == null) {
			throw new IllegalArgumentException("Secret cannot be NULL");
		}
		if (s.getGroupId() != null) {
			groups.get(s.getGroupId()).removeMember(s);
		}
	}
	
	public List<Secret> getSecrets() {
		return List.copyOf(secrets.values()); // Unmodifiable
	}
	
	public List<Group> getGroups() {
		return List.copyOf(groups.values()); // Unmodifiable
	}
	
	public ReadOnlyListProperty<Secret> secretsProperty() {
		return secretsProperty;
	}
	
	public ReadOnlyListProperty<Group> groupsProperty() {
		return groupsProperty;
	}

	@Override
	public String stringify() {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("encryption", new JSONObject(encryption.stringify()));
		JSONArray jsonSecrets = new JSONArray();
		for (Secret s : secrets.values()) {
			jsonSecrets.put(new JSONObject(s.stringify()));
		}
		json.put("secrets", jsonSecrets);
		JSONArray jsonGroups = new JSONArray();
		for (Group g : groups.values()) {
			jsonGroups.put(new JSONObject(g.stringify()));
		}
		json.put("groups", jsonGroups);
		return json.toString();
	}
	
	public static Vault parse(String s) throws ClassNotFoundException {
		Vault vault = new Vault();
		try {
			
			JSONObject json = new JSONObject(s);
			vault.name = json.getString("name");
			vault.encryption = EncryptionConfig.parse(json.getJSONObject("encryption").toString());
			
			vault.secretsProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
			HashMap<UUID, Secret> secrets = new HashMap<>();
			for (Object secObj : json.getJSONArray("secrets")) {
				JSONObject jsonSecret = (JSONObject) secObj;
				Secret sec = Secret.parse(jsonSecret.toString());
				secrets.put(sec.getId(), sec);
				vault.secretsProperty.add(sec);
			}
			vault.secrets = secrets;
			
			vault.groupsProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
			HashMap<UUID, Group> groups = new HashMap<>();
			for (Object gObj : json.getJSONArray("groups")) {
				JSONObject jsonGroup =(JSONObject) gObj;
				Group g = Group.parse(jsonGroup.toString());
				groups.put(g.getId(), g);
				vault.groupsProperty.add(g);
			}
			vault.groups = groups;
			
		} catch (Exception e) {
			Logging.warning("Cannot load Vault from JSON: " + e.getMessage());
			throw new ClassNotFoundException();
		}
		return vault;
	}

	public void saveChanges() {
		VaultService vs = new VaultService();
		// Cached values have no getter methods, so it's called from inside Vault
		vs.save(this, cachedPassword, cachedFileLocation);
	}

}
