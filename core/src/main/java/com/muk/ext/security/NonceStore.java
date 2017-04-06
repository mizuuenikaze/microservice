package com.muk.ext.security;

/**
 * 
 * A persistent store for the nonce information.
 * 
 */
public interface NonceStore {
	int validatePayloadAgainstStore(String storeId, String nonce, Long timestamp) throws Exception;
}
