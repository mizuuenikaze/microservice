/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.core.ext.test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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
		allApplications.put("mukapi", "umkay!");

		keystoreService = new DefaultKeystoreService();
		keystoreService.setKeystore(Paths.get(System.getProperty("custom.application.keystore")));
		keystoreService.setKeystorePass(System.getProperty("custom.application.keystorepass"));
	}

	/**
	 * This keystore test is not ideal since it goes to an actual keystore on the target filesystem.
	 * This will not pass for package managers that run tests in a sandbox.
	 *
	 * This test is used to setup a default keystore and can be used locally, but to do not
	 * check in this enabled test.
	 */
	@Test
	@Ignore
	public void addSharedSecretToKeystoreTest() throws Exception {

		for (final Map.Entry<String, String> entry : allApplications.entrySet()) {
			keystoreService.addPBEKey(entry.getKey(), entry.getValue());
		}

		listKeystoreTest();
	}

	@Test
	@Ignore
	public void listKeystoreTest() throws Exception {
		for (final Map.Entry<String, String> entry : allApplications.entrySet()) {
			final String sensitiveValue = keystoreService.getPBEKey(entry.getKey());
			System.out.println("alias " + entry.getKey() + " -- value " + sensitiveValue);
		}
	}

	@Test
	public void dummyTest() {
		assert(true);
	}
}
