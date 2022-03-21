package com.keeptio.services.security;

import java.util.EnumSet;
import java.util.HashMap;

import lombok.Getter;

public enum SupportedCiphers {
	
	AES_256('1'), TWOFISH('2');
	
	private static final HashMap<Character, SupportedCiphers> lookup = new HashMap<>();
	
	static {
		for (SupportedCiphers s : EnumSet.allOf(SupportedCiphers.class)) {
			lookup.put(s.getId(), s);
		}
	}
	
	@Getter
	char id;
	
	private SupportedCiphers(char id) {
		this.id = id;
	}
	
	public static SupportedCiphers get(char id) {
		return lookup.get(id);
	}
	
}
