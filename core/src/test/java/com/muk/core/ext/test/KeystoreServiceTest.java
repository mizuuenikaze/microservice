package com.muk.core.ext.test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.muk.ext.security.impl.DefaultKeystoreService;

/**
 * Add and list values in a jceks keystore. Must set up junit vm settings.
 *
 */
public class KeystoreServiceTest {

	private Map<String, String> allApplications;
	private DefaultKeystoreService keystoreService;

	@Before
	public void setup() {
		allApplications = new HashMap<String, String>();
		allApplications.put("mzint.quickbooks.1.0.0.Release", "8a9a083b5a3349b5bdf4b804a1118ada");

		keystoreService = new DefaultKeystoreService();
		keystoreService.setKeystore(Paths.get(System.getProperty("custom.application.keystore")));
		keystoreService.setKeystorePass(System.getProperty("custom.application.keystorepass"));
	}

	@Test
	public void addSharedSecretToKeystoreTest() throws Exception {

		for (final Map.Entry<String, String> entry : allApplications.entrySet()) {
			keystoreService.addPBEKey(entry.getKey(), entry.getValue());
		}

		listKeystoreTest();
	}

	@Test
	public void listKeystoreTest() throws Exception {
		for (final Map.Entry<String, String> entry : allApplications.entrySet()) {
			final String sensitiveValue = keystoreService.getPBEKey(entry.getKey());
			System.out.println("alias " + entry.getKey() + " -- value " + sensitiveValue);
		}
	}
}
