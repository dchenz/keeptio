package com.keeptio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.keeptio.entities.EncryptionConfig;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;

public class TestEntityStringify {

	private Vault vault;

	@Before
	public void init() {
		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_512, 10000);
		vault = new Vault("Test", config);
	}
	
	@Test
	public void test_vault_empty() {
		Vault rebuilt = null;
		try {
			rebuilt = Vault.parse(vault.stringify());
		} catch (ClassNotFoundException e) {
			fail("Vault could not be rebuilt from string");
		}
		assertEquals(vault.getName(), rebuilt.getName());
		assertEquals(vault.getSecrets().size(), 0);
		assertEquals(vault.getSecrets().size(), rebuilt.getSecrets().size());
		assertEquals(vault.getGroups().size(), 0);
		assertEquals(vault.getGroups().size(), rebuilt.getGroups().size());
		assertEquals(vault.getEncryption().getCipherAlgorithm(), rebuilt.getEncryption().getCipherAlgorithm());
		assertEquals(vault.getEncryption().getHashAlgorithm(), rebuilt.getEncryption().getHashAlgorithm());
		assertEquals(vault.getEncryption().getPbkdfRounds(), rebuilt.getEncryption().getPbkdfRounds());
	}
	
	@Test
	public void test_vault_hasSecrets_noGroups() {
		int nSecrets = 10;
		for (int i = 0; i < nSecrets; i++) {
			vault.createSecret(
				(new Date()).toString(), 
				(new Date()).toString(), 
				(new Date()).toString(), 
				i < nSecrets / 2 ? "" : "password"
			);
		}
		Vault rebuilt = null;
		try {
			rebuilt = Vault.parse(vault.stringify());
		} catch (ClassNotFoundException e) {
			fail("Vault could not be rebuilt from string");
		}
		Set<UUID> rebuiltSecretIds = rebuilt.getSecrets().stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());
		assertEquals(vault.getName(), rebuilt.getName());
		assertEquals(vault.getSecrets().size(), nSecrets);
		assertEquals(vault.getSecrets().size(), rebuiltSecretIds.size());
		for (Secret s : vault.getSecrets()) {
			assertTrue(rebuiltSecretIds.contains(s.getId()));
		}
		assertEquals(vault.getGroups().size(), 0);
		assertEquals(vault.getGroups().size(), rebuilt.getGroups().size());
		assertEquals(vault.getEncryption().getCipherAlgorithm(), rebuilt.getEncryption().getCipherAlgorithm());
		assertEquals(vault.getEncryption().getHashAlgorithm(), rebuilt.getEncryption().getHashAlgorithm());
		assertEquals(vault.getEncryption().getPbkdfRounds(), rebuilt.getEncryption().getPbkdfRounds());
	}
	
	@Test
	public void test_vault_noSecrets_hasGroups() {
		int nGroups = 10;
		for (int i = 0; i < nGroups; i++) {
			vault.createGroup((new Date()).toString());
		}
		Vault rebuilt = null;
		try {
			rebuilt = Vault.parse(vault.stringify());
		} catch (ClassNotFoundException e) {
			fail("Vault could not be rebuilt from string");
		}
		Set<UUID> rebuiltGroupIds = rebuilt.getGroups().stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());
		assertEquals(vault.getName(), rebuilt.getName());
		assertEquals(vault.getSecrets().size(), 0);
		assertEquals(vault.getSecrets().size(), rebuilt.getSecrets().size());
		assertEquals(vault.getGroups().size(), nGroups);
		assertEquals(vault.getGroups().size(), rebuiltGroupIds.size());
		for (Group g : vault.getGroups()) {
			assertTrue(rebuiltGroupIds.contains(g.getId()));
		}
		assertEquals(vault.getEncryption().getCipherAlgorithm(), rebuilt.getEncryption().getCipherAlgorithm());
		assertEquals(vault.getEncryption().getHashAlgorithm(), rebuilt.getEncryption().getHashAlgorithm());
		assertEquals(vault.getEncryption().getPbkdfRounds(), rebuilt.getEncryption().getPbkdfRounds());
	}
	
	@Test
	public void test_vault_hasSecrets_hasGroups() {
		// Add secrets and groups (5 * 10 + 5)
		int nSecretsPerGroup = 5;
		int nGroups = 10;
		for (int i = 0; i < nGroups; i++) {
			Group g = vault.createGroup((new Date()).toString());
			for (int j = 0; j < nSecretsPerGroup; j++) {
				Secret s = vault.createSecret(
					(new Date()).toString(), 
					(new Date()).toString(), 
					(new Date()).toString(), 
					j < nSecretsPerGroup / 2 ? "" : "password"
				);
				vault.addSecretToGroup(s, g);
			}
		}
		// Also add secrets without groups
		for (int i = 0; i < nSecretsPerGroup; i++) {
			vault.createSecret(
				(new Date()).toString(), 
				(new Date()).toString(), 
				(new Date()).toString(), 
				i < nSecretsPerGroup / 2 ? "" : "password"
			);
		}
		// Test string parsing
		Vault rebuilt = null;
		try {
			rebuilt = Vault.parse(vault.stringify());
		} catch (ClassNotFoundException e) {
			fail("Vault could not be rebuilt from string");
		}
		// Get sets of secret/group UUIDs from vault 
		Set<UUID> rebuiltSecretIds = rebuilt.getSecrets().stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());
		Set<UUID> rebuiltGroupIds = rebuilt.getGroups().stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());
		
		assertEquals(vault.getName(), rebuilt.getName());
		
		// Check the number of secrets
		assertEquals(vault.getSecrets().size(), nSecretsPerGroup * nGroups + nSecretsPerGroup);
		assertEquals(vault.getSecrets().size(), rebuilt.getSecrets().size());
		for (Secret s : vault.getSecrets()) {
			// Check that all secrets' UUID are reproduced in the rebuilt vault
			assertTrue(rebuiltSecretIds.contains(s.getId()));
			if (s.getGroupId() != null) {
				// Check that rebuilt vault contains all groups referenced by secrets
				assertTrue(rebuiltGroupIds.contains(s.getGroupId()));	
			}
		}
		// Check the number of groups
		assertEquals(vault.getGroups().size(), nGroups);
		assertEquals(vault.getGroups().size(), rebuiltGroupIds.size());
		for (Group g : vault.getGroups()) {
			// Check that all groups' UUID are reproduced in the rebuilt vault
			assertTrue(rebuiltGroupIds.contains(g.getId()));
			for (UUID sId : g.getMembers()) {
				// Check that all groups' members' UUIDs refer to an existing secret
				Secret s = vault.findSecret(sId);
				assertNotEquals(s, null);
				assertNotEquals(s.getGroupId(), null);
				assertEquals(g.getId(), s.getGroupId());
			}
		}
		assertEquals(vault.getEncryption().getCipherAlgorithm(), rebuilt.getEncryption().getCipherAlgorithm());
		assertEquals(vault.getEncryption().getHashAlgorithm(), rebuilt.getEncryption().getHashAlgorithm());
		assertEquals(vault.getEncryption().getPbkdfRounds(), rebuilt.getEncryption().getPbkdfRounds());
	}
	
	@Test
	public void test_secret_noGroup() {
		Secret secret = vault.createSecret("name", "value", "description", "");
		Secret rebuilt = null;
		try {
			rebuilt = Secret.parse(secret.stringify());
		} catch (ClassNotFoundException e) {
			fail("Secret could not be rebuilt from string");
		}
		assertEquals(secret.getId(), rebuilt.getId());
		assertEquals(secret.getCreatedTimestamp(), rebuilt.getCreatedTimestamp());
		assertEquals(secret.getName(), rebuilt.getName());
		assertEquals(secret.getValue(), rebuilt.getValue());
		assertEquals(secret.getDescription(), rebuilt.getDescription());
		assertEquals(secret.isEncrypted(), rebuilt.isEncrypted());
		assertEquals(secret.getGroupId(), rebuilt.getGroupId());
	}
	
	@Test
	public void test_secret_hasGroup() {
		Secret secret = vault.createSecret("name", "value", "description", "");
		vault.addSecretToGroup(secret, vault.createGroup("group"));
		Secret rebuilt = null;
		try {
			rebuilt = Secret.parse(secret.stringify());
		} catch (ClassNotFoundException e) {
			fail("Secret could not be rebuilt from string");
		}
		assertEquals(secret.getId(), rebuilt.getId());
		assertEquals(secret.getCreatedTimestamp(), rebuilt.getCreatedTimestamp());
		assertEquals(secret.getName(), rebuilt.getName());
		assertEquals(secret.getValue(), rebuilt.getValue());
		assertEquals(secret.getDescription(), rebuilt.getDescription());
		assertEquals(secret.isEncrypted(), rebuilt.isEncrypted());
		assertEquals(secret.getGroupId(), rebuilt.getGroupId());
	}
	
	@Test
	public void test_secret_encrypted_noGroup() {
		Secret secret = vault.createSecret("name", "value", "description", "123");
		Secret rebuilt = null;
		try {
			rebuilt = Secret.parse(secret.stringify());
		} catch (ClassNotFoundException e) {
			fail("Secret could not be rebuilt from string");
		}
		assertEquals(secret.getId(), rebuilt.getId());
		assertEquals(secret.getCreatedTimestamp(), rebuilt.getCreatedTimestamp());
		assertEquals(secret.getName(), rebuilt.getName());
		assertEquals(secret.getValue(), rebuilt.getValue());
		assertEquals(secret.getDescription(), rebuilt.getDescription());
		assertEquals(secret.isEncrypted(), rebuilt.isEncrypted());
		assertEquals(secret.getGroupId(), rebuilt.getGroupId());
	}
	
	@Test
	public void test_secret_encrypted_hasGroup() {
		Secret secret = vault.createSecret("name", "value", "description", "123");
		vault.addSecretToGroup(secret, vault.createGroup("group"));
		Secret rebuilt = null;
		try {
			rebuilt = Secret.parse(secret.stringify());
		} catch (ClassNotFoundException e) {
			fail("Secret could not be rebuilt from string");
		}
		assertEquals(secret.getId(), rebuilt.getId());
		assertEquals(secret.getCreatedTimestamp(), rebuilt.getCreatedTimestamp());
		assertEquals(secret.getName(), rebuilt.getName());
		assertEquals(secret.getValue(), rebuilt.getValue());
		assertEquals(secret.getDescription(), rebuilt.getDescription());
		assertEquals(secret.isEncrypted(), rebuilt.isEncrypted());
		assertEquals(secret.getGroupId(), rebuilt.getGroupId());
	}
	
	@Test
	public void test_group_noSecrets() {
		Group group = vault.createGroup("group");
		Group rebuilt = null;
		try {
			rebuilt = Group.parse(group.stringify());
		} catch (ClassNotFoundException e) {
			fail("Group could not be rebuilt from string");
		}
		assertEquals(group.getId(), rebuilt.getId());
		assertEquals(group.getName(), rebuilt.getName());
		assertEquals(group.getMembers().size(), 0);
		assertEquals(group.getMembers().size(), rebuilt.getMembers().size());
	}
	
	@Test
	public void test_group_hasSecrets() {
		int n = 10;
		Group group = vault.createGroup("group");
		for (int i = 0; i < n; i++) {
			Secret secret = vault.createSecret("name", "value", "description", "");
			vault.addSecretToGroup(secret, group);
		}
		Group rebuilt = null;
		try {
			rebuilt = Group.parse(group.stringify());
		} catch (ClassNotFoundException e) {
			fail("Group could not be rebuilt from string");
		}
		Set<UUID> memberIds = Set.copyOf(rebuilt.getMembers());
		assertEquals(group.getId(), rebuilt.getId());
		assertEquals(group.getName(), rebuilt.getName());
		assertEquals(group.getMembers().size(), n);
		assertEquals(group.getMembers().size(), memberIds.size());
		for (UUID sId : group.getMembers()) {
			assertTrue(memberIds.contains(sId));
		}
	}
	
	@Test
	public void test_encryptionConfig() {
		int rounds = 50000;
		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_256, rounds);
		EncryptionConfig rebuilt = null;
		try {
			rebuilt = EncryptionConfig.parse(config.stringify());
		} catch (ClassNotFoundException e) {
			fail("EncryptionConfig could not be rebuilt from string");
		}
		assertEquals(config.getCipherAlgorithm(), SupportedCiphers.AES_256);
		assertEquals(config.getHashAlgorithm(), SupportedHashes.SHA_256);
		assertEquals(config.getPbkdfRounds(), Integer.valueOf(rounds));
		assertEquals(config.getCipherAlgorithm(), rebuilt.getCipherAlgorithm());
		assertEquals(config.getHashAlgorithm(), rebuilt.getHashAlgorithm());
		assertEquals(config.getPbkdfRounds(), rebuilt.getPbkdfRounds());
	}

}
