package com.keeptio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.keeptio.entities.EncryptedSecret;
import com.keeptio.entities.EncryptionConfig;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;

public class TestVaultSecretCreate {

	private Vault vault;

	@Before
	public void init() {
		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_512, 10000);
		vault = new Vault("Test", config);
	}

	@Test
	public void test_create_noGroup() {
		
		String name = "hello";
		String value = "my secret";
		String description = "test";
		String individualPassword = "";
		
		Secret s = vault.createSecret(name, value, description, individualPassword);
		
		// Test values of Secret
		assertEquals(name, s.getName());
		assertEquals(value, s.getValue());
		assertEquals(description, s.getDescription());
		
		assertEquals(null, s.getGroupId());
		
		assertEquals(false, s.isEncrypted());
		assertEquals(Secret.class, s.getClass());
		
	}
	
	@Test
	public void test_create_hasGroup() {
		
		String name = "hello";
		String value = "my secret";
		String description = "test";
		String individualPassword = "";
		
		Secret s = vault.createSecret(name, value, description, individualPassword);
		Group group = vault.createGroup("My Group");
		vault.addSecretToGroup(s, group);
		
		// Test values of Secret
		assertEquals(name, s.getName());
		assertEquals(value, s.getValue());
		assertEquals(description, s.getDescription());
		
		assertEquals(group.getId(), s.getGroupId());
		
		assertEquals(group.getMembers().size(), 1);
		assertEquals(group.getMembers().get(0), s.getId());
		
		assertEquals(false, s.isEncrypted());
		assertEquals(Secret.class, s.getClass());
		
	}
	
	@Test
	public void test_create_noPassword() {
		
		String name = "hello";
		String value = "my secret";
		String description = "test";
		String individualPassword = "";
		
		Secret s = vault.createSecret(name, value, description, individualPassword);
		
		// Test values of Secret
		assertEquals(name, s.getName());
		assertEquals(value, s.getValue());
		assertEquals(description, s.getDescription());
		
		assertEquals(null, s.getGroupId());
		
		assertEquals(false, s.isEncrypted());
		assertEquals(Secret.class, s.getClass());
		
	}
	
	@Test
	public void test_create_hasPassword_hasDescription() {
		
		String name = "hello";
		String value = "my secret";
		String description = "test";
		String individualPassword = "123";
		
		Secret s = vault.createSecret(name, value, description, individualPassword);
		
		// Test values of Secret
		assertEquals(name, s.getName());
		assertNotEquals(value, s.getValue());
		assertNotEquals(description, s.getDescription());
		
		assertEquals(null, s.getGroupId());
		
		assertEquals(true, s.isEncrypted());
		assertEquals(EncryptedSecret.class, s.getClass());
		
		assertEquals(((EncryptedSecret) s).retrieve(individualPassword + "1"), null);
		
		Secret retrieved = ((EncryptedSecret) s).retrieve(individualPassword);
		assertNotEquals(retrieved, null);
		
		assertEquals(s.getId(), retrieved.getId());
		assertEquals(name, retrieved.getName());
		assertEquals(value, retrieved.getValue());
		assertEquals(description, retrieved.getDescription());
		
		assertEquals(null, retrieved.getGroupId());
		
		assertEquals(false, retrieved.isEncrypted());
		assertEquals(Secret.class, retrieved.getClass());
		
		retrieved.setName("Hello123");
		assertEquals(retrieved.getName(), s.getName());
		assertEquals(retrieved.getName(), "Hello123");
		
	}
	
	@Test
	public void test_create_hasPassword_noDescription() {
		
		String name = "hello";
		String value = "my secret";
		String description = "";
		String individualPassword = "123";
		
		Secret s = vault.createSecret(name, value, description, individualPassword);
		
		// Test values of Secret
		assertEquals(name, s.getName());
		assertNotEquals(value, s.getValue());
		assertEquals(description, s.getDescription());
		
		assertEquals(null, s.getGroupId());
		
		assertEquals(true, s.isEncrypted());
		assertEquals(EncryptedSecret.class, s.getClass());
		
		assertEquals(((EncryptedSecret) s).retrieve(individualPassword + "1"), null);
		
		Secret retrieved = ((EncryptedSecret) s).retrieve(individualPassword);
		assertNotEquals(retrieved, null);
		
		assertEquals(s.getId(), retrieved.getId());
		assertEquals(name, retrieved.getName());
		assertEquals(value, retrieved.getValue());
		assertEquals(description, retrieved.getDescription());
		
		assertEquals(null, retrieved.getGroupId());
		
		assertEquals(false, retrieved.isEncrypted());
		assertEquals(Secret.class, retrieved.getClass());
		
		retrieved.setName("Hello123");
		assertEquals(retrieved.getName(), s.getName());
		assertEquals(retrieved.getName(), "Hello123");
		
	}
	
	@Test
	public void test_create_invalid_name() {
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret(null, "value", "description", "");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret("", "value", "description", "");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret(" ", "value", "description", "");
		});
		
		try {
			vault.createSecret(".", "value", "description", "");
		} catch (IllegalArgumentException e) {
			fail("Name of single character string should be accepted");
		}
		
	}
	
	@Test
	public void test_create_invalid_value() {
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret("name", null, "description", "");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret("name", "", "description", "");
		});
		
		try {
			vault.createSecret("name", ".", "description", "");
		} catch (IllegalArgumentException e) {
			fail("Secret value of single character string should be accepted");
		}
		
		try {
			vault.createSecret("name", " ", "description", "");
		} catch (IllegalArgumentException e) {
			fail("Secret value of whitespace string should be accepted");
		}
		
	}
	
	@Test
	public void test_create_invalid_description() {
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret("name", "value", null, "");
		});

		try {
			vault.createSecret("name", "value", "", "");
		} catch (IllegalArgumentException e) {
			fail("Description of empty string should be accepted");
		}
		
		try {
			vault.createSecret("name", "value", " ", "");
		} catch (IllegalArgumentException e) {
			fail("Description of whitespace string should be accepted");
		}
		
		try {
			vault.createSecret("name", "value", ".", "");
		} catch (IllegalArgumentException e) {
			fail("Description of single character string should be accepted");
		}
		
	}

	@Test
	public void test_create_invalid_password() {
		
		assertThrows(IllegalArgumentException.class, () -> {
			vault.createSecret("name", "value", "description", null);
		});
		
		try {
			vault.createSecret("name", "value", "description", "");
		} catch (IllegalArgumentException e) {
			fail("Password of empty string should be accepted");
		}
		
		try {
			vault.createSecret("name", "value", "description", " ");
		} catch (IllegalArgumentException e) {
			fail("Password of whitespace string should be accepted");
		}
		
		try {
			vault.createSecret("name", "value", "description", ".");
		} catch (IllegalArgumentException e) {
			fail("Password of single character string should be accepted");
		}
		
	}

}
