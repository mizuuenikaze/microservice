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
package com.muk.ext.security.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.muk.ext.security.KeystoreService;

/**
 * A jce keystore gets and sets sensitive values in an arbitrary keystore.
 *
 */
public class DefaultKeystoreService implements KeystoreService {
	private Path keystore;
	private String keystorePass;

	@Override
	public PrivateKey getPrivateKey(String alias) throws KeyStoreException, CertificateException,
			InvalidKeySpecException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
		final KeyStore ks = loadKeyStore();

		final KeyStore.PrivateKeyEntry privateKeyEntry = extractKeyEntry(ks, alias, KeyStore.PrivateKeyEntry.class);

		return privateKeyEntry.getPrivateKey();
	}

	@Override
	public String getPBEKey(String alias) throws KeyStoreException, CertificateException, InvalidKeySpecException,
			NoSuchAlgorithmException, UnrecoverableEntryException, IOException {

		final KeyStore ks = loadKeyStore();

		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		final KeyStore.SecretKeyEntry secretKeyEntry = extractKeyEntry(ks, alias, KeyStore.SecretKeyEntry.class);
		final PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(secretKeyEntry.getSecretKey(), PBEKeySpec.class);
		return new String(keySpec.getPassword());
	}

	private KeyStore loadKeyStore()
			throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
		final KeyStore ks = KeyStore.getInstance("JCEKS");

		InputStream ksIn = null;

		try {
			ksIn = Files.newInputStream(keystore, StandardOpenOption.READ);
			ks.load(ksIn, keystorePass.toCharArray());
		} finally {
			if (ksIn != null) {
				ksIn.close();
			}
		}

		return ks;
	}

	private <T extends KeyStore.Entry> T extractKeyEntry(KeyStore ks, String alias, Class<T> entryType)
			throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeySpecException {
		final KeyStore.PasswordProtection entryPassword = new KeyStore.PasswordProtection(keystorePass.toCharArray());

		if (!ks.entryInstanceOf(alias, entryType)) {
			throw new KeyStoreException("Entry is not the correct type.");
		}

		return entryType.cast(ks.getEntry(alias, entryPassword));
	}

	@Override
	public void addPBEKey(String alias, String sensitiveValue) throws KeyStoreException, CertificateException,
			InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		final SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(sensitiveValue.toCharArray()));

		final KeyStore ks = KeyStore.getInstance("JCEKS");

		InputStream ksIn = null;

		try {
			ksIn = Files.newInputStream(keystore, StandardOpenOption.READ);
			ks.load(ksIn, keystorePass.toCharArray());
		} finally {
			if (ksIn != null) {
				ksIn.close();
			}
		}

		final KeyStore.PasswordProtection entryPassword = new KeyStore.PasswordProtection(keystorePass.toCharArray());

		ks.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), entryPassword);

		final OutputStream ksOut = Files.newOutputStream(keystore, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);

		try {
			ks.store(ksOut, keystorePass.toCharArray());
		} finally {
			if (ksOut != null) {
				ksOut.close();
			}
		}
	}

	public void setKeystore(Path keystore) {
		this.keystore = keystore;
	}

	public void setKeystorePass(String keystorePass) {
		this.keystorePass = keystorePass;
	}
}
