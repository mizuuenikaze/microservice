package com.muk.ext.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * Defines nonce creation and validation to make urls somewhat tamper-proof and
 * time sensitive.
 * 
 */
public interface NonceService {
	byte[] generatePsudoRandomNonce() throws NoSuchAlgorithmException;

	String encode(byte[] byteArray);

	String encodeUrlSafe(byte[] byteArray);

	String generateHash(String salt, String payload) throws NoSuchAlgorithmException, InvalidKeyException;

	String generateHash(String payload) throws NoSuchAlgorithmException;

	int validatePayload(String storeId, String salt, String payload, Long timestamp, String hash)
			throws NoSuchAlgorithmException, InvalidKeyException, Exception;
}
