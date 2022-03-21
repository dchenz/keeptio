package com.keeptio.services.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

class PBKDF2HashSHA implements PasswordHashing {

	private SecretKeyFactory factory;

	private int rounds;
	
	private int keySizeBits;

	public PBKDF2HashSHA(SupportedHashes variant, int rounds) {
		this.rounds = rounds;
		try {
			switch (variant) {
			case SHA_256:
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				break;
			case SHA_512:
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
				break;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keySizeBits = 256; // Both supported ciphers are 256-bits 
	}

	@Override
	public byte[] digest(String password, byte[] salt) {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, rounds, keySizeBits);
		SecretKey key = null;
		try {
			key = factory.generateSecret(spec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key.getEncoded();
	}

}
