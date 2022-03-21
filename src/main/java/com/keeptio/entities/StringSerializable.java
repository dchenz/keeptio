package com.keeptio.entities;

public interface StringSerializable {
	
	public String stringify();
	
	// Implementing classes also should implement this
	//
	//	public static CLASS parse(String s) {
	//		return fromJSON(new JSONObject(s));
	//	}
		
}
