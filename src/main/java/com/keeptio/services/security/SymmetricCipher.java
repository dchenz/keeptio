package com.keeptio.services.security;

interface SymmetricCipher {
	public byte[] encrypt(byte[] plainText, byte[] key);
	public byte[] decrypt(byte[] cipherText, byte[] key) throws DecryptionException;
}
