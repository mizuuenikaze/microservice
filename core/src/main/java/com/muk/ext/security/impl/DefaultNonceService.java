package com.muk.ext.security.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.muk.ext.security.NonceService;
import com.muk.ext.security.NonceStore;

/**
 * 
 * Provides basic nonce ideas around temporary urls. Needs a concrete
 * implementation of persistent storage of this nonce information.
 * 
 */
public class DefaultNonceService implements NonceService {

	private NonceStore nonceStore;

	@Override
	public byte[] generatePsudoRandomNonce() throws NoSuchAlgorithmException {
		byte[] nonce = new byte[16];
		Random rand;
		rand = SecureRandom.getInstance("SHA1PRNG");
		rand.nextBytes(nonce);

		return nonce;
	}

	@Override
	public String encode(byte[] byteArray) {
		return Base64.encodeBase64String(byteArray);
	}

	@Override
	public String encodeUrlSafe(byte[] byteArray) {
		return Base64.encodeBase64URLSafeString(byteArray);
	}

	@Override
	public String generateHash(String salt, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
		return hash(salt, payload);
	}

	@Override
	public String generateHash(String payload) throws NoSuchAlgorithmException {
		String result = null;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		result = encode(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));

		return result;
	}

	@Override
	public int validatePayload(String storeId, String salt, String payload, Long timestamp, String hash)
			throws NoSuchAlgorithmException, InvalidKeyException, Exception {
		int exitCode = 0;

		if (nonceStore != null) {
			exitCode = nonceStore.validatePayloadAgainstStore(storeId, salt, timestamp);
		}

		if (exitCode == 0) {
			String receivedHash = hash(salt, payload);

			if (!hash.equals(receivedHash)) {
				return 3;
			}
		}

		return exitCode;

	}

	public void setNonceStore(NonceStore nonceStore) {
		this.nonceStore = nonceStore;
	}

	private String hash(String salt, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(salt.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		return Base64.encodeBase64URLSafeString(sha256_HMAC.doFinal(payload.getBytes()));
	}
}
