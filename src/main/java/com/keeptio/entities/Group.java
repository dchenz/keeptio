package com.keeptio.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class Group implements StringSerializable, Displayable {

	@Getter
	private UUID id;

	@Getter
	@Setter
	private String name;

	private ListProperty<UUID> membersProperty;

	protected Group(String name) {
		id = UUID.randomUUID();
		this.name = name;
		membersProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
	}

	protected void addMember(Secret s) {
		if (!isMember(s)) {
			Logging.debug("Added to group: " + s.getName());
			membersProperty.add(s.getId());
			s.setGroupId(this.getId());
		} else {
			Logging.warning("Group already contains secret. Trying to add: " + s.toString());
		}
	}

	protected void removeMember(Secret s) {
		if (isMember(s)) {
			Logging.debug("Removed from group: " + s.getName());
			membersProperty.remove(s.getId());
			s.setGroupId(null);
		} else {
			Logging.warning("Group doesn't contain secret. Trying to remove: " + s.toString());
		}
	}

	public boolean isMember(Secret s) {
		return membersProperty.contains(s.getId());
	}

	public List<UUID> getMembers() {
		return List.copyOf(membersProperty); // Unmodifiable
	}
	
	public ReadOnlyListProperty<UUID> membersProperty() {
		return membersProperty;
	}

	@Override
	public String stringify() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name", name);
		json.put("members", new JSONArray(getMembers()));
		return json.toString();
	}

	public static Group parse(String s) throws ClassNotFoundException {
		Group g = new Group();
		try {
			JSONObject json = new JSONObject(s);
			g.id = UUID.fromString(json.getString("id"));
			g.name = json.getString("name");
			g.membersProperty = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
			for (Object idObj : json.getJSONArray("members")) {
				g.membersProperty.add(UUID.fromString((String) idObj));
			}
		} catch (Exception e) {
			Logging.warning("Cannot load Group from JSON: " + e.getMessage());
			throw new ClassNotFoundException();
		}
		return g;
	}

}
