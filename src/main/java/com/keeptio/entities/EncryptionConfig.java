package com.keeptio.entities;

import org.json.JSONObject;

import com.keeptio.models.VaultEncryptionConfig;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;
import com.keeptio.util.Logging;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter

public class EncryptionConfig implements StringSerializable {

	private SupportedCiphers cipherAlgorithm;
	
	private SupportedHashes hashAlgorithm;
	
	private Integer pbkdfRounds;
	
	public EncryptionConfig(VaultEncryptionConfig config) {
		cipherAlgorithm = SupportedCiphers.valueOf(config.getCipherAlgorithm());
		hashAlgorithm = SupportedHashes.valueOf(config.getHashAlgorithm());
		pbkdfRounds = config.getPbkdfRounds();
	}
	
	@Override
	public String stringify() {
		JSONObject json = new JSONObject();
		json.put("c", cipherAlgorithm.getId());
		json.put("h", hashAlgorithm.getId());
		json.put("r", pbkdfRounds);
		return json.toString();
	}
	
	public static EncryptionConfig parse(String s) throws ClassNotFoundException {
		EncryptionConfig c = new EncryptionConfig();
		try {
			JSONObject json = new JSONObject(s);
			c.cipherAlgorithm = SupportedCiphers.get((char) json.getInt("c"));
			if (c.cipherAlgorithm == null) {
				throw new ClassNotFoundException();
			}
			c.hashAlgorithm = SupportedHashes.get((char) json.getInt("h"));
			if (c.hashAlgorithm == null) {
				throw new ClassNotFoundException();
			}
			c.pbkdfRounds = json.getInt("r");
		} catch (Exception e) {
			Logging.warning("Cannot load EncryptionConfig from JSON: " + e.getMessage());
			throw new ClassNotFoundException();
		}
		return c;
	}

}


