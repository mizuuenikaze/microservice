package com.muk.services.commerce;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muk.services.api.CryptoService;

public class CryptoServiceImpl implements CryptoService {
	private final static Logger LOG = LoggerFactory.getLogger(CryptoServiceImpl.class);

	private SecretKeySpec temporaryKey;
	private AlgorithmParameterSpec ivSpec;

	@Override
	public String encrypt(String value) {
		final byte[] result = encrypt(value.getBytes(StandardCharsets.UTF_8));
		return encode(result);
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
	public byte[] decode(String value) {
		return Base64.decodeBase64(value);
	}

	@Override
	public String decrypt(String value) {
		final byte[] result = decrypt(decode(value));

		return new String(result, StandardCharsets.UTF_8);
	}

	private byte[] decrypt(byte[] byteArray) {
		byte[] result = null;

		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, temporaryKey, ivSpec);

			result = cipher.doFinal(byteArray);

		} catch (final InvalidKeyException keyEx) {
			LOG.error("Failed to init cipher.", keyEx);
		} catch (final NoSuchPaddingException padEx) {
			LOG.error("Failed to init cipher.", padEx);
		} catch (final NoSuchAlgorithmException algEx) {
			LOG.error("Failed to init cipher.", algEx);
		} catch (final BadPaddingException badPadEx) {
			LOG.error("Failed to decrypt.", badPadEx);
		} catch (final IllegalBlockSizeException blockEx) {
			LOG.error("Failed to decrypt.", blockEx);
		} catch (final InvalidAlgorithmParameterException paramEx) {
			LOG.error("Failed to decrypt.", paramEx);
		}

		return result;
	}

	private byte[] encrypt(byte[] byteArray) {
		byte[] result = null;

		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, temporaryKey, ivSpec);

			result = cipher.doFinal(byteArray);

		} catch (final InvalidKeyException keyEx) {
			LOG.error("Failed to init cipher.", keyEx);
		} catch (final NoSuchPaddingException padEx) {
			LOG.error("Failed to init cipher.", padEx);
		} catch (final NoSuchAlgorithmException algEx) {
			LOG.error("Failed to init cipher.", algEx);
		} catch (final BadPaddingException badPadEx) {
			LOG.error("Failed to encrypt.", badPadEx);
		} catch (final IllegalBlockSizeException blockEx) {
			LOG.error("Failed to encrypt.", blockEx);
		} catch (final InvalidAlgorithmParameterException paramEx) {
			LOG.error("Failed to encrypt.", paramEx);
		}

		return result;
	}

	@PostConstruct
	public void postConstruct() {
		try {
			final KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			temporaryKey = new SecretKeySpec(kgen.generateKey().getEncoded(), "AES");

			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final byte[] iv = new byte[cipher.getBlockSize()];
			new SecureRandom().nextBytes(iv);
			ivSpec = new IvParameterSpec(iv);
		} catch (final NoSuchAlgorithmException ex) {
			LOG.error("Failed to initalize encryption key", ex);
		} catch (final NoSuchPaddingException padEx) {
			LOG.error("Failed to get cipher.", padEx);
		}
	}
}
