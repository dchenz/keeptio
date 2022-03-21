package com.keeptio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.keeptio.entities.EncryptionConfig;
import com.keeptio.entities.Group;
import com.keeptio.entities.Secret;
import com.keeptio.entities.Vault;
import com.keeptio.services.security.SupportedCiphers;
import com.keeptio.services.security.SupportedHashes;

public class TestVaultGroupCreate {

	private Vault vault;

	@Before
	public void init() {
		EncryptionConfig config = new EncryptionConfig(SupportedCiphers.AES_256, SupportedHashes.SHA_512, 10000);
		vault = new Vault("Test", config);
	}
	
	@Test
	public void test_create_group_noMembers() {
		String name = "Group 123";
		Group g = vault.createGroup(name);
		assertEquals(name, g.getName());
		assertEquals(g.getMembers().size(), 0);
		assertEquals(vault.getGroups().size(), 1);
		assertTrue(vault.getGroups().contains(g));
	}
	
	@Test
	public void test_create_group_hasMembers() {
		String name = "Group 123";
		Group g = vault.createGroup(name);
		
		assertEquals(name, g.getName());
		assertEquals(g.getMembers().size(), 0);
		assertEquals(vault.getGroups().size(), 1);
		assertTrue(vault.getGroups().contains(g));
		
		Secret s1 = vault.createSecret("Sec1", "123", "test", "");
		Secret s2 = vault.createSecret("Sec1", "123", "test", "");
		
		vault.addSecretToGroup(s1, g);
		
		assertEquals(s1.getGroupId(), g.getId());
		assertEquals(s2.getGroupId(), null);
		assertEquals(g.getMembers().size(), 1);
		assertTrue(g.getMembers().contains(s1.getId()));
		assertFalse(g.getMembers().contains(s2.getId()));		
	}
	
	@Test
	public void test_groupMember_sameRef() {
		String name = "Group 123";
		Group g = vault.createGroup(name);
		Secret s = vault.createSecret("Sec1", "123", "test", "");
		vault.addSecretToGroup(s, g);
		assertEquals(g.getMembers().get(0), s.getId());
		assertEquals(vault.findSecret(g.getMembers().get(0)), s);
		assertTrue(vault.findSecret(g.getMembers().get(0)) == s);
	}
	
}
