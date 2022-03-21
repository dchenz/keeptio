package com.keeptio.services.security;

import java.util.EnumSet;
import java.util.HashMap;

import lombok.Getter;

public enum SupportedHashes {

	SHA_256('1'), SHA_512('2');

	private static final HashMap<Character, SupportedHashes> lookup = new HashMap<>();

	static {
		for (SupportedHashes s : EnumSet.allOf(SupportedHashes.class)) {
			lookup.put(s.getId(), s);
		}
	}

	@Getter
	char id;

	private SupportedHashes(char id) {
		this.id = id;
	}

	public static SupportedHashes get(char id) {
		return lookup.get(id);
	}
	
}
