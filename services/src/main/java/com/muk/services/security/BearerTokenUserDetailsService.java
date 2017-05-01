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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.ext.core.json.model.oauth.TokenResponse;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.CachingOauthUserDetailsService;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.exchange.RestConstants;

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
			user = loadAnonymousUser();
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
	public TokenResponse loadByAuthorizationCode(String authorizationCode, String redirectUri) {
		// request bearer token
		final URI authUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthTokenPath()).build().toUri();

		final URI userInfoUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthUserInfoPath()).build().toUri();

		ResponseEntity<TokenResponse> response = null;

		final MultiValueMap<String, String> postBody = new LinkedMultiValueMap<String, String>();
		postBody.put("response_type", Collections.singletonList("token"));
		postBody.put("grant_type", Collections.singletonList("authorization_code"));
		postBody.put("code", Collections.singletonList(authorizationCode));
		postBody.put("redirect_uri", Collections.singletonList(redirectUri));

		try {
			response = restTemplate.exchange(authUrl, HttpMethod.POST, buildAuthRequest("Basic",
					securityCfgService.getOauthServiceClientId() + ":"
							+ keystoreService.getPBEKey(securityCfgService.getOauthServiceClientId()),
							postBody), TokenResponse.class);
		} catch (final IOException ioEx) {
			LOG.error("Failed to get oauth client secret.", ioEx);
			response = new ResponseEntity<TokenResponse>(new TokenResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to read client secret from keystore.", secEx);
			response = new ResponseEntity<TokenResponse>(new TokenResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final HttpClientErrorException httpEx) {
			response = new ResponseEntity<TokenResponse>(new TokenResponse(), httpEx.getStatusCode());

			if (httpEx instanceof HttpStatusCodeException) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
					LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
				}
			}
		}

		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			response.getBody()
			.setMessage("Failed to request access token. " + response.getStatusCode().getReasonPhrase());
		} else {
			// Request user info
			ResponseEntity<String> infoResponse = null;

			try {
				infoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
						buildAuthRequest("Bearer", response.getBody().getAccess_token(), null), String.class);
			} catch (final HttpClientErrorException httpEx) {
				response = new ResponseEntity<TokenResponse>(new TokenResponse(), httpEx.getStatusCode());

				if (httpEx instanceof HttpStatusCodeException) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
						LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
					}
				}
			}
			if (!HttpStatus.OK.equals(infoResponse.getStatusCode())) {
				response.getBody()
				.setMessage("Failed to request user info. " + response.getStatusCode().getReasonPhrase());
			} else {
				// save in cache
				userCache.putUserInCache(buildUser(response.getBody(), infoResponse.getBody(), redirectUri));
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

	protected UserDetails loadAnonymousUser() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return new OauthUser(RestConstants.Rest.anonymousToken, " ", true, true, true, true, authorities, null, null);
	}

	protected boolean isValidToken(String primaryToken) {
		final URI checkTokenUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthCheckTokenPath()).build().toUri();

		final MultiValueMap<String, String> postBody = new LinkedMultiValueMap<String, String>();
		if (LOG.isDebugEnabled()) {
			LOG.debug("primary token on check token lookup {}", primaryToken);
		}
		postBody.put("token", Collections.singletonList(primaryToken));

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(checkTokenUrl, HttpMethod.POST, buildAuthRequest("Basic",
					securityCfgService.getOauthServiceClientId() + ":"
							+ keystoreService.getPBEKey(securityCfgService.getOauthServiceClientId()),
							postBody), String.class);

		} catch (final IOException ioEx) {
			LOG.error("Failed to get oauth client secret.", ioEx);
			response = new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to read client secret from keystore.", secEx);
			response = new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final HttpClientErrorException httpEx) {
			response = new ResponseEntity<String>("", httpEx.getStatusCode());

			if (httpEx instanceof HttpStatusCodeException) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
					LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
				}
			}
		}

		return HttpStatus.OK.equals(response == null ? "" : response.getStatusCode());
	}

	protected void refreshUser(UserDetails user) {
		loadByAuthorizationCode(((OauthUser) user).getRefreshToken(), ((OauthUser) user).getRedirectUri());
	}

	protected UserDetails buildUser(TokenResponse tokenResponse, String userInfo, String redirectUri) {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		OauthUser userDetails = new OauthUser(userInfo, tokenResponse.getAccess_token(), authorities,
				tokenResponse.getRefresh_token(), redirectUri);

		// Munge a secondary bearer token

		try {
			tokenResponse.setAccess_token(
					nonceService.generateHash(securityCfgService.getSalt(), tokenResponse.getAccess_token()));

		} catch (final InvalidKeyException invalidKeyEx) {
			tokenResponse.setMessage("Failed creating secondary token. " + invalidKeyEx.getMessage());
			tokenResponse.setAccess_token(null);
			userDetails = null;
		} catch (final NoSuchAlgorithmException noSuchAlgorithmEx) {
			tokenResponse.setMessage("Failed creating secondary token. " + noSuchAlgorithmEx.getMessage());
			tokenResponse.setAccess_token(null);
			userDetails = null;
		} finally {
			tokenResponse.setRefresh_token(null);

			if (userDetails != null) {
				userDetails.setSecondaryToken(tokenResponse.getAccess_token());
			}
		}

		return userDetails;
	}

	private HttpEntity<MultiValueMap<String, String>> buildAuthRequest(String authType, String value,
			MultiValueMap<String, String> postBody) {
		HttpEntity<MultiValueMap<String, String>> request = null;

		try {
			final MultiValueMap<String, String> customHeaders = new LinkedMultiValueMap<String, String>();
			customHeaders.add("Authorization", authType + " " + (authType.equals("Basic")
					? nonceService.encode(value.getBytes(StandardCharsets.UTF_8.name())) : value));

			if (postBody != null) {
				customHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			}

			request = new HttpEntity<MultiValueMap<String, String>>(postBody, customHeaders);

		} catch (final UnsupportedEncodingException e) {
			LOG.error("Failed to buid auth request.", e);
		}

		return request;
	}
}
