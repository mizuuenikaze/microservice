package com.muk.services.api;

public interface CryptoService {
	String encode(byte[] byteArray);

	String encodeUrlSafe(byte[] byteArray);

	byte[] decode(String value);

	String encrypt(String value);

	String decrypt(String value);
}
