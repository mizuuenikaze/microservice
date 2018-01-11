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
package com.muk.services.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.security.KeystoreService;
import com.muk.services.api.CryptoService;
import com.muk.services.api.ExternalOauthService;
import com.muk.services.exchange.ServiceConstants;

/**
 * Authorizes against google oauth with jwt claims.
 *
 *
 */
public class GoogleOAuthService implements ExternalOauthService {
	private static final Logger LOG = LoggerFactory.getLogger(GoogleOAuthService.class);
	private static final String JWT_HEADER = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"; // {"alg":"RS256","typ":"JWT"}
	private static final String JWT_AUD = "https://www.googleapis.com/oauth2/v4/token";
	private static final String JWT_SUB = "fernandocano@winduponthewater.com";
	private static final String TOKEN_GRANT = "urn:ietf:params:oauth:grant-type:jwt-bearer";
	private static final String CACHE_KEY = "google";

	@Inject
	private CryptoService cryptoService;

	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	private CacheManager cacheManager;

	@Override
	public String authorizeRequest(String scope, String privateKeyAlias, String serviceAccount) {
		final Cache cache = cacheManager.getCache(ServiceConstants.CacheNames.externalTokenCache);
		final ValueWrapper valueWrapper = cache.get(CACHE_KEY);
		String token = StringUtils.EMPTY;

		if (valueWrapper == null || valueWrapper.get() == null) {
			final String jwt = createClaim(scope, privateKeyAlias, serviceAccount);
			token = requestAccessToken(jwt);

			cache.put(CACHE_KEY, token);
		} else {
			token = (String) valueWrapper.get();
		}

		return token;
	}

	private String createClaim(String scope, String privateKeyAlias, String account) {
		final Instant issued = Instant.now();
		final Instant expirePlusHour = issued.plus(50, ChronoUnit.MINUTES);

		final StringBuilder sb = new StringBuilder();

		sb.append("{\"iss\":\"").append(account).append("\",\"scope\":\"").append(scope).append("\",\"aud\":\"")
				.append(JWT_AUD).append("\",\"exp\":").append(expirePlusHour.getEpochSecond()).append(",\"iat\":")
				.append(issued.getEpochSecond()).append(",\"sub\":\"").append(JWT_SUB).append("\"}");

		/*
		 * sb.append("{\"iss\":\"").append(account).append("\",\"scope\":\"").append(scope).append("\",\"aud\":\"")
		 * .append(JWT_AUD).append("\",\"exp\":").append(expirePlusHour.getEpochSecond()).append(",\"iat\":")
		 * .append(issued.getEpochSecond()).append("}");
		 */

		String jwt = JWT_HEADER + "." + cryptoService.encodeUrlSafe(sb.toString().getBytes(StandardCharsets.UTF_8));

		try {
			jwt = jwt + "."
					+ cryptoService.signature("SHA256withRSA", jwt, keystoreService.getPrivateKey(privateKeyAlias));
		} catch (final IOException ioEx) {
			LOG.error("Failed to access keystore.", ioEx);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to sign jwt.", secEx);
		}

		return jwt;
	}

	private String requestAccessToken(String jwt) {
		final HttpHeaders headers = new HttpHeaders();
		String token = "error";

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("grant_type", TOKEN_GRANT);
		body.add("assertion", jwt);

		try {
			final ResponseEntity<JsonNode> response = restTemplate.postForEntity(JWT_AUD,
					new HttpEntity<MultiValueMap<String, String>>(body, headers), JsonNode.class);

			token = response.getBody().get("access_token").asText();
		} catch (final HttpClientErrorException httpClientEx) {
			LOG.error("Failed to request token.", httpClientEx);
		}

		return token;
	}

}
