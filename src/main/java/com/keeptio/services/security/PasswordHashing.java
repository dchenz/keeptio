package com.keeptio.services.security;

interface PasswordHashing {
	public byte[] digest(String password, byte[] salt);
}
