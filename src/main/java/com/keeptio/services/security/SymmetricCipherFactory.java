package com.keeptio.services.security;

class SymmetricCipherFactory {

	public static SymmetricCipher getInstance(SupportedCiphers variant) {
		SymmetricCipher c = null;
		switch (variant) {
		case AES_256:
			c = new AESCipher();
			break;
		case TWOFISH:
			c = new AESCipher(); // TODO: Implement this
			break;
		}
		return c;
	}

}
