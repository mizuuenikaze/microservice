/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
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
