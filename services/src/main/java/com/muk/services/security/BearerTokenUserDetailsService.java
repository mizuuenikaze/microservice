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
package com.muk.services.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.ext.core.json.model.oauth.TokenResponse;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.CachingOauthUserDetailsService;
import com.muk.services.api.SecurityConfigurationService;

public class BearerTokenUserDetailsService implements CachingOauthUserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(BearerTokenUserDetailsService.class);

	private UserCache userCache = new NullUserCache();

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Inject
	@Qualifier("hashService")
	private NonceService nonceService;

	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = userCache.getUserFromCache(username);

		if (user == null) {
			user = loadInvalidUser();
		} else if (!(user instanceof OauthUser)) {
			throw new UsernameNotFoundException("Unexpected user detail type.");
		} else if (!isValidToken(user.getPassword())) {
			refreshUser(user);
			user = userCache.getUserFromCache(username);
		}

		Assert.notNull(user, "UserDetailsService returned null for username " + username + ". "
				+ "This is an interface contract violation");

		return user;
	}

	@Override
	public TokenResponse loadByAuthorizationCode(String authorizationCode) {
		// request bearer token
		final URI authUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthTokenPath()).queryParam("response_type", "token")
				.queryParam("grant_type", "authorization_code").queryParam("code", authorizationCode).build().toUri();

		final URI userInfoUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthUserInfoPath()).build().toUri();

		ResponseEntity<TokenResponse> response = null;

		try {
			response = restTemplate.exchange(authUrl, HttpMethod.GET,
					buildAuthRequest("Basic",
							securityCfgService.getOauthServiceClientId() + ":"
									+ keystoreService.getPBEKey(securityCfgService.getOauthServiceClientId())),
					TokenResponse.class);
		} catch (final IOException ioEx) {
			LOG.error("Failed to get oauth client secret.", ioEx);
			response = new ResponseEntity<TokenResponse>(new TokenResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to read client secret from keystore.", secEx);
			response = new ResponseEntity<TokenResponse>(new TokenResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			response.getBody()
					.setMessage("Failed to request access token. " + response.getStatusCode().getReasonPhrase());
		} else {
			// Request user info
			final ResponseEntity<String> infoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
					buildAuthRequest("Bearer", response.getBody().getAccess_token()), String.class);

			if (!HttpStatus.OK.equals(infoResponse.getStatusCode())) {
				response.getBody()
						.setMessage("Failed to request user info. " + response.getStatusCode().getReasonPhrase());
			} else {
				// save in cache
				userCache.putUserInCache(buildUser(response.getBody(), infoResponse.getBody()));
			}
		}

		return response.getBody();
	}

	public UserCache getUserCache() {
		return userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	protected UserDetails loadInvalidUser() {
		return new OauthUser("unknown", " ", false, false, false, false, Collections.emptyList(), null, null);
	}

	protected boolean isValidToken(String primaryToken) {
		final URI checkTokenUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthCheckTokenPath()).build().toUri();

		final ResponseEntity<String> response = restTemplate.exchange(checkTokenUrl, HttpMethod.GET,
				buildAuthRequest("Bearer", primaryToken), String.class);

		return HttpStatus.OK.equals(response.getStatusCode());
	}

	protected void refreshUser(UserDetails user) {
		loadByAuthorizationCode(((OauthUser) user).getRefreshToken());
	}

	protected UserDetails buildUser(TokenResponse tokenResponse, String userInfo) {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("user"));

		OauthUser userDetails = new OauthUser(userInfo, tokenResponse.getAccess_token(), authorities,
				tokenResponse.getRefresh_token(), tokenResponse.getId_token());

		// Munge a secondary bearer token

		try {
			tokenResponse.setAccess_token(
					nonceService.generateHash(securityCfgService.getSalt(), tokenResponse.getId_token()));

		} catch (final InvalidKeyException invalidKeyEx) {
			tokenResponse.setMessage("Failed creating secondary token. " + invalidKeyEx.getMessage());
			tokenResponse.setAccess_token(null);
			userDetails = null;
		} catch (final NoSuchAlgorithmException noSuchAlgorithmEx) {
			tokenResponse.setMessage("Failed creating secondary token. " + noSuchAlgorithmEx.getMessage());
			tokenResponse.setAccess_token(null);
			userDetails = null;
		} finally {
			tokenResponse.setId_token(null);
			tokenResponse.setRefresh_token(null);

			if (userDetails != null) {
				userDetails.setSecondaryToken(tokenResponse.getAccess_token());
			}
		}

		return userDetails;
	}

	private HttpEntity<Object> buildAuthRequest(String authType, String value) {
		HttpEntity<Object> request = null;

		try {
			final MultiValueMap<String, String> customHeaders = new LinkedMultiValueMap<String, String>();
			customHeaders
					.add("Authorization",
							authType + ": "
									+ (authType.equals("Basic")
											? nonceService.encode(value.getBytes(StandardCharsets.UTF_8.name()))
											: value));
			request = new HttpEntity<Object>(customHeaders);
		} catch (final UnsupportedEncodingException e) {
			LOG.error("Failed to buid auth request.", e);
		}

		return request;
	}
}
