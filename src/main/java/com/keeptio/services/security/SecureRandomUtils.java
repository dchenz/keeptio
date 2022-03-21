package com.keeptio.services.security;

import java.security.SecureRandom;

public class SecureRandomUtils {

	private static class Random {
		static final SecureRandom random = new SecureRandom();
	}
	
	public static byte[] randBytes(int n) {
		byte[] b = new byte[n];
		Random.random.nextBytes(b);
		return b;
	}
	
}
